package ui;

import app.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserView extends Application {
	
	Stage stage; // the window
	Scene scene; // the main content in the window
	Controller ctrl;
	Text display;
	TextArea fieldSQL;
	
	public void setDisplay(String text) {
		display.setText(text);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		stage = primaryStage;
		stage.setTitle("Window for NLIDB");
		
		Label label1 = new Label("Welcome to Natural Language Interface to DataBase!");
		
		Label lblInput = new Label("Natural Language Input:");
		TextArea fieldIn = new TextArea();
		fieldIn.setPrefHeight(100);
		fieldIn.setPrefWidth(100);
		fieldIn.setWrapText(true);
		
		Button btnTranslate = new Button("translate");

		Label lblSQL = new Label("SQL query:");
		fieldSQL = new TextArea();
		fieldSQL.setPrefHeight(100);
		fieldSQL.setPrefWidth(100);
		fieldSQL.setWrapText(true);
		
		// TODO: create a frame to show database query output to user.
		// TODO: create bindings to show output
		
		// Define action of the translate button.
		btnTranslate.setOnAction(e -> {
			String queryMsg = ctrl.processNaturalLanguage(fieldIn.getText()).get();
			fieldSQL.setText(queryMsg);
		});
		
		display = new Text();
		display.setWrappingWidth(500);
		display.setText("Default display text");
		
		VBox vb = new VBox();
		vb.setSpacing(10);
		vb.getChildren().addAll(
				label1,
				lblInput,fieldIn,
				btnTranslate,
				lblSQL, fieldSQL
				);
		
		HBox hb = new HBox();
		hb.setSpacing(10);
		hb.getChildren().addAll(vb, display);
		
		scene = new Scene(hb, 800, 450);
		
		stage.setScene(scene);
		stage.show();
		
		ctrl = new Controller(this);
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
		try {
		Application.launch(args);
		} catch (Exception e) { e.printStackTrace(); }
	}

}
