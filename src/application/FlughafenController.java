package application;

import application.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.ScrollEvent;

public class FlughafenController {
	private FlughafenView view;
	private Flughafen model;
	
	public FlughafenController(Flughafen model, FlughafenView view) {
		this.model = model;
		this.view = view;
		Canvas canvas = this.view.getCanvas();
		this.view.drawCanvas();
		canvas.addEventHandler(ScrollEvent.SCROLL, e->{
			if (e.getDeltaY()>0) this.view.setZoomFactor(this.view.getZoomFactor()+3.0);
			else this.view.setZoomFactor(this.view.getZoomFactor()-3.0);
			this.view.drawCanvas(); // das kann später raus, wenn der Controller ohnehin in jedem Tick die drawCanvas()-Methode aufruft
		});
	}
	
}
