package app;

import java.sql.Connection;

import model.SQLQuery;
import model.SchemaGraph;
import ui.UserView;


/**
 * The controller to control database and UI.
 * @author keping
 */
public class Controller {
	private SchemaGraph schemaGraph;
	private Connection connection; 
	
	/**
	 * Start connection with the database.
	 */
	public void startConnection() {
		// TODO
	}
	/**
	 * Close connection with the database.
	 */
	public void closeConnection() {
		// TODO
	}
	/**
	 * Get user input from the view.
	 * @param s
	 * @return
	 */
	public String getUserInput(String s) {
		// TODO: get user input from UserView
		return s;
	}
	/**
	 * Process natural language and return an sql query.
	 * @param nl
	 * @return
	 */
	public SQLQuery processNaturalLanguage(String nl) {
		// TODO: process the natural language.
		return new SQLQuery("Hello I'm an sql query");
	}
	/**
	 * Show an sql query in view.
	 * @param query
	 */
	public void viewQuery(SQLQuery query) {
		// TODO
	}
	/**
	 * Get schema graph from data base.
	 */
	public void getSchemaGraphFromDB() {
		// TODO
	}
	
	
	public static void main(String[] args) {
		System.out.println("Hello World!~");
		System.out.println("and open window...");
		javafx.application.Application.launch(UserView.class);
	}
	
}
