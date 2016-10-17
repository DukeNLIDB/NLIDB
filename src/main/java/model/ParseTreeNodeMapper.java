package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class ParseTreeNodeMapper {
	private ParseTree tree;
	private int N;
	private SchemaGraph schema;
	private ArrayList<Node> nodes;
	private Queue<List<Node>> choicesQueue;
	
	/**
	 * Create a shallow copy, only deep of the nodes part.
	 * @param tree
	 * @param schema
	 */
	public ParseTreeNodeMapper(ParseTree tree, SchemaGraph schema) {
		this.schema = schema;
		this.tree = tree;
		this.N = tree.length();
		this.nodes = new ArrayList<Node>(Arrays.asList(new Node[tree.length()]));
		choicesQueue = new LinkedList<>();
		
		mapNodes();
	}
	
	private void mapNode(int i) {
		String word = tree.getWord(i);
		String nodeType = "";
		String nodeValue = "";
		if (word.equals("return")) {
			nodeType = "SN";
			nodeValue = "SELECT";
		} else {
			searchName(i);
			return; 
		}
		chooseNode(new Node(i, nodeType, nodeValue));
	}
	
	/**
	 * Search the word over database table and column names.
	 * @param word
	 */
	private void searchName(int i) {
		List<Node> choices = new ArrayList<>();
		choices.add(new Node (i, "HAHAType", "HAHAValue"));
		choices.add(new Node (i, "HAHBType", "HAHBValue"));
		choices.add(new Node (i, "HAHCType", "HAHCValue"));
		
		choicesQueue.add(choices);
		// TODO
	}
	
	/**
	 * Search the word over database values.
	 * @param word
	 */
	private void searchValue(String word) {
		// TODO
	}
	
	/**
	 * Map nodes from left to right. For uncertain nodes, enqueue the choices
	 * to choicesQueue.
	 */
	private void mapNodes() {
		for (int i = 1; i < N; i++) {
			mapNode(i);
		}
	}
	
	public boolean hasNextChoices() {
		return !choicesQueue.isEmpty();
	}
	
	public List<Node> nextChoices() {
		return choicesQueue.poll();
	}
	
	public void chooseNode(Node node) {
		nodes.set(node.index, node);
	}
	
	public ParseTree getMappedTree() {
		tree.setNodes(nodes);
		return tree;
	}

	
}
