package application;

import application.model.*;
import application.model.Node;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;


public class FlughafenView {
	private Flughafen model;
	private Stage stage;
	private Scene scene;
	private final int heightButtonplatz= 70; // abstand von oben bis Nodes
	private Canvas canvas;
	private GraphicsContext gc;
	private int height = 600 ;
	private int width = 800;
	private double zoomFactor = 1.0;
	private double offsetX = 0.0; // absoluter XOffset (verschiebt die Zeichnung auf dem Canvas)
	private double offsetY = 0.0; // absoluter YOffset
	private Group root = new Group();
	HBox  buttonHbox= new HBox ();
	private Button zoomButton = new Button("");
	private ToggleButton nameButton = new ToggleButton("show me the Node-names");
	private Label zoomLabel;
	final StringProperty btnText = nameButton.textProperty();
	Map<Plane, ImageView> planes = new HashMap<Plane, ImageView>();
	//pair oder tupel statt imageview wo path und imageview rein kommt damit man beim zoomen (während der animation) 
	
	public FlughafenView(Flughafen model, Stage stage) {
		this.model = model;
		this.stage = stage;
		this.canvas = new Canvas(width, height+heightButtonplatz);
		this.gc = canvas.getGraphicsContext2D();
		root.getChildren().addAll(canvas);
		this.setInitialZoomAndOffset(model.getNodes());
		this.scene = new Scene(root);
		this.stage.setScene(scene);
		this.stage.setTitle("Flughafen");
		this.stage.show();
		Image buttonImage = new Image("/application/source/Images/zoomout.png");
		zoomButton.setGraphic(new ImageView(buttonImage));
		setButtonStyle(zoomButton);
		setButtonStyle(nameButton);
		this.setHboyStyle();
		buttonHbox.getChildren().addAll(zoomButton,nameButton);
		createZoomLabel();
		root.getChildren().addAll(buttonHbox, zoomLabel);
	}

