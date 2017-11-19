package application;

import application.model.*;
import javafx.scene.input.ScrollEvent;

public class FlughafenController {
	private FlughafenView view;
	private Flughafen model;

	public FlughafenController(Flughafen model, FlughafenView view) {
		this.model = model;
		this.view = view;
		this.view.drawCanvas();
		this.view.getCanvas().addEventHandler(ScrollEvent.SCROLL, e->{
			if (e.getDeltaY()>0) this.view.setZoomFactor(this.view.getZoomFactor()+0.1);
			else this.view.setZoomFactor(this.view.getZoomFactor()-0.1);
			this.view.drawCanvas();
		});
	}
	
}
