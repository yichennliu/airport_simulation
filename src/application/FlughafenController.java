package application;

import application.model.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;

public class FlughafenController {
	private FlughafenView view;
	private Flughafen model;
	private static final double MAX_SCALE=3.0;
	private static final double MIN_SCALE=1.0;
	private double translateArray[] = new double[4]; // [mousePressStartX, mousePressStartY, oldOffsetX, oldOffsetY] (für das Verschieben benötigt)

	
	public FlughafenController(Flughafen model, FlughafenView view) {
		this.model = model;
		this.view = view;
		Canvas canvas = this.view.getCanvas();
		Stage stage= this.view.getStage();
		this.view.drawCanvas();
		
		canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event->{
			translateArray[0] = event.getX();
			translateArray[1] = event.getY();
			translateArray[2] = 0;
			translateArray[3] = 0;
		});
		
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->{		
			double xOffset = event.getX()-translateArray[0];
			double yOffset = event.getY()-translateArray[1];
			view.setOffsetX(view.getOffsetX()-translateArray[2]+xOffset);
			view.setOffsetY(view.getOffsetY()-translateArray[3]+yOffset);
			translateArray[2] = xOffset;
			translateArray[3] = yOffset;
			this.view.drawCanvas();
		});
		
		canvas.addEventHandler(ScrollEvent.SCROLL, e->{
			view.zoomTo(e.getDeltaY(), e.getX(), e.getY(),3.0);
			view.drawCanvas();
	});
			
}

	public static double compare(double value, double min, double max){
		if(Double.compare(value, min)<0){
			return min;
		}
		if(Double.compare(value, max)>0){
			return max;
		}
		return value;
	}
	
}
	

