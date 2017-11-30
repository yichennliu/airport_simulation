package application;

import application.model.*;
import application.model.Node;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

public class FlughafenView {
	private Flughafen model;
	private Stage stage;
	private Scene scene;

	private Canvas canvas;
	private GraphicsContext gc;
	private int height = 600;
	private int width = 800;

	private double zoomFactor = 1.0;
	private double offsetX = 0.0; // absoluter XOffset (verschiebt die Zeichnung auf dem Canvas)
	private double offsetY = 0.0; // absoluter YOffset
	Group root = new Group();
	private Button zoomButton = new Button("");
	 ToggleButton nameButton = new ToggleButton("show me the Node-names");
	 final StringProperty btnText = nameButton.textProperty();

	Map<Plane, ImageView> planes = new HashMap<Plane, ImageView>();
//pair oder tupel statt imageview wo path und imageview rein kommt damit man beim zoomen (während der animation) 
	
	public FlughafenView(Flughafen model, Stage stage) {
		this.model = model;
		this.stage = stage;
		this.canvas = new Canvas(width, height);
		this.gc = canvas.getGraphicsContext2D();
		root.getChildren().addAll(canvas);
		this.setInitialZoomAndOffset(model.getNodes());
		this.scene = new Scene(root);
		this.stage.setScene(scene);
		this.stage.setTitle("Flughafen");
		this.stage.show();
		this.flugtest();
		Image buttonImage = new Image("/application/source/Images/zoomout.png");
		zoomButton.setGraphic(new ImageView(buttonImage));
		root.getChildren().addAll(zoomButton,nameButton);
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
			gc.clearRect(0, 0, this.width, this.height);
			drawNodes(nodes);

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

	private void drawNodes(Collection<Node> nodes) {
		for (Node node : nodes)
			drawNode(node);
			
	}

	private void drawNode(Node node) {
		double radius = 5;
		double x = (node.getX() * this.zoomFactor) + offsetX;
		double y = (node.getY() * this.zoomFactor) + offsetY;
		Kind kind = node.getKind();
		this.gc.setLineWidth(1.0);
		this.gc.setStroke(Color.DARKGREY);
		this.gc.setFill(Color.GREY);
		switch (kind) {
		case air: {
			this.gc.setStroke(Color.BLUE);
			this.gc.setLineWidth(0.2);
			this.gc.setFill(Color.BLUE.darker());
			break;
		}

		case concrete: {
			this.gc.setStroke(Color.grayRgb(10, 1));
			this.gc.setLineWidth(0.3);
			this.gc.setFill(Color.grayRgb(10, 1));
			break;

		}

		case hangar: {
			this.gc.setStroke(Color.DARKGREEN);
			this.gc.setLineWidth(0.6);
			this.gc.setFill(Color.DARKGREEN);
			break;

		}
		case runway: {
			this.gc.setStroke(Color.BLACK);
			this.gc.setLineWidth(0.4);
			this.gc.setFill(Color.BLACK);
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
			double x = node.getX() * this.zoomFactor + this.offsetX - 1;
			double y = node.getY() * this.zoomFactor + this.offsetY - 1;
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
			maxY = maxY - minY; // maxY ist jetzt die Hoehe des Flughafens

			if (maxY * ((double) this.width / this.height) <= maxX) // passt den Flughafen in die Bildschirmmasse ein //
																	// (orientiert an breite)
				this.zoomFactor = this.width / maxX;
			else
				this.zoomFactor = this.height / maxY;

			widthFlughafen = maxX * this.zoomFactor; // absolute Breite des Flughafens (die relative steht ja schon in
														// maxX)
			heightFlughafen = maxY * this.zoomFactor;

			this.offsetX = (0 - minX * this.zoomFactor) + (this.width - (widthFlughafen)) * 0.5; // horizontalAlign des
																									// Flughafens
			this.offsetY = (0 - minY * this.zoomFactor) + (this.height - (heightFlughafen)) * 0.5; // verticalAlign
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
		canvas.setHeight(height);
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
		zoomButton.setStyle(
			    "-fx-border-color: lightblue; "
			    + "-fx-font-size: 20;"
			    + "-fx-border-insets: -5; "
			    + "-fx-border-radius: 5;"
			    + "-fx-border-style: dotted;"
			    + "-fx-border-width: 2;"
			);

		return this.zoomButton;

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
	
	

}
