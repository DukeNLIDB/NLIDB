package ui;

import app.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserView extends Application {
	
	Stage stage; // the window
	Scene scene; // the main content in the window
	Controller ctrl;

	@Override
	public void start(Stage primaryStage) throws Exception {
		ctrl = new Controller(this);
		stage = primaryStage;
		stage.setTitle("Window for NLIDB");
		
		Label label1 = new Label("Welcome to Natural Language Interface to DataBase!");
		
		Label lblInput = new Label("Natural Language Input:");
		TextField fieldIn = new TextField();
		fieldIn.setPrefHeight(100);
		
		Button btnTranslate = new Button("translate");

		Label lblSQL = new Label("SQL query:");
		TextField fieldSQL = new TextField();
		fieldSQL.setPrefHeight(100);
		
		// TODO: create a frame to show database query output to user.
		// TODO: create bindings to show output
		
		// Define action of the translate button.
		btnTranslate.setOnAction(e -> {
			String queryMsg = ctrl.processNaturalLanguage(fieldIn.getText()).get();
			fieldSQL.setText(queryMsg);
		});
		
		VBox vb = new VBox();
		vb.setSpacing(10);
		vb.getChildren().addAll(
				label1,
				lblInput,fieldIn,
				btnTranslate,
				lblSQL, fieldSQL
				);
		
		scene = new Scene(vb, 600, 450);
		
		stage.setScene(scene);
		stage.show();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		if (ctrl != null) {
			ctrl.closeConnection();
		}
		Platform.exit();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}

}
