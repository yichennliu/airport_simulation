package application;

import application.model.*;
import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;

public class FlughafenController {
	private FlughafenView view;
	private Flughafen model;
	private double translateArray[] = new double[4]; // [mousePressStartX, mousePressStartY, oldOffsetX, oldOffsetY] (f�r das Verschieben ben�tigt)

	
	public FlughafenController(Flughafen model, FlughafenView view) {
		this.model = model;
		this.view = view;
		Canvas canvas = this.view.getCanvas();
		Stage stage= this.view.getStage();
		
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event->{
			canvas.setCursor(Cursor.CLOSED_HAND);
			translateArray[0] = event.getX();		// speichert die Startposition des Draggings
			translateArray[1] = event.getY();
			translateArray[2] = 0;					// die seither vergangene Verschiebung (0, es wurde ja gerade erst geklickt)
			translateArray[3] = 0;
		
		});
		

		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->{
			double xOffset = event.getX()-translateArray[0]; // die Verschiebung relativ zur Startposition des DragEvents
			double yOffset = event.getY()-translateArray[1];
			view.setOffsetX(view.getOffsetX()-translateArray[2]+xOffset); // um richtig zu verschieben, muss vom alten Offset die alte Verschiebung wieder weggenommen werden, bevor die neue addiert wird
			view.setOffsetY(view.getOffsetY()-translateArray[3]+yOffset);
			translateArray[2] = xOffset; // die neue Verschiebung (relativ zum Startpunkt des DragEvents) wird gespeichert
			translateArray[3] = yOffset;
			this.view.update(); 
			
		});
		
		canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event ->{
			canvas.setCursor(Cursor.DEFAULT);
		});
		
		canvas.addEventHandler(ScrollEvent.SCROLL, e->{
			view.zoomTo(e.getDeltaY(), e.getX(), e.getY(),3.0);
			view.update(); 	
			view.updateLabel();
		});
		
		stage.widthProperty().addListener((observableValue, oldWidth, newWidth) -> { // bei Skalierung des Fensters skaliert das Canvas mit
			this.view.resize(newWidth.doubleValue(),canvas.getHeight());
		});
		
		stage.heightProperty().addListener((observableValue, oldHeight, newHeight) -> {
			this.view.resize(canvas.getWidth(),newHeight.doubleValue());
		});
		
		

		this.view.getZoomOutButton().addEventHandler(MouseEvent.MOUSE_PRESSED, event ->{
			this.view.zoomOut(this.model.getNodes());
			this.view.updateLabel();
		});

		
		this.view.getNameButton().setOnAction((ActionEvent event)-> {
			
			ToggleButton source = (ToggleButton) event.getSource();
			 if (source.isSelected()) {
				 this.view.btnText.set("hide Nodename");
				 this.view.showName(this.model.getNodes());
			 } 
			 else {
				 this.view. btnText.set("show Nodenames");
				 this.view.hideName(this.model.getNodes());
				 this.view.update();			   
			}  
		});
		
		this.view.update();	
		
	}

	
}
	

