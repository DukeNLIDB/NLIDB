package ui;

import app.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Node;

public class UserView extends Application {
	
	Stage stage; // the window
	Scene scene; // the main content in the window
	Controller ctrl;
	Text display;
	TextArea fieldSQL;
	ChoiceBox<Node> choiceBox;
	
	public void setDisplay(String text) {
		display.setText(text);
	}
	
	public void appendDisplay(String text) {
		display.setText(display.getText()+text);
	}
	
	public void setChoices(ObservableList<Node> choices) {
		choiceBox.setItems(choices);
		choiceBox.setValue(choices.get(0));
	}
	
	public Node getChoice() {
		return choiceBox.getValue();
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
		fieldIn.setText("Haha return my lord.");
		
		Button btnTranslate = new Button("translate");

		Label lblSQL = new Label("SQL query:");
		fieldSQL = new TextArea();
		fieldSQL.setPrefHeight(100);
		fieldSQL.setPrefWidth(100);
		fieldSQL.setWrapText(true);
		
		// Define action of the translate button.
		btnTranslate.setOnAction(e -> {
			if (!ctrl.isProcessing()) {
				ctrl.setInput(fieldIn.getText());
				ctrl.processNaturalLanguage();
			} else {
				this.appendDisplay("Currently processing a sentence...");
			}
		});
		
		display = new Text();
		display.setWrappingWidth(500);
		display.prefHeight(300);
		display.setText("Default display text");

		choiceBox = new ChoiceBox<Node>();
		
		
		Button btnConfirm = new Button("confirm choice");
		btnConfirm.setOnAction(e -> {
			ctrl.chooseNode();
		});
		
		VBox vb1 = new VBox();
		vb1.setSpacing(10);
		vb1.getChildren().addAll(
				label1,
				lblInput,fieldIn,
				btnTranslate,
				lblSQL, fieldSQL
				);
		
		VBox vb2 = new VBox();
		vb2.setSpacing(20);
		vb2.getChildren().addAll(display, choiceBox, btnConfirm);
		
		HBox hb = new HBox();
		hb.setSpacing(10);
		hb.getChildren().addAll(vb1, vb2);
		
		scene = new Scene(hb, 800, 450);
		
		stage.setScene(scene);
		ctrl = new Controller(this);
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
		try {
		Application.launch(args);
		} catch (Exception e) { e.printStackTrace(); }
	}

}
