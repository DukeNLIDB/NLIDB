package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UserView extends Application {
	
	Stage stage; // the window
	Scene scene; // the main content in the window

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		stage.setTitle("First window for NLIDB");
		
		Label label1 = new Label("Welcome!");
		
		StackPane layout = new StackPane();
		layout.getChildren().add(label1);
		
		scene = new Scene(layout, 300, 200);
		
		stage.setScene(scene);
		stage.show();
	}
	

}