	public Stage getStage() {
		return stage;
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	public void update() {
		drawCanvas();
		drawPlanes();

	}

	private void drawCanvas() {
		Collection<Node> nodes = model.getNodes();
		if (!nodes.isEmpty()) {
			gc.clearRect(0, 0, this.width, this.height+heightButtonplatz);
			drawNodes(new ArrayList<Node>(nodes));

		}
	}

	public void drawPlanes() {
		List<Plane> planes = model.getPlanes();
		if (!planes.isEmpty()) {
			for (Plane plane : planes) {
				drawPlane(plane);
			}

		}
	}

	/*	drawNodes() zeichnet rekursiv (damit die unten liegenden Nodes zuerst gezeichnet werden) */
	
	private void drawNodes(ArrayList<Node> nodes) {
		if(nodes.size()>0) {
			Node node = nodes.remove(0);
			drawNodes(nodes);
			drawNode(node);
		}
			
			
	}

	private void drawNode(Node node) {
		double radius = 5;
		double x = (node.getX() * this.zoomFactor) + offsetX;
		double y = (node.getY() * this.zoomFactor) + offsetY;
		Kind kind = node.getKind();
		
		/*	setStyle: Funktionales Interface, dem man drei Argumente mitgeben kann, damit es die Farben fuer die Nodes 
			anpasst. */
		
		Function<Double,Function<Color,Consumer<Color>>> setStyle = width -> (strokeC -> (fillC -> {
			this.gc.setLineWidth(width);
			this.gc.setStroke(strokeC);
			this.gc.setFill(fillC);
		}));

		setStyle.apply(1.0).apply(Color.DARKGREY).accept(Color.GREY);
		
		switch (kind) {
			case air: {
				setStyle.apply(1.0).apply(Color.BLUE).accept(Color.BLUE.darker());
				break;
			}
			case concrete: {
				setStyle.apply(0.3).apply(Color.grayRgb(10, 1)).accept(Color.grayRgb(10, 1));
				break;
			}
			case hangar: {
				setStyle.apply(0.6).apply(Color.DARKGREEN).accept(Color.DARKGREEN);
				break;
	
			}
			case runway: {
				setStyle.apply(0.4).apply(Color.BLACK).accept(Color.BLACK);
				break;
			}
		}
	
		for (Node children : node.getTo()) {
			gc.strokeLine(x, y, (children.getX() * zoomFactor) + offsetX, (children.getY() * zoomFactor) + offsetY);
		}

		this.gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

	}

	private void drawPlane(Plane plane) {

		if (!this.planes.containsKey(plane)) {
			ImageView imgV = PlaneType.BOEING.getImageView(); // hier spaeter PLaneType aus dem Plane holen plane.getType()

			this.planes.put(plane, imgV);
			root.getChildren().add(imgV);
		}
		ImageView imgV = this.planes.get(plane);
		Node node = plane.getNextNode();
				
		if (node != null) {
			double x = node.getX() * this.zoomFactor + this.offsetX - PlaneType.BOEING.getSize()/2;
			double y = node.getY() * this.zoomFactor + this.offsetY - PlaneType.BOEING.getSize()/2;
			imgV.setX(x);
			imgV.setY(y);
		}
		imgV.setFitWidth(PlaneType.AIRBUS.getSize() * this.zoomFactor);
		imgV.setFitHeight(PlaneType.AIRBUS.getSize() * this.zoomFactor);
	}

	private void setInitialZoomAndOffset(Collection<Node> nodes) { // setzt den initialen Faktor und Verschiebung,
																	// sodass alles auf das canvas passt;
		Iterator<Node> it = nodes.iterator();
		if (it.hasNext()) {
			double minY, minX, maxX, maxY, widthFlughafen, heightFlughafen;
			Node firstNode = it.next();
			minX = firstNode.getX();
			minY = firstNode.getY();
			maxY = minY;
			maxX = minX;
			while (it.hasNext()) {
				Node currentNode = it.next();
				double currentX = currentNode.getX();
				double currentY = currentNode.getY();
				if (currentX < minX) minX = currentX;
				if (currentY < minY) minY = currentY;
				if (currentX > maxX) maxX = currentX;
				if (currentY > maxY) maxY = currentY;
			}
			maxX = maxX - minX; // maxX ist jetzt die breite des Flughafens (!)
			maxY = maxY - (minY-1); // maxY ist jetzt die Hoehe des Flughafens

			if (maxY * ((double) this.width / this.height) <= maxX) // passt den Flughafen in die Bildschirmma�e ein (orientiert an breite)
				this.zoomFactor = this.width / maxX;
			else
				this.zoomFactor = this.height / maxY;

			widthFlughafen = maxX * this.zoomFactor; // absolute Breite des Flughafens (die relative steht ja schon in maxX)											
			heightFlughafen = maxY * this.zoomFactor;

			this.offsetX = (0 - minX * this.zoomFactor) + (this.width - (widthFlughafen)) * 0.5; // horizontalAlign des Flughafens																					
			this.offsetY = (heightButtonplatz - minY * this.zoomFactor) + ((this.height) - (heightFlughafen)) * 0.5; // verticalAlign
		}
	}

	public void zoomTo(double deltaY, double absoluteX, double absoluteY, double zoomAmount) {
		if (deltaY < 0)
			zoomAmount = -zoomAmount;
		double zoomFactorNeu = zoomAmount + this.zoomFactor;
		if (zoomFactorNeu > 0) {
			double relX = (absoluteX - this.offsetX) / this.zoomFactor; // die relative "Model X-Koordinate", auf die der  Mauszeiger zeigt
																	
			double relY = (absoluteY - this.offsetY) / this.zoomFactor; // ''

			this.offsetX = absoluteX - (relX * zoomFactorNeu); // offsetX wird genau so verschoben, dass die relative
			this.offsetY = absoluteY - (relY * zoomFactorNeu); // Koordinate des Mauszeigers nach dem Zoom immer noch genau														
			this.zoomFactor = zoomFactorNeu; // an der absoluten Position ist
			

		}
	}

	public double getZoomFactor() {
		return this.zoomFactor;
	}

	public void setZoomFactor(double factor) {
		this.zoomFactor = factor;
	}

	public void resize(double width, double height) {
		this.width = (int) width;
		this.height = (int) height;
		canvas.setWidth(width);
		canvas.setHeight(height+heightButtonplatz);
		buttonHbox.setPrefWidth(this.width); //damit der Hbox sich an Canvas gröse anpasst
		this.drawCanvas();
	}

	public void setOffsetX(double offsetX) {
		this.offsetX = offsetX;
	}

	public void setOffsetY(double offsetY) {
		this.offsetY = offsetY;
	}

	public double getOffsetX() {
		return this.offsetX;
	}

	public double getOffsetY() {
		return this.offsetY;
	}

	private void flugtest() {
		Image image = new Image("/application/source/Images/flugzeugrechts.png");
		ImageView imageV = new ImageView(image);
		root.getChildren().add(imageV);
		imageV.setFitWidth(2 * this.zoomFactor);
		imageV.setFitHeight(2 * this.zoomFactor);
		PathTransition pt = new PathTransition();
		Path path = new Path();

		MoveTo moveTo = new MoveTo();
		moveTo.setX(0);
		moveTo.setY(0);

		ArcTo arcTo = new ArcTo();
		arcTo.setX(400); // center - radiusX-1
		arcTo.setY(300); // center - radiusY
		arcTo.setRadiusX(50); // sweepFlag auf false, largeFlag auf true
		arcTo.setRadiusY(50); // setXAxisRotation(rotate)
		arcTo.setSweepFlag(false);
		arcTo.setLargeArcFlag(true);

		path.getElements().add(moveTo);
		path.getElements().add(arcTo);

		pt.setDuration(Duration.millis(2000));
		pt.setCycleCount(Animation.INDEFINITE);
		pt.setPath(path);
		pt.setNode(imageV);
		pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);

		ParallelTransition pl = new ParallelTransition(imageV, pt);
		pl.play();

	}

