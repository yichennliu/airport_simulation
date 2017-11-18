package application;

import application.model.*;

public class FlughafenController {
	private FlughafenView view;
	private Flughafen model;

	public FlughafenController(Flughafen model, FlughafenView view) {
		this.model = model;
		this.view = view;
		
		
		this.view.getStage().setTitle("Flughafen");
		this.view.getStage().show();
	}
	
}
