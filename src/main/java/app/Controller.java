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
	private boolean selectingTree = false;
	private boolean processing = false;
	private List<ParseTree> treeChoices;
	private SQLQuery query;
	
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
	
// ---- Methods for nodes mapping ---- //
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
		processAfterNodesMapping();
	}
	
	/**
	 * Start the nodes mapping process. A boolean will be set to indicate that
	 * the application is in the process of mapping Nodes. Cannot call startMappingNodes
	 * again during mapping Nodes. After this is called, the view shows the choices
	 * of NodeInfos for a node, waiting for the user to choose one.
	 */
	public void startMappingNodes() {
		if (mappingNodes) { return; }
		view.showNodesChoice();
		
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
//		System.out.println("Now the tree is:");
//		System.out.println(parseTree);
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
// ----------------------------------- //
	
	
// ---- Methods for trees selection ---- //
	public void startTreeSelection() {
		if (selectingTree) { return; }
		view.showTreesChoice();
		selectingTree = true;
		treeChoices = parseTree.getAdjustedTrees();
	}
	
	public void showTree(int index) {
		view.setDisplay(treeChoices.get(index).toString());
	}
	
	public void chooseTree(int index) {
		parseTree = treeChoices.get(index);
		finishTreeSelection();
	}
	
	public void finishTreeSelection() {
		selectingTree = false;
		view.removeTreesChoices();
		processAfterTreeSelection();
	}
// ------------------------------------- //
	
	public void processAfterTreeSelection() {
		System.out.println("The tree before implicit nodes insertion: ");
		System.out.println(parseTree);
		parseTree.insertImplicitNodes();
		System.out.println("Going to do translation for tree: ");
		System.out.println(parseTree);
		query = parseTree.translateToSQL(schema);	
		view.setDisplay(query.toString());
		processing = false;		
	}
	
	public void processAfterNodesMapping() {
		System.out.println("Going to remove meaningless nodes for tree: ");
		System.out.println(parseTree);
		parseTree.removeMeaninglessNodes();
		parseTree.mergeLNQN();
		startTreeSelection();
	}
	
	/**
	 * Process natural language and return an sql query.
	 * @param nl
	 * @return
	 */
	public void processNaturalLanguage(String input) {
		if (processing) { view.appendDisplay("\nCurrently processing a sentence!\n"); }
		processing = true;
		parseTree = new ParseTree(input, parser);
		startMappingNodes();
	}

}
