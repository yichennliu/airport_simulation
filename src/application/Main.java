package application;

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
    
    try {
    	model = JSONImport.createFlughafen("application/json/small.json");
    	
    }
    catch(Exception e) {
    	Alert alertWindow = new Alert(Alert.AlertType.ERROR,
                e.getMessage(),
                ButtonType.OK);
    	alertWindow.setHeaderText("Fehler, bitte Überprüfen Sie ihre JSON-Dartei");
    	Image img = new Image("/application/source/Images/ber.jpg");
    	alertWindow.setGraphic(new ImageView(img));
    	alertWindow.show();
    	return;
    }
    
    FlughafenView view = new FlughafenView(model, primaryStage);
    new FlughafenController(model, view);
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
 
}
