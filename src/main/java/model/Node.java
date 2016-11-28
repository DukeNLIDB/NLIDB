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
	
	public Node(int index, String word, String posTag){
		this(index, word, posTag, null);
	}
	
	public Node(int index, String word, String posTag, NodeInfo info) {
		this.index = index;
		this.word = word;
		this.posTag = posTag;
		this.info = info;
	}
	
	private Node clone(Node node){
		if (node == null) return null;
		Node copy = new Node(node.index, node.word, node.posTag, node.info);
		for (Node child : node.children){
			Node copyChild = clone(child);
			copyChild.parent = copy;
			copy.children.add(copyChild);
		}
		return copy;
	}
	public Node clone(){
		return clone(this);
	}
	
	public static void preOrder(Node node){
		if (node == null) return;
		System.out.print(node+" ");
		for (Node child: node.getChildren()){
			preOrder(child);
		}
		System.out.println("\n");
	}
	
	public static int count(Node root){
		int number = 0;
		if (root != null) {
			number++;
			for (Node child: root.getChildren()){
				number = number + count(child);
			}
		}
		return number;
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

	
	
	/**
	 * Only includes posTag, word, info, and children.
	 * Return the hashCode of the tree represented by this node. 
	 */
	@Override
	public int hashCode() { // exclude parent.
		final int prime = 31;
		int result = 17;
		result = prime * result + ((posTag == null) ? 0 : posTag.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		if (children != null) {
			for (Node child : children) {
				result = prime * result + child.hashCode();				
			}
		}

		return result;
	}

	
	/**
	 * Only considers word, posTag, info, and children (recursively).
	 * See whether two trees represented by two nodes are equal.
	 */
	@Override
	public boolean equals(Object obj) { // exclude parent
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		Node other = (Node) obj;
		if (!word.equals(other.word)) { return false; }
		if (!posTag.equals(other.posTag)) { return false; }
		if (info != other.info) {
			if (info == null || other.info == null) { return false; }
			if (!info.equals(other.info)) { return false; }
		}
		if (children != other.children) {
			if (children == null || other.children == null) { return false; }
			if (children.size() != other.children.size()) { return false; }
			for (int i = 0; i < children.size(); i++) {
				if (!children.get(i).equals(other.children.get(i))) { return false; }	
			}
		}
		return true;
	}

	public String toString() {
		String s = word;
		if (info != null) {
			s += "("+info.getType()+":"+info.getValue()+")";
		}
		return s;
	}
}
