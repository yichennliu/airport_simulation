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
			if (e.getDeltaY()>0) this.view.setZoomFactor(this.view.getZoomFactor()+3.0);
			else this.view.setZoomFactor(this.view.getZoomFactor()-3.0);
			this.view.drawCanvas(); // das kann sp�ter raus, wenn der Controller ohnehin in jedem Tick die drawCanvas()-Methode aufruft
		});
	}
	
}