	public Button getZoomOutButton() {
		return this.zoomButton;
	}

	public ToggleButton getNameButton() {
		return this.nameButton;
	}
	
	public void zoomOut(Collection<Node> nodes) {
		setInitialZoomAndOffset(nodes);
		update();
	}
	
	public void showName(Collection<Node> nodes) {
		for (Node node : nodes) {
			double x = (node.getX() * this.zoomFactor) + offsetX;
			double y = (node.getY() * this.zoomFactor) + offsetY;
		this.gc.fillText(node.getName(), x + 5, y);
	}}
	
	public void hideName(Collection<Node> nodes) {
		for (Node node : nodes) {
		double x = (node.getX() * this.zoomFactor) + offsetX;
		double y = (node.getY() * this.zoomFactor) + offsetY;
		this.gc.fillText(" ", x + 5, y);
	}}
	
	public void setHboyStyle() {
		buttonHbox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
		        + "-fx-border-width: 1;" + "-fx-border-insets: 1;"
		        + "-fx-border-radius: 1;" + "-fx-border-color:  #6699ff;"+"-fx-background-color:  #6699ff;");
		buttonHbox.setSpacing(20);
		buttonHbox.setAlignment(Pos.CENTER);
		 
		buttonHbox.setPrefHeight(heightButtonplatz-20);
		buttonHbox.setPrefWidth(this.width);
		
	}
	public void setButtonStyle(ButtonBase button) {
		
		button.setStyle(
			    "-fx-border-color:  #66ffff; "
			    + "-fx-font-size: 10;"
			    + "-fx-border-insets: -5; "
			    + "-fx-border-radius: 5;"
			    + "-fx-border-style: dotted;"
			    + "-fx-border-width: 2;"
			    +"-fx-background-color: #ffffcc;"
			);
	}
	
	public void createZoomLabel(){
		this.zoomLabel = new Label();
		this.zoomLabel.setTranslateX(740);
		this.zoomLabel.setTranslateY(this.height);
		this.zoomLabel.setFont(Font.font("Arial",20));
		this.zoomLabel.setStyle("-fx-background-color: thistle;"
					+"-fx-border-color: black;"
					+"-fx-background-image:url('/application/source/Images/zoomLabel.jpg');"
					);
		updateLabel();
	}
	
	public void updateLabel() {
		this.zoomLabel.setText((int) this.zoomFactor - 40 + " %");
	}
	
	public Label getZoomLabel(){
		return this.zoomLabel;
	}

}
