package application;

import application.model.*;
import javafx.stage.Stage;

public class FlughafenView {
	private Flughafen model;
	private Stage stage;

	public FlughafenView(Flughafen model, Stage stage) {
		this.model = model;
		this.stage = stage;
		
	}
	
	public Stage getStage() {
		return stage;
	}

}