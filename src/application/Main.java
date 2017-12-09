package application;

import java.util.regex.Pattern;

import application.model.*;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    Flughafen model;
    
    model = JSONImport.createFlughafen("application/json/small.json");
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
