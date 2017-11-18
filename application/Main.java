package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
	  
    Flughafen model = new Flughafen(screenWidth, screenHeight -30);
    View view = new View(model, primaryStage,screenWidth, screenHeight-30);
    new Controller(model, view);
	
	
  }

  public static void main(String[] args) {
    Application.launch(args);
  }
 
}
