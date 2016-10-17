package app;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.NLParser;
import model.ParseTree;
import model.ParseTreeNodeMapper;
import model.ParseTreeStructureAdjuster;
import model.QueryTree;
import model.QueryTreeTranslator;
import model.SQLQuery;
import model.SchemaGraph;
import ui.UserView;


/**
 * The controller between model and view.
 * @author keping
 */
public class Controller {
	private Connection connection;
	private ParseTreeNodeMapper nodeMapper;
	private ParseTreeStructureAdjuster adjuster;
	private QueryTreeTranslator translator;
	private SchemaGraph schemaGraph;
	private NLParser parser;
	private UserView view;
	
	/**
	 * Initialize the Controller.
	 */
	public Controller(UserView view) {
		this.view = view;
//		parser     = new NLParser(); // initialize parser, takes some time
		nodeMapper = new ParseTreeNodeMapper(this);
		adjuster   = new ParseTreeStructureAdjuster(this);
		translator = new QueryTreeTranslator();
		connection = null;
		
		startConnection();
	}
	/**
	 * Start connection with the database and read schema graph
	 */
	public void startConnection() {
		// TODO
		try { Class.forName("org.postgresql.Driver"); } 
		catch (ClassNotFoundException e1) { }
		
		System.out.println("PostgreSQL JDBC Driver Registered!");

		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dblp", "dblpuser", "dblpuser");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Connection successful!");
		
		try {
			schemaGraph = new SchemaGraph(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		view.setDisplay("Database Schema:\n\n"+schemaGraph.toString());
	}
	
	/**
	 * Close connection with the database.
	 */
	public void closeConnection() {
		try {
			if (connection != null) { connection.close(); }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Connection closed.");
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
//		parseTree = new ParseTree(nl, parser);
//		parseTree = nodeMapper.mapTreeNode(parseTree, schemaGraph);
//		parseTree = adjuster.adjustStructure(parseTree, schemaGraph);
//		queryTree = adjuster.parseTreeToQueryTree(parseTree);
//		query = translator.queryTreeToQuery(queryTree);
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
	
	
	//0---- Methods for interactive communication ----
	public void showNodes(ArrayList<ParseTree> trees) {
		// TODO: show possible nodes to user
	}
	public ParseTree getUserChoiceNode() {
		// TODO
		return new ParseTree("User's choice tree nodes", parser);
	}
	public void showStructures(ArrayList<ParseTree> trees) {
		// TODO
	}
	public ParseTree getUserChoiceStructure() {
		// TODO
		return new ParseTree("User's choice tree structure", parser);
	}
	//0-----------------------------------------------
	
}
