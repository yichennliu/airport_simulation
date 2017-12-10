
package application;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import application.model.Flughafen;
import application.model.Kind;
import application.model.Node;
import application.model.Plane;
import javafx.animation.Interpolator;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
public class FlughafenView {
	private static int width = 850;
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
	private Rectangle backGroundRectangle ;
	private HBox buttonHbox;
	private Label showMaxplanes = new Label();
	private Button zoomButton;
	private ToggleButton infoButton = new ToggleButton("Show Node Info");
	public 	final StringProperty btnText = infoButton.textProperty();
	private boolean showNodeInfo = false;
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
		this.colorPicker= new ColorPicker();
		setColorPikcer();
		createBgRect();
		fileChooserButton = new Button("Open File");
		this.setInitialZoomAndOffset(model.getNodes());
		this.zoomButton = new Button("buttonlabel" + zoomFactor);
		Image buttonImage = new Image("/application/source/Images/zoomout.png");
		this.zoomButton.setGraphic(new ImageView(buttonImage));
		setButtonStyle(zoomButton);
		setButtonStyle(infoButton);
		setButtonStyle(fileChooserButton);
		createZoomLabel();
		setActivePlanes();
		root.getChildren().addAll(canvas);
		buttonHbox.getChildren().addAll(showMaxplanes,zoomButton, infoButton,fileChooserButton,colorToolbar);
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
		this.update(false,false);
	}
	public Stage getStage() {
		return stage;
	}
	public Canvas getCanvas() {
		return this.canvas;
	}
	public void update(boolean onlyPlanes, boolean onlyTransitions) {
		if (!onlyPlanes)
			drawCanvas();
		if(onlyTransitions) {
			updateTransitions();
		}
		else {
			drawPlanes();
		}
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
	
	private void drawPlane(Plane plane) {
		if(registerOrDeletePlane(plane)) {
			buttonHbox.toFront();
			movePlane(plane);
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
				this.gc.setLineDashes(5);
			else
				this.gc.setLineDashes(null);
		})));
		setStyle.apply(1.0).apply(Color.DARKGREY).apply(Color.GREY).accept(false);
		switch (kind) {
		case AIR: {
			setStyle.apply(0.7).apply(Color.DODGERBLUE).apply(Color.DODGERBLUE.darker()).accept(true);
			break;
		}
		case CONCRETE: {
			setStyle.apply(1.8).apply(Color.rgb(106, 120, 135)).apply(Color.rgb(106, 120, 135,0.5)).accept(false);
			break;
		}
		case HANGAR: {
			setStyle.apply(2.8).apply(Color.rgb(75, 163, 133)).apply(Color.rgb(75, 163, 133,0.7)).accept(false);
			break;
		}
		case RUNWAY: {
			setStyle.apply(4.4).apply(Color.rgb(232, 150, 118)).apply(Color.rgb(232, 150, 118,0.6)).accept(false);
			
			break;
		}
		}
		if (showNodeInfo) {
			gc.setFont(fontSmall);
			this.gc.fillText(node.getName()+"\n"+(node.isFree(Flughafen.getTime()) ? "frei" : "belegt"), x - 40, y+10);
			
		}
		for (Node children : node.getTo()) {
			gc.strokeLine(x, y, (children.getX() * zoomFactor) + offsetX, (children.getY() * zoomFactor) + offsetY);
		}
		this.gc.fillOval(x - radius, y - radius, radius * 1.2, radius * 0.9);
	}
	private boolean registerOrDeletePlane(Plane plane) {
		ViewPlane viewPlane;
		
		// loescht ein Flugzeug, das NExtNode und LastNode == null hat.
		if (plane.getNextNode() == null && plane.getLastNode() == null) { 
			viewPlane = this.planes.get(plane);
			if (viewPlane != null)
				this.root.getChildren().remove(viewPlane.getImageview());
			this.planes.remove(plane);
			return false;
		}
		
		// fügt ein Flugzeug hinzu, falls es noch nicht inder View registriert wurde
		if (!this.planes.containsKey(plane)) {  
			viewPlane = new ViewPlane();
			ImageView imgV = viewPlane.getImageview();
			DynamicPathTransition dPT = new DynamicPathTransition(imgV,null,null,offsetX,offsetY,zoomFactor);
			dPT.setInterpolator(Interpolator.LINEAR);
			dPT.setCycleCount(1);
			dPT.setDuration(Duration.seconds(1));
			viewPlane.setDynamicPathTransition(dPT);
			this.planes.put(plane, viewPlane);
			root.getChildren().add(imgV);
		} 
		return true;
	}
	
	private void updateTransitions() {
		for(ViewPlane vp:this.planes.values()) {
			DynamicPathTransition dPT = vp.getDynamicPathTransition();
			dPT.updateZoomAndOffset(offsetX, offsetY, zoomFactor);
			updateViewPlaneSize(vp);
		}
	}
	
	private void movePlane(Plane plane) { 
		
		ViewPlane viewPlane = planes.get(plane);
		
		DynamicPathTransition pt = viewPlane.getDynamicPathTransition();
		pt.updateZoomAndOffset(offsetX, offsetY, zoomFactor);
		
		Node nextNode = plane.getNextNode();
        Node lastNode = plane.getLastNode();
        
        pt.stop();
        if (lastNode!=null) {
        	 pt.setStartEndNodes(lastNode,nextNode);
             pt.play();
        }
        setShadow(plane);
        updateViewPlaneSize(viewPlane);
	}
	
	private void updateViewPlaneSize(ViewPlane vp) {
		double planeSize = vp.getType().getSize();
		ImageView imgV = vp.getImageview();
		imgV.setFitWidth(planeSize * zoomFactor);
		imgV.setFitHeight(planeSize * zoomFactor);
	}
        
	private void setShadow(Plane plane) {
		ViewPlane vp = this.planes.get(plane);
		Node nextNode = plane.getNextNode();
		Kind kind = nextNode.getKind();
		ImageView imgV = vp.getImageview();
		
		switch (kind) {
			case AIR: {
				imgV.setEffect(new DropShadow(3,3,20, Color.rgb(120, 143, 165)));
				
				break;
			}
			case  RUNWAY: {
				imgV.setEffect(new DropShadow(2,1,6, Color.rgb(232, 150, 118,0.8)));
				
				break;
			}
			case  CONCRETE: {
				imgV.setEffect(null);
				break;
			}
			case  HANGAR: {
				imgV.setEffect(null);
				break;
			}
		}
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
	public Button getZoomOutButton() {
		return this.zoomButton;
	}
	public ToggleButton getInfoButton() {
		return this.infoButton;
	}
	public void zoomOut(Collection<Node> nodes) {
		setInitialZoomAndOffset(nodes);
		update(false,true);
	}
	public void setHboxStyle() {
		buttonHbox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;" + "-fx-border-width: 1;"
				+ "-fx-border-insets: 1;" + "-fx-border-radius: 1;" + "-fx-border-color: #6699ff;"
				+ "-fx-background-color: #627e89;");
		buttonHbox.setSpacing(20);
		buttonHbox.setAlignment(Pos.CENTER);
		buttonHbox.toFront();
		buttonHbox.setPrefHeight(heightButtonplatz - 20);
		buttonHbox.setPrefWidth(width);
	}
	public void setButtonStyle(ButtonBase button) {
		button.setStyle( "-fx-font-size: 13;" + "-fx-border-insets: -1.5; "
				+ "-fx-border-radius: 5;" +"-fx-border-width: 1 2 3 4; -fx-border-color:  transparent darkgray darkgreen  darkgray;"+ "-fx-border-style: dotted;" + "-fx-border-width: 2;"
				+ "-fx-background-color: #f6f5f3;"+"-fx-text-fill:#627e89;");
		button.setMinSize(50, 36);
		
		
		
	}
	
	public void setTextStyle(Label label) {
		label.setTextAlignment(TextAlignment.CENTER);
		label.setFont(fontBold);	
		label.setFont(fontBold);	
		label.setStyle( "-fx-border-insets: -1.5; "
				+ "-fx-border-radius: 5;" +"-fx-border-width: 1 2 3 4; -fx-border-color:  transparent darkgray darkred  darkgray;"+ "-fx-border-style: dotted;" + "-fx-border-width: 1;"
				+ "-fx-background-color: #627e89;;"+"-fx-text-fill:#e89676;");
		
		label.setMinSize(60, 40);
		
	}
	
	public void showNodeInfo(boolean show) {
		showNodeInfo = show;
		if (show) {
			this.btnText.set("Hide Node Info");
		}
		else {
			this.btnText.set("Show Node Info");
		}
		this.drawCanvas();
	}
	
	public boolean isShowNodeInfo() {
		return this.showNodeInfo;
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
	
	public void setActivePlanes() {
		int showActivePlanes= model.getActivePlanes();
		int maxPlanes= model.getMaxplanes();
		setTextStyle(showMaxplanes);
		this.showMaxplanes.setText("Active planes "+"["+showActivePlanes+" / "+maxPlanes+"]"+"  ");
	}
	
	public Label getActivePlanesLabel() {
		return this.showMaxplanes;
	}
		
	public void createBgRect() {
		this. backGroundRectangle = new Rectangle(width,height + heightButtonplatz);
		backGroundRectangle.setFill(Color.rgb(242, 242, 242));
		root.getChildren().addAll(backGroundRectangle);
		
	}
	
	public Rectangle getBgRect() {
		return backGroundRectangle;}
	
	public void setColorPikcer() {
		colorPicker.setValue(Color.ANTIQUEWHITE);
	}
	
	public ColorPicker getColorPicker() {
		return this.colorPicker;
	}
}