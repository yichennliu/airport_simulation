package application;

import application.model.*;
import application.model.Node;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
	ArrayList<Image> flugzeugBilder = new ArrayList<Image>();
	
	public FlughafenView(Flughafen model, Stage stage) {
		this.model = model;
		this.stage = stage;
		this.canvas = new Canvas(width, height);
		this.gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);
		this.setInitialZoomAndOffset(model.getNodes());
		root.setStyle("-fx-border-color:red"); ///////////////////A: um border zu sehen 
		this.scene = new Scene(root);
		this.stage.setScene(scene);
		this.stage.setTitle("Flughafen");
		this.stage.show();
		
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
		flugzeugBild();
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

	public void flugzeugBild() {

			double breite =60.5; 
			double hoehe=60.5;
		flugzeugBilder.add(new Image("/application/source/Images/flugzeugrechts.png"));
		flugzeugBilder.add(new Image("/application/source/Images/flugzeugYamailinks.png"));
		flugzeugBilder.add(new Image("/application/source/Images/flugzeugYamaioben.png"));
		flugzeugBilder.add(new Image("/application/source/Images/flugzeugYamaiunten.png"));

		Image flugzeugrechts = flugzeugBilder.get(0);
		Image flugzeugYamailinks= flugzeugBilder.get(1);
		Image flugzeugYamaioben = flugzeugBilder.get(2);
		Image flugzeugYamaiunten = flugzeugBilder.get(3);
		
		/*Image image = new Image("/application/source/Images/flugzeugrechts.png",60,60,false, false);
		ImageView imageView = new ImageView(image);*/
		//root.getChildren().addAll(new ImageView(flugzeugYamaiunten ),new ImageView(flugzeugYamailinks));
		
		
		this.gc.drawImage(flugzeugYamaiunten ,  1*this.zoomFactor+this.offsetY, 1*this.zoomFactor+this.offsetY,
				breite,
				hoehe);
		
		/** also: das klappt doch schon mal ganz gut ;) Die Sache ist, dass alles andere �ber das Canvas generiert wird.
		das bedeutet, dass alles �ber die Funktionen den GraphicsContext, der hier als this.gc ansprechbar ist, gemalt wird.
		Und Konkret hei�t das: Alles, was gemalt wird, wird �ber die Funktionen von this.gc gemalt, f�r Bilder gibt es die Funktion
		
		Image planeImage = new Image("/application/source/Images/flugzeugrechts.png");
		this.gc.drawImage(planeImage,
						  x*this.zoomFactor+this.offsetY,   (
						  y*this.zoomFactor+this.offsetY,
						  <breite>,
						  <hoehe>);
		
		schau mal ob es so klappt! Achso: das Bild w�rde ich gleihc am anfang, wenn der Konstruktor aufgerufen wird,
		in eine ArrayList<Image> laden, dann muss es incht in jedem Rechenschritt neu geladen werden
		**/
	
	}

}