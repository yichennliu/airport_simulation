package application;

import application.model.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Flughafen model = JSONImport.createFlughafen("application/json/small.json");
    FlughafenView view = new FlughafenView(model, primaryStage);
    new FlughafenController(model, view);
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
 
}
