package app;

import java.sql.Connection;

import model.ParseTree;
import model.ParseTreeNodeMapper;
import model.ParseTreeStructureAdjuster;
import model.QueryTree;
import model.QueryTreeTranslator;
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
	private ParseTreeNodeMapper nodeMapper;
	private ParseTreeStructureAdjuster adjuster;
	private QueryTreeTranslator translator;
	
	/**
	 * Initialize the Controller.
	 */
	public Controller() {
		nodeMapper = new ParseTreeNodeMapper();
		adjuster   = new ParseTreeStructureAdjuster();
		translator = new QueryTreeTranslator();
	}
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
		ParseTree parseTree;
		QueryTree queryTree;
		SQLQuery  query;
		// TODO: process the natural language.
		parseTree = new ParseTree(nl);
		parseTree = nodeMapper.mapTreeNode(parseTree);
		parseTree = adjuster.adjustStructure(parseTree);
		queryTree = adjuster.parseTreeToQueryTree(parseTree);
		query = translator.queryTreeToQuery(queryTree);
		// below just a test implementation
		query = new SQLQuery(nl+" to sql query...");
		return query;
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
		Controller ctrl = new Controller();
		System.out.println("Hello World!~");
		System.out.println("and open window...");
		javafx.application.Application.launch(UserView.class);
	}
	
}
