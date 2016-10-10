package app;

import java.sql.Connection;
import java.util.ArrayList;

import model.NLParser;
import model.ParseTree;
import model.ParseTreeNodeMapper;
import model.ParseTreeStructureAdjuster;
import model.QueryTree;
import model.QueryTreeTranslator;
import model.SQLQuery;
import model.SchemaGraph;


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
	
	/**
	 * Initialize the Controller.
	 */
	public Controller() {
		nodeMapper = new ParseTreeNodeMapper(this);
		adjuster   = new ParseTreeStructureAdjuster(this);
		translator = new QueryTreeTranslator();
		parser     = new NLParser();
	}
	/**
	 * Start connection with the database and read schema graph
	 */
	public void startConnection() {
		// TODO
		getSchemaGraphFromDB();
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
		parseTree = new ParseTree(nl, parser);
		parseTree = nodeMapper.mapTreeNode(parseTree, schemaGraph);
		parseTree = adjuster.adjustStructure(parseTree, schemaGraph);
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
	private SchemaGraph getSchemaGraphFromDB() {
		// TODO
		return new SchemaGraph();
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
