package application;

import application.model.*;
import application.model.Node;
import javafx.scene.shape.*;
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
import javax.swing.*;
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
	
	private static int width = 800;
	private static int height = 600;
	private static double zoomFactor = 1.0;
	private static final int heightButtonplatz = 70; // abstand von oben bis Nodes
	private static double offsetX = 0.0; // absoluter XOffset (verschiebt die Zeichnung auf dem Canvas)
    private static double offsetY = 0.0; // absoluter YOffset
	private Flughafen model;
    private Stage stage;
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;
    private static Group root;
    private HBox buttonHbox;
    private Button zoomButton;
    private ToggleButton nameButton= new ToggleButton("show me the Node-names");
    public final StringProperty btnText = nameButton.textProperty();
    boolean nameshown= false;
    private Label zoomLabel;
    Map<Plane, ImageView> planes = new HashMap<Plane, ImageView>();
    //pair oder tupel statt imageview wo path und imageview rein kommt damit man beim zoomen (während der animation)

    public FlughafenView(Flughafen model, Stage stage) {
        this.model = model;
        this.stage = stage; 
        root = new Group();
        this.canvas = new Canvas(width, height + heightButtonplatz);
        this.gc = canvas.getGraphicsContext2D();
        root.getChildren().addAll(canvas);
        this.setInitialZoomAndOffset(model.getNodes());
        this.scene = new Scene(root);
        this.stage.setScene(scene);
        this.stage.setTitle("Flughafen");
        this.stage.show();
        this.zoomButton= new Button("");
        Image buttonImage = new Image("/application/source/Images/zoomout.png");
        zoomButton.setGraphic(new ImageView(buttonImage));
        setButtonStyle(zoomButton);
        setButtonStyle(nameButton);
        this.buttonHbox= new HBox();
        this.setHboyStyle();
        buttonHbox.getChildren().addAll(zoomButton, nameButton);
        createZoomLabel();
        root.getChildren().addAll(buttonHbox, zoomLabel);
    }

   public Stage getStage() {
        return stage;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public void update(boolean onlyPlanes) {
        if(!onlyPlanes) drawCanvas();
        drawPlanes();
    }

    private void drawCanvas() {
        Collection<Node> nodes = model.getNodes();
        if (!nodes.isEmpty()) {
            gc.clearRect(0, 0, width, height + heightButtonplatz);
            drawNodes(new ArrayList<Node>(nodes));
            if(nameshown)	{
            	showName(nodes);
   			}
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

   /*	drawNodes() zeichnet rekursiv (damit die unten liegenden Nodes zuerst gezeichnet werden) */

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
		
		/*	setStyle: Funktionales Interface, dem man drei Argumente mitgeben kann, damit es die Farben fuer die Nodes 
			anpasst. */

        Function<Double, Function<Color, Consumer<Color>>> setStyle = width -> (strokeC -> (fillC -> {
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
            double x = node.getX() * this.zoomFactor + this.offsetX - (PlaneType.BOEING.getSize() / 2* this.zoomFactor);
            double y = node.getY() * this.zoomFactor + this.offsetY - (PlaneType.BOEING.getSize() / 2* this.zoomFactor);

            imgV.setX(x);
            imgV.setY(y);
        }

        imgV.setFitWidth(PlaneType.BOEING.getSize() * this.zoomFactor);
        imgV.setFitHeight(PlaneType.BOEING.getSize() * this.zoomFactor);

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
            maxY = maxY - (minY - 1); // maxY ist jetzt die Hoehe des Flughafens

            if (maxY * ((double) width / height) <= maxX) // passt den Flughafen in die Bildschirmma�e ein (orientiert an breite)
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
            double relX = (absoluteX - offsetX) / zoomFactor; // die relative "Model X-Koordinate", auf die der  Mauszeiger zeigt

            double relY = (absoluteY - offsetY) / zoomFactor; // ''

            offsetX = absoluteX - (relX * zoomFactorNeu); // offsetX wird genau so verschoben, dass die relative
            offsetY = absoluteY - (relY * zoomFactorNeu); // Koordinate des Mauszeigers nach dem Zoom immer noch genau
            zoomFactor = zoomFactorNeu; // an der absoluten Position ist


        }
    }

//    public double getZoomFactor() {
//        return this.zoomFactor;
//    }

//    public void setZoomFactor(double factor) {
//        this.zoomFactor = factor;
//    }

    public void resize(double width, double height) {
    	FlughafenView.width= (int) width;
    	FlughafenView.height= (int) height;
        canvas.setWidth(width);
        canvas.setHeight(height + heightButtonplatz);
        buttonHbox.setPrefWidth(width); //damit der Hbox sich an Canvas gröse anpasst
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

        MoveTo line = new MoveTo(lastNode.getX() * zoomFactor + offsetX - PlaneType.BOEING.getSize() / 2, lastNode.getY() * zoomFactor + offsetY - PlaneType.BOEING.getSize() / 2);
        LineTo line2 = new LineTo(nextNode.getX() * zoomFactor + offsetX - PlaneType.BOEING.getSize() / 2, nextNode.getY() * zoomFactor + offsetY - PlaneType.BOEING.getSize() / 2);
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

    public void showName(Collection<Node> nodes) {
        for (Node node : nodes) {
            double x = (node.getX() * zoomFactor) + offsetX;
            double y = (node.getY() * zoomFactor) + offsetY;
            this.gc.fillText(node.getName(), x + 5, y);
            nameshown=true;
        }
    }

   
    public void setHboyStyle() {
        buttonHbox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 1;" + "-fx-border-insets: 1;"
                + "-fx-border-radius: 1;" + "-fx-border-color:  #6699ff;" + "-fx-background-color:  #6699ff;");
        buttonHbox.setSpacing(20);
        buttonHbox.setAlignment(Pos.CENTER);

        buttonHbox.setPrefHeight(heightButtonplatz - 20);
        buttonHbox.setPrefWidth(width);

    }

    public void setButtonStyle(ButtonBase button) {

        button.setStyle(
                "-fx-border-color:  #66ffff; "
                        + "-fx-font-size: 10;"
                        + "-fx-border-insets: -5; "
                        + "-fx-border-radius: 5;"
                        + "-fx-border-style: dotted;"
                        + "-fx-border-width: 2;"
                        + "-fx-background-color: #ffffcc;"
        );
    }

    public void createZoomLabel() {
        this.zoomLabel = new Label();
        this.zoomLabel.setTranslateX(740);
        this.zoomLabel.setTranslateY(height);
        this.zoomLabel.setFont(Font.font("Arial", 20));
        this.zoomLabel.setStyle("-fx-background-color: thistle;"
                + "-fx-border-color: black;"
                + "-fx-background-image:url('/application/source/Images/zoomLabel.jpg');"
        );
        updateLabel();
    }

    public void updateLabel() {
        this.zoomLabel.setText((int) zoomFactor - 40 + " %");
    }

    public Label getZoomLabel() {
        return this.zoomLabel;
    }

}
