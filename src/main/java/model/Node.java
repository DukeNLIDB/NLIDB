package model;

import java.util.List;

/**
 * Interface for a parse tree node.
 * @author keping
 *
 */
public class Node {
	
	/**
	 * Index of the node in the sentence corresponding to 
	 * the ParseTree.
	 */
	private int index;
	/**
	 * Information indicating the corresponding SQL component of the Node.
	 */
	private NodeInfo info = null;
	/**
	 * The natural language word of the Node. This is the only field of 
	 * the Node object that is immutable.
	 */
	private String word;
	
	/**
	 * Parent of the node can be directly modified by ParseTree.
	 */
	Node parent = null; // package private
	/**
	 * Children of the node can be directly modified by ParseTree.
	 */
	List<Node> children = null; // package private
	
	public Node(int index, String word) {
		this.index = index;
		this.word = word;
	}
	
	public int getIndex() { return index; }
	public void setIndex(int index) { this.index = index; }
	public NodeInfo getInfo() { return info; }
	public void setInfo(NodeInfo info) { this.info = info; }
	public String getWord() { return word; }

	
}
