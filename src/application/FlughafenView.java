package application;

import application.model.*;
import application.model.Node;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
	StackPane layout = new StackPane();
	ArrayList<Image> flugzeugBilder = new ArrayList<Image>();

	public FlughafenView(Flughafen model, Stage stage) {
		this.model = model;
		this.stage = stage;
		this.canvas = new Canvas(width, height);
		this.gc = canvas.getGraphicsContext2D();
		root.getChildren().addAll(canvas, layout);
		this.setInitialZoomAndOffset(model.getNodes());

		flugzeugBilder.add(new Image("/application/source/Images/flugzeugrechts.png"));

		this.scene = new Scene(root);
		this.stage.setScene(scene);
		this.stage.setTitle("Flughafen");
		this.stage.show();
		flugtest();
	}

	public Stage getStage() {
		return stage;
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	public void drawCanvas() {
		gc.clearRect(0, 0, this.width, this.height);
		Collection<Node> nodes = model.getNodes();
		List<Plane> planes = model.getPlanes();
		drawNodes(nodes);
	 // flugzeugBild();
		flugzeugBildaida();

//		flugzeugBildaida();
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

			if (maxY * ((double) this.width / this.height) <= maxX) { // passt den Flughafen in die Bildschirmmasse ein
																		// (orientiert an breite)
				this.zoomFactor = this.width / maxX;
			} else
				this.zoomFactor = this.height / maxY;

			widthFlughafen = maxX * this.zoomFactor; // absolute Breite des Flughafens (die relative steht ja schon im
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
		double relX = (absoluteX - this.offsetX) / this.zoomFactor; // die relative "Model X-Koordinate", auf die der
																	// Mauszeiger zeigt
		double relY = (absoluteY - this.offsetY) / this.zoomFactor; // ''

		this.offsetX = absoluteX - (relX * zoomFactorNeu); // offsetX wird genau so verschoben, dass die relative
															// Koordinate des Mauszeigers nach dem Zoom immer noch genau
															// an der absoluten Position ist
		this.offsetY = absoluteY - (relY * zoomFactorNeu);
		this.zoomFactor = zoomFactorNeu;
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

//	 public void flugzeugBild() {
//	 /* wenn ein Planeobjekt hier �bergeben werden w�rde und man wei�, von welchem
//	 zu welchem Node das Flugzeug gerade fliegt (A zu B),
//	 * l�sst sich der Drehwinkel folgenderma�en berechnen:
//	 * Ax = x-Koordinate Punkt A, Ay = y-Koordinate von Punkt A, Bx = ....
//	 * double winkel = Math.aSin( (By - Ay) / Math.sqrt(Math.pow( (Bx-Ax),2) ) +
//	 Math.pow( (By-Ay),2 ) ));
//	 */
//	 double breite = 2;
//	 double hoehe = 2;
//	 double x =3*this.zoomFactor+this.offsetX; // bei rotation muesste hier
//	// breite/2 und
//	 double y = 3*this.zoomFactor+this.offsetY;// hier hoehe/2 gerechnet werden
//	 this.gc.translate(x, y);
//	 this.gc.rotate(90);
//	 this.gc.drawImage(flugzeugBilder.get(0) ,
//	 0,0,
//	 breite*this.zoomFactor,
//	 hoehe*this.zoomFactor);
//	 this.gc.rotate(-90);
//	 this.gc.translate(-x,-y);
//	 }

	private void flugtest() {
		ImageView imageV = new ImageView(flugzeugBilder.get(0));
		root.getChildren().add(imageV);
		imageV.setFitWidth(5 * this.zoomFactor);
		imageV.setFitHeight(5 * this.zoomFactor );
		Path path = new Path();
		path.getElements().add(new MoveTo(0,0));
		path.getElements().add(new LineTo(canvas.getWidth(),canvas.getHeight()));
		path.getElements().add(new LineTo(0,0));
		PathTransition pt = new PathTransition();
		pt.setDuration(Duration.millis(2000));
		pt.setCycleCount(Timeline.INDEFINITE);
		pt.setPath(path);
		pt.setNode(imageV);
		pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
		pt.play();
	}
	
//	public void flugzeugBildaida() {
//		// okay - damit sprengst du den speicher - immer wenn gezoomt wird, etc., wird diese Funktion aufgerufen - das hat zur Folge, dass
//		// immer ein neues Image + ImageView erstellt wird, die dann angezeigt werden muessen. Oben muss als private Variable eine List rein, die die Planes enthaelt. 
//		// und nur alle Flugzeuge, die da drin sind, werden gemalt (!) Bei mir ist nach 10 sekunden scrollen der Speicher voll ;) 
//		double breite = 5;
//		double hoehe = 5;
//		double x = 3 * this.zoomFactor + this.offsetX; // bei rotation muesste hier breite/2 und
//		double y = 3 * this.zoomFactor + this.offsetY;// hier hoehe/2 gerechnet werden
//		Image image = new Image("/application/source/Images/flugzeugrechts.png");
//		ImageView iv1 = new ImageView();
//		iv1.setImage(image);
//		iv1.setFitWidth(breite * this.zoomFactor);
//		iv1.setFitHeight(hoehe * this.zoomFactor );
//        iv1.setPreserveRatio(true);
//        iv1.setSmooth(true);
//        Rectangle2D viewportRect = new Rectangle2D(331, 3335, 0, 10);
//        iv1.setViewport(viewportRect);
//     	iv1.setRotate(90);
//		layout.getChildren().add(iv1);
//	
//		
//		
//	}

	
	// funktion get beide nodes f�r plane IN PLANE --> dann winkle zwischen a und b in view berechnen 

	//
	//
	 public void flugzeugBildaida() {
	 double breite = 5;
	 double hoehe = 5;
	 double x = 3 * this.zoomFactor + this.offsetX; // bei rotation muesste hier breite/2 und
	 double y = 3 * this.zoomFactor + this.offsetY;// hier hoehe/2 gerechnet werden
	 Image image = new Image("/application/source/Images/flugzeugrechts.png");
	 ImageView iv1 = new ImageView();
	 iv1.setImage(image);
	 iv1.setFitWidth(breite * this.zoomFactor);
	 iv1.setFitHeight(hoehe * this.zoomFactor );
	 iv1.autosize();
	 iv1.setPreserveRatio(true);
	 iv1.setSmooth(true);
	 // Rectangle2D viewportRect = new Rectangle2D(35, 35, 0, 10);
	 // iv1.setViewport(viewportRect);
	 iv1.setRotate(190);
	 layout.getChildren().add(iv1);
	 iv1.setFitWidth(breite * this.zoomFactor);
	 iv1.setFitHeight(hoehe * this.zoomFactor );
	
	
	 }
//
//	public void flugzeugBildaida() {
//
//		double startX = 600;
//		double endX = 500;
//		double startY = 400;
//		double endY = 350;
//
//		DropShadow ds = new DropShadow();
//		ds.setOffsetY(3.0);
//		ds.setColor(Color.color(0.4, 0.4, 0.4));
//
//		Group rootzeug = new Group();
//		ColorPicker picker = new ColorPicker();
//
//		Rectangle rect = new Rectangle(100, 180, 500, 40);
//		// rect.setWidth(110);
//		// rect.setHeight(100);
//		rect.fillProperty().bind(picker.valueProperty());
//		rect.setArcHeight(15);
//		rect.setArcWidth(55);
//		rect.setEffect(ds);
//
//		Rectangle rectflugel = new Rectangle(450, 0, 40, 400);
//		// rect.setWidth(110);
//		// rect.setHeight(100);
//		rectflugel.fillProperty().bind(picker.valueProperty());
//		rectflugel.setArcHeight(15);
//		rectflugel.setArcWidth(385);
//		rectflugel.setEffect(ds);
//
//		root.getChildren().addAll(picker, rect, rectflugel);
//
//	}
//
//	// funktion get beide nodes für plane IN PLANE --> dann winkle zwischen a und b
//	// in view berechnen
//	//

}