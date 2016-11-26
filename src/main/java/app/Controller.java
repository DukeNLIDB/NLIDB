package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import model.NLParser;
import model.Node;
import model.NodeInfo;
import model.NodeMapper;
import model.ParseTree;
import model.ParseTree.ParseTreeIterator;
import model.SQLQuery;
import model.SchemaGraph;
import ui.UserView;


/**
 * The controller between model and view.
 * @author keping
 */
public class Controller {
	private Connection connection = null;
	private String input;
	private SchemaGraph schema;
	private NLParser parser;
	private NodeMapper nodeMapper;
	private ParseTree parseTree;
	private UserView view;	
	/**
	 * Iterator for nodes mapping.
	 */
	private ParseTreeIterator iter;
	/**
	 * Attribute for nodes mapping, to indicate the current Node.
	 */
	private Node node;
	private boolean mappingNodes = false;
	private boolean processing = false;
	
	/**
	 * Initialize the Controller.
	 */
	public Controller(UserView view) {
		this.view = view;
		startConnection();
		
		try { nodeMapper = new NodeMapper();
		} catch (Exception e) { e.printStackTrace(); }
		parser     = new NLParser(); // initialize parser, takes some time
		
		System.out.println("Controller initialized.");
	}
	
	/**
	 * ONLY FOR TESTING. An empty constructor.
	 */
	public Controller() {
		
	}
	
	/**
	 * Start connection with the database and read schema graph
	 */
	public void startConnection() {
		
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
			schema = new SchemaGraph(connection);
			view.setDisplay("Database Schema:\n\n"+schema.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isProcessing() { return processing; }
	public String getInput() { return input; }
	public void setInput(String input) { this.input = input; }
	
	/**
	 * Helper method for nodes mapping, displaying the currently mapping Node
	 * and the choices on the view.
	 * @param choices
	 */
	private void setChoicesOnView(List<NodeInfo> choices) {
		view.setDisplay("Mapping nodes: \n"+parseTree.getSentence()+"\n");
		view.appendDisplay("Currently on: "+node);
		view.setChoices(FXCollections.observableArrayList(choices));
	}
	
	/**
	 * Terminates the mapping Nodes process by setting the boolean mappingNodes false;
	 */
	private void finishNodesMapping() {
		view.setDisplay("Nodes mapped.\n"+parseTree.getSentence());
		mappingNodes = false;
		view.removeChoiceBoxButton();
	}
	
	/**
	 * Start the nodes mapping process. A boolean will be set to indicate that
	 * the application is in the process of mapping Nodes. Cannot call startMappingNodes
	 * again during mapping Nodes. After this is called, the view shows the choices
	 * of NodeInfos for a node, waiting for the user to choose one.
	 */
	public void startMappingNodes() {
		if (mappingNodes) { return; }
		mappingNodes = true;
		iter = parseTree.iterator();
		if (!iter.hasNext()) {
			finishNodesMapping();
			return; 
		}
		
		node = iter.next();
		List<NodeInfo> choices = nodeMapper.getNodeInfoChoices(node, schema);
		if (choices.size() == 1) { chooseNode(choices.get(0)); }
		else { setChoicesOnView(choices); }
		// After this wait for the button to call chooseNode
	}
	
	/**
	 * Choose NodeInfo for the current Node. This method is called when the user
	 * clicked the confirmChoice button, or automatically called when the choices
	 * of NodeInfo contains only one element. 
	 * @param info {@link NodeInfo}
	 */
	public void chooseNode(NodeInfo info) {
		if (!mappingNodes) { return; }
		node.setInfo(info);
		if (!iter.hasNext()) {
			finishNodesMapping(); 
			return;
		}
		
		node = iter.next();
		List<NodeInfo> choices = nodeMapper.getNodeInfoChoices(node, schema);
		if (choices.size() == 1) { chooseNode(choices.get(0)); }
		else { setChoicesOnView(choices); }
		// After this wait for the button to call chooseNode
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
	public void processNaturalLanguage() {
		processing = true;
		parseTree = new ParseTree(input, parser);
		startMappingNodes();
	}
	
	public SQLQuery getQuery() {
		// TODO
		SQLQuery query;
		query = new SQLQuery(parseTree.getSentence()+" to sql query...");
		return query;
	}
	
	/**
	 * Show an sql query in view.
	 * @param query
	 */
	public void viewQuery(SQLQuery query) {
		// TODO
	}
	

}
