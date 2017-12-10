package application;

import java.io.File;
import application.model.Flughafen;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FlughafenController {
	private FlughafenView view;
	private Flughafen model;
	private double translateArray[] = new double[4]; // [mousePressStartX, mousePressStartY, oldOffsetX, oldOffsetY] (für das Verschieben benötigt)
	private FileChooser fileChooser = new FileChooser();

//	
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
			FlughafenView.setOffsetX(FlughafenView.getOffsetX()-translateArray[2]+xOffset); // um richtig zu verschieben, muss vom alten Offset die alte Verschiebung wieder weggenommen werden, bevor die neue addiert wird
			FlughafenView.setOffsetY(FlughafenView.getOffsetY()-translateArray[3]+yOffset);
			translateArray[2] = xOffset; // die neue Verschiebung (relativ zum Startpunkt des DragEvents) wird gespeichert
			translateArray[3] = yOffset;
			this.view.update(false,true); 
			
		});
		
		canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event ->{
			canvas.setCursor(Cursor.DEFAULT);
		});
		
		canvas.addEventHandler(ScrollEvent.SCROLL, e->{
			view.zoomTo(e.getDeltaY(), e.getX(), e.getY(),3.0);
			view.update(false,true); 	
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
				 view.showNames(true);
			 } 
			 else {
				view.showNames(false);	   
			}  
		});
		
		EventHandler<ActionEvent> loop = e -> {
			this.model.update();
			this.view.update(true,false);
			Flughafen.tick();
		};
		
		KeyFrame frame = new KeyFrame(Duration.seconds(1),loop);
		Timeline t1 = new Timeline(frame);
		t1.setCycleCount(Timeline.INDEFINITE);
		t1.play();
		
		
		this.fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("Flughafendatei","*.json"),
		         new ExtensionFilter("Alle Dateien","*.*")
		 );
		
		this.view.update(false,false);	
		
		
		
		this.view.getColorPicker().setOnAction(new EventHandler<ActionEvent>() {
	            @Override
	            public void handle(ActionEvent event) {
	            	view.getBgRect().setFill(view.getColorPicker().getValue()); // muss in controler
	            }
	        });

	
	this.view.getfileChooserButton().addEventHandler(MouseEvent.MOUSE_PRESSED, event ->{
		openFile();
	});


	}

	private void openFile() {
	Flughafen newFlughafen;
	Stage fileOpenStage = new Stage();
	File selectedFile =  fileChooser.showOpenDialog(fileOpenStage);
	if(selectedFile!=null) {
		String path = selectedFile.getPath();
	    
	    newFlughafen = JSONImport.createFlughafen(path);
	    if (newFlughafen == null) {
	    	return;
	    }
	    
	    this.fileChooser.setInitialDirectory(selectedFile.getParentFile());
		this.model = newFlughafen;
	    this.view.reset(this.model);
	}

}


}


