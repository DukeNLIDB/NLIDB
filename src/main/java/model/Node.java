package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for a parse tree node.
 * @author keping
 *
 */
public class Node {
	// TODO: all fields should be private in final version
	
	/**
	 * Index of the node in the sentence corresponding to 
	 * the ParseTree.
	 */
	int index;
	/**
	 * Information indicating the corresponding SQL component of the Node.
	 */
	NodeInfo info = null;
	/**
	 * The natural language word of the Node. This is the only field of 
	 * the Node object that is immutable.
	 */
	String word;
	/**
	 * Part-of-speech tag for the Node.
	 */
	String posTag;
	
	/**
	 * Parent of the node can be directly modified by ParseTree.
	 */
	Node parent = null; // package private
	/**
	 * Children of the node can be directly modified by ParseTree.
	 */
	List<Node> children = new ArrayList<Node>(); // package private
	
	//for testing purpose
	boolean isInvalid = false;
	
	public Node(int index, String word, String posTag) {
		this.index = index;
		this.word = word;
		this.posTag = posTag;
	}
	
	public Node(Node n){
		this.index = n.index;
		this.word = n.word;
		this.posTag = n.posTag;
		this.info = n.info;
		if (n.parent == null)
			this.parent = n.parent;
		else
			this.parent = new Node(n.parent);
		for (int i = 0; i < n.children.size(); i++)
			this.children.add(new Node(n.children.get(i)));
	}
	
	public int getIndex() { return index; }
	public void setIndex(int index) { this.index = index; }
	public NodeInfo getInfo() { return info; }
	public void setInfo(NodeInfo info) { this.info = info; }
	public String getWord() { return word; }
	public void setWord(String word) {this.word = word;}
	public String getPosTag() { return posTag; }
	public List<Node> getChildren() { return children; }
	public void setChild(Node child) {this.children.add(child);}
	public Node getParent() {return parent;}
	public void setParent(Node parent) {this.parent = parent;}

	public String toString() {
		String s = word;
		if (info != null) {
			s += "("+info.getType()+":"+info.getValue()+")";
		}
		return s;
	}
}
