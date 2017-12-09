package application;

import application.model.*;
import application.model.Node;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class FlughafenView {

	private static int width = 800;
	private static int height = 600;
	private static double zoomFactor = 1.0;
	private static final int heightButtonplatz = 70; // abstand von oben bis Nodes
	private static double offsetX = 0.0; // absoluter XOffset (verschiebt die Zeichnung auf dem Canvas)
	private static double offsetY = 0.0; // absoluter YOffset
	private Flughafen model;
	private Stage stage;
	private Scene scene;
	private static Group root;
	private Canvas canvas;
	private GraphicsContext gc;
	private 	Rectangle backGroundRectangle ;
	private HBox buttonHbox;
	private Label showMaxplanes = new Label();
	private Button zoomButton;
	private ToggleButton nameButton = new ToggleButton("Show Node names");
	public 	final StringProperty btnText = nameButton.textProperty();
	boolean nameshown = false;
	private Font fontSmall = Font.font("Droid Sans", FontWeight.EXTRA_LIGHT, 10);
	private Font fontBold = Font.font("Droid Sans", FontWeight.EXTRA_BOLD, 18);
	private Button fileChooserButton;
	private ToolBar colorToolbar = new ToolBar();
	private final ColorPicker colorPicker ;

	
   
	private Label zoomLabel;
	Map<Plane, ViewPlane> planes = new HashMap<Plane, ViewPlane>();

	public FlughafenView(Flughafen model, Stage stage) {
		this.model = model;
		this.stage = stage;
		root = new Group();
		this.buttonHbox = new HBox();
		this.setHboxStyle();
		this.canvas = new Canvas(width, height + heightButtonplatz);
		this.gc = canvas.getGraphicsContext2D();
		this. backGroundRectangle = new Rectangle(width,height + heightButtonplatz);
		this.colorPicker= new ColorPicker();
		colorPicker.setValue(Color.ANTIQUEWHITE);
		backGroundRectangle.setFill(Color.ANTIQUEWHITE);
		 colorPicker.setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	                backGroundRectangle.setFill(colorPicker.getValue()); // muss in controler
	            }
	        });
		 fileChooserButton = new Button("Open File");
		this.setInitialZoomAndOffset(model.getNodes());
		this.zoomButton = new Button("buttonlabel" + this.zoomFactor);
		Image buttonImage = new Image("/application/source/Images/zoomout.png");
		this.zoomButton.setGraphic(new ImageView(buttonImage));
		setButtonStyle(zoomButton);
		setButtonStyle(nameButton);
		setButtonStyle(fileChooserButton);
		createZoomLabel();
		setActivePlanes();
		root.getChildren().addAll(backGroundRectangle);
		root.getChildren().addAll(canvas);
		buttonHbox.getChildren().addAll(showMaxplanes,zoomButton, nameButton,fileChooserButton,colorToolbar);
		root.getChildren().addAll(buttonHbox);
		colorToolbar.getItems().addAll(colorPicker);
		this.scene = new Scene(root);
		this.stage.setScene(scene);
		this.stage.setTitle("Flughafen");
		this.stage.show();

	}

	public void reset(Flughafen model) {
		// alle ImageViews der Planes aus der View loeschen
		for (ViewPlane vp : this.planes.values()) {
			this.root.getChildren().remove(vp.getImageview());
		}
		// Planes-Hashmap resetten
		this.planes = new HashMap<Plane, ViewPlane>();
		// das neue Model setzen
		this.model = model;
		// alles neu zeichnen
		this.setInitialZoomAndOffset(this.model.getNodes());
		this.update(false);
	}

	public Stage getStage() {
		return stage;
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	public void update(boolean onlyPlanes) {
		if (!onlyPlanes)
			drawCanvas();
		drawPlanes();
		setActivePlanes();
	}

	private void drawCanvas() {
		Collection<Node> nodes = model.getNodes();
		if (!nodes.isEmpty()) {
			gc.clearRect(0, 0, width, height + heightButtonplatz);
			drawNodes(new ArrayList<Node>(nodes));
		}
	}

	public void drawPlanes() {
		Collection<Plane> planes = model.getPlanes();
		if (!planes.isEmpty()) {
			for (Plane plane : planes) {
				drawPlane(plane);
			}
		}
	}

	/*
	 * drawNodes() zeichnet rekursiv (damit die unten liegenden Nodes zuerst
	 * gezeichnet werden)
	 */

	private void drawNodes(ArrayList<Node> nodes) {
		if (nodes.size() > 0) {
			Node node = nodes.remove(0);
			drawNodes(nodes);
			drawNode(node);
		}

	}

	private void drawNode(Node node) {
		double radius = 5;
		double x = (node.getX() * zoomFactor) + offsetX;
		double y = (node.getY() * zoomFactor) + offsetY;
		Kind kind = node.getKind();

		/*
		 * setStyle: Funktionales Interface, dem man vier Argumente mitgeben kann, damit
		 * es die Farben fuer die Nodes anpasst.
		 */

		Function<Double, Function<Color, Function<Color, Consumer<Boolean>>>> setStyle = width -> (strokeC -> (fillC -> (dotted -> {
			this.gc.setLineWidth(width);
			this.gc.setStroke(strokeC);
			this.gc.setFill(fillC);
			if (dotted)
				this.gc.setLineDashes(10);
			else
				this.gc.setLineDashes(null);
		})));

		setStyle.apply(1.0).apply(Color.DARKGREY).apply(Color.GREY).accept(false);

		switch (kind) {
		case air: {
			setStyle.apply(1.0).apply(Color.BLUE).apply(Color.BLUE.darker()).accept(true);
			break;
		}
		case concrete: {
			setStyle.apply(0.3).apply(Color.grayRgb(10, 1)).apply(Color.grayRgb(10, 1)).accept(false);
			break;
		}
		case hangar: {
			setStyle.apply(2.6).apply(Color.rgb(0, 179, 0)).apply(Color.rgb(0, 179, 0)).accept(false);
			break;

		}
		case runway: {
			setStyle.apply(1.4).apply(Color.BLACK).apply(Color.BLACK).accept(false);
			break;
		}
		}
		if (nameshown) {
			gc.setFont(fontSmall);
			this.gc.fillText(node.getName(), x - 40, y+10);
			
		}
		for (Node children : node.getTo()) {
			gc.strokeLine(x, y, (children.getX() * zoomFactor) + offsetX, (children.getY() * zoomFactor) + offsetY);
		}

		this.gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

	}

	private void drawPlane(Plane plane) { 
		buttonHbox.toFront();
		
		
		if (plane.getNextNode() == null && plane.getLastNode() == null) {
			ViewPlane vp = this.planes.get(plane);
			if (vp != null)
				this.root.getChildren().remove(vp.getImageview());
			this.planes.remove(plane);
			return;
		}
		ViewPlane viewPlane;
		if (!this.planes.containsKey(plane)) {
			viewPlane = new ViewPlane();
			this.planes.put(plane, viewPlane);
			root.getChildren().add(viewPlane.getImageview());
		} else
			viewPlane = planes.get(plane);
		ImageView imgV = viewPlane.getImageview();
		double planeSize = viewPlane.getType().getSize();

		Node nextNode = plane.getNextNode();
        Node lastNode = plane.getLastNode();

        if (lastNode!=null) {
            double x1 = lastNode.getX();
            double y1 = lastNode.getY();
            double x2 = nextNode.getX();
            double y2 = nextNode.getY();
            
            Path path = viewPlane.getPath();
            if(x1==x2 && y1==y2){
            	imgV.setX(x1); // noch mit offset und so weiter
            	imgV.setY(y1);
            	return;
            }
            MoveTo moveTo = new MoveTo(x1,y1);
            LineTo lineTo = new LineTo(x2,y2);
            path.setTranslateX((x1*this.zoomFactor+offsetX));
            path.setTranslateY((y1*this.zoomFactor+offsetY));
            path.setScaleX(this.zoomFactor);
            path.setScaleY(this.zoomFactor);
            
            path.getElements().clear();
            
            path.getElements().add(moveTo);
            path.getElements().add(lineTo);
            
            PathTransition pt = new PathTransition();
            pt.setNode(imgV);
            pt.setPath(path);
            pt.setCycleCount(1);
            pt.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
            pt.setDuration(Duration.seconds(1));
            pt.play();
            
        }

		imgV.setFitWidth(planeSize * this.zoomFactor);
		imgV.setFitHeight(planeSize * this.zoomFactor);

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
				if (currentX < minX)
					minX = currentX;
				if (currentY < minY)
					minY = currentY;
				if (currentX > maxX)
					maxX = currentX;
				if (currentY > maxY)
					maxY = currentY;
			}
			maxX = maxX - minX; // maxX ist jetzt die breite des Flughafens (!)
			maxY = maxY - (minY - 1); // maxY ist jetzt die Hoehe des Flughafens

			if (maxY * ((double) width / height) <= maxX) // passt den Flughafen in die Bildschirmma�e ein (orientiert
															// an breite)
				zoomFactor = width / maxX;
			else
				zoomFactor = height / maxY;

			widthFlughafen = maxX * zoomFactor; // absolute Breite des Flughafens (die relative steht ja schon in maxX)
			heightFlughafen = maxY * zoomFactor;

			offsetX = (0 - minX * zoomFactor) + (width - (widthFlughafen)) * 0.5; // horizontalAlign des Flughafens
			offsetY = (heightButtonplatz - minY * zoomFactor) + ((height) - (heightFlughafen)) * 0.5; // verticalAlign
		}
	}

	public void zoomTo(double deltaY, double absoluteX, double absoluteY, double zoomAmount) {
		if (deltaY < 0)
			zoomAmount = -zoomAmount;
		double zoomFactorNeu = zoomAmount + zoomFactor;
		if (zoomFactorNeu > 0) {
			double relX = (absoluteX - offsetX) / zoomFactor; // die relative "Model X-Koordinate", auf die der
																// Mauszeiger zeigt

			double relY = (absoluteY - offsetY) / zoomFactor; // ''

			offsetX = absoluteX - (relX * zoomFactorNeu); // offsetX wird genau so verschoben, dass die relative
			offsetY = absoluteY - (relY * zoomFactorNeu); // Koordinate des Mauszeigers nach dem Zoom immer noch genau
			zoomFactor = zoomFactorNeu; // an der absoluten Position ist

		}
	}

	public void resize(double width, double height) {
		FlughafenView.width = (int) width;
		FlughafenView.height = (int) height;
		canvas.setWidth(width);
		canvas.setHeight(height + heightButtonplatz);
		buttonHbox.setPrefWidth(width);
		backGroundRectangle.widthProperty().bind(canvas.widthProperty());
		backGroundRectangle.heightProperty().bind(canvas.widthProperty());
		this.drawCanvas();
		

	}

	public static void setOffsetX(double newOffsetX) {
		offsetX = newOffsetX;
	}

	public static void setOffsetY(double newOffsetY) {
		offsetY = newOffsetY;
	}

	public static double getOffsetX() {
		return offsetX;
	}

	public static double getOffsetY() {
		return offsetY;
	}

	private Path getPathFromPlane(Plane plane) {
		Path resultPath = new Path();
		Node lastNode = plane.getLastNode();
		Node nextNode = plane.getNextNode();
		if (lastNode == null) {
			lastNode = this.model.getNode(plane.getWaypoints().get(0).toString());
			nextNode = this.model.getNode("air6");
		}
		ViewPlane viewPlane = new ViewPlane();
		double planeSize = viewPlane.getType().getSize();
		MoveTo line = new MoveTo(lastNode.getX() * zoomFactor + offsetX - planeSize / 2,
				lastNode.getY() * zoomFactor + offsetY - planeSize / 2);
		LineTo line2 = new LineTo(nextNode.getX() * zoomFactor + offsetX - planeSize / 2,
				nextNode.getY() * zoomFactor + offsetY - planeSize / 2);
		resultPath.getElements().add(line);
		resultPath.getElements().add(line2);
		return resultPath;
	}

	public Button getZoomOutButton() {
		return this.zoomButton;
	}

	public ToggleButton getNameButton() {
		return this.nameButton;
	}

	public void zoomOut(Collection<Node> nodes) {
		setInitialZoomAndOffset(nodes);
		update(false);
	}

	public void setHboxStyle() {
		buttonHbox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 1;"
				+ "-fx-border-insets: 1;" + "-fx-border-radius: 1;" + "-fx-border-color:  #6699ff;"
				+ "-fx-background-color:  #627e89;");
		buttonHbox.setSpacing(20);
		buttonHbox.setAlignment(Pos.CENTER);
		buttonHbox.toFront();
		buttonHbox.setPrefHeight(heightButtonplatz - 20);
		buttonHbox.setPrefWidth(width);

	}

	public void setButtonStyle(ButtonBase button) {

		button.setStyle("-fx-border-color:  #66ffff; " + "-fx-font-size: 10;" + "-fx-border-insets: -5; "
				+ "-fx-border-radius: 5;" + "-fx-border-style: dotted;" + "-fx-border-width: 2;"
				+ "-fx-background-color: #ffffcc;");
	}

	
	
	public void setTextStyle(Label label) {
		
		label.setTextAlignment(TextAlignment.CENTER);
		label.setFont(fontBold);
		label.setTextFill(Color.WHITE);
		
		
	}
	
	public void showNames(boolean show) {
		nameshown = show;

		if (show) {
			this.btnText.set("Hide Node names");
		}

		else {
			this.btnText.set("Show Node names");
		}
		this.drawCanvas();
	}

	public void createZoomLabel() {
		this.zoomLabel = new Label();
		updateLabel();
	}

	public void updateLabel() {
		this.zoomButton.setText("Zoom-Factor : " + Math.round(zoomFactor * 100 / 100));
	}

	public Label getZoomLabel() {
		return this.zoomLabel;
	}

	public Button getfileChooserButton() {
		return fileChooserButton;

	}
	

	
	
//	public Label test() {
//		int a= 9; 
//		Label test =  new Label(); 
//		test.setText("show me the test int "+a);
//		return test;
//		
//	}
	
	public void setActivePlanes() {
		int showActivePlanes= model.getActivePlanes();
		int maxPlanes= model.getMaxplanes();
		this.showMaxplanes.setText("Active planes "+showActivePlanes+" / "+maxPlanes);
	}
	
	public Label getActivePlanesLabel() {
		return this.showMaxplanes;
	}
	


}
