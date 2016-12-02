package ui;

import app.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.NodeInfo;

public class UserView extends Application {
	private static final String TEST_TEXT = "Return the number of authors who published theory papers before 1980.";
	// "Return the number of authors who published theory papers before 1980."
	
	Stage stage; // the window
	Scene scene; // the main content in the window
	Controller ctrl;
	Button btnTranslate;
	Text display;
	ComboBox<NodeInfo> choiceBox; // use scrollable comboBox instead of choiceBox
	Button btnConfirmChoice;
	ComboBox<Integer> treeChoice;
	Button btnTreeConfirm;
	HBox hb;
	VBox vb1, vb2;
	
	
	public void setDisplay(String text) {
		display.setText(text);
	}
	
	public void appendDisplay(String text) {
		display.setText(display.getText()+text);
	}
	
	public void showNodesChoice() {
		vb2.getChildren().addAll(choiceBox, btnConfirmChoice);
	}
	
	public void removeChoiceBoxButton() {
		vb2.getChildren().remove(choiceBox);
		vb2.getChildren().remove(btnConfirmChoice);
	}
	
	public void setChoices(ObservableList<NodeInfo> choices) {
		choiceBox.setItems(choices);
		choiceBox.setValue(choices.get(0));
	}
	
	public NodeInfo getChoice() {
		return choiceBox.getValue();
	}
	
	public void showTreesChoice() {
		vb2.getChildren().addAll(treeChoice, btnTreeConfirm);
	}
	
	public void removeTreesChoices() {
		vb2.getChildren().removeAll(treeChoice, btnTreeConfirm);
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
		fieldIn.setText(TEST_TEXT);
		
		btnTranslate = new Button("translate");
		
		// Define action of the translate button.
		btnTranslate.setOnAction(e -> {
			ctrl.processNaturalLanguage(fieldIn.getText());
		});
		
		display = new Text();
		display.setWrappingWidth(500);
		display.prefHeight(300);
		display.setText("Default display text");

		// choices and button for nodes mapping
		choiceBox = new ComboBox<NodeInfo>();
		choiceBox.setVisibleRowCount(6);
		btnConfirmChoice = new Button("confirm choice");
		btnConfirmChoice.setOnAction(e -> {
			ctrl.chooseNode(getChoice());
		});
		
		// choices and button for tree selection
		treeChoice = new ComboBox<Integer>(); // ! only show 3 choices now
		treeChoice.setItems(FXCollections.observableArrayList(0,1,2));
		treeChoice.getSelectionModel().selectedIndexProperty().addListener((ov, oldV, newV) -> {
			ctrl.showTree(treeChoice.getItems().get((Integer) newV));
		});
		btnTreeConfirm = new Button("confirm tree choice");
		btnTreeConfirm.setOnAction(e -> {
			ctrl.chooseTree(treeChoice.getValue());
		});
		
		vb1 = new VBox();
		vb1.setSpacing(10);
		vb1.getChildren().addAll(
				label1,
				lblInput,fieldIn,
				btnTranslate
				);
		
		vb2 = new VBox();
		vb2.setSpacing(20);
		vb2.getChildren().addAll(display);
		
		hb = new HBox();
		hb.setPadding(new Insets(15, 12, 15, 12));
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
