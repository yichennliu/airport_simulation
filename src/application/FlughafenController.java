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
	
	public FlughafenController(Flughafen model, FlughafenView view) {
		this.model = model;
		this.view = view;
		Canvas canvas = this.view.getCanvas();
		Stage stage= this.view.getStage();
		this.view.drawCanvas();
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event->{
			canvas.setTranslateX(event.getSceneX());
			canvas.setTranslateY(event.getSceneY());
		});
		
		stage.addEventHandler(ScrollEvent.SCROLL, e->{
			double delta =1.5;
			double scale = canvas.getScaleY();
			double oldscale =scale;
			if (e.getDeltaY()<0) {
				scale= scale/delta;
				}
			else {
				scale= scale*delta;
			}
			scale=compare(scale, MIN_SCALE,MAX_SCALE);
			double fac=scale-oldscale;
			double dx=(e.getSceneX()-(canvas.getBoundsInParent().getWidth()/2+canvas.getBoundsInParent().getMinX()));
			double dy=(e.getSceneY()-(canvas.getBoundsInParent().getHeight()/2+canvas.getBoundsInParent().getMinY()));
			canvas.setScaleY(scale);
			canvas.setTranslateX(canvas.getTranslateX()-fac*dx);
			canvas.setTranslateY(canvas.getTranslateY()-fac*dy);
		
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
	

