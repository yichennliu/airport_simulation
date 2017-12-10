package application;

import application.model.Flughafen;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Flughafen model;
    
    model = JSONImport.createFlughafen("application/json/big.json");
    if (model == null) {
    	return;
    }
    
    FlughafenView view = new FlughafenView(model, primaryStage);
    new FlughafenController(model, view);
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
 
}
