package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Interface for a parse tree node.
 * @author keping
 *
 */
public class Node {
	
	/**
	 * record if the node is copied over
	 */
	boolean outside = false;
	
	private int index = 0;
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
	
	public Node(String word, String posTag, NodeInfo info) {
		this(0, word, posTag, info);
		
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
	

	public NodeInfo getInfo() { return info; }
	public void setInfo(NodeInfo info) { this.info = info; }
	public String getWord() { return word; }
	public void setWord(String word) {this.word = word;}
	public String getPosTag() { return posTag; }
	public List<Node> getChildren() { return children; }
	public void setChild(Node child) {this.children.add(child);}
	public Node getParent() {return parent;}
	public void setParent(Node parent) {this.parent = parent;}
	public void setOutside(boolean outside) {this.outside = outside;}
	public boolean getOutside() {return this.outside;}
	
	public void removeChild (Node child) {

		for (int i = 0; i < children.size(); i ++) {

			if (children.get(i).equals(child)) {

				children.remove(i);
				return;
			}
		}
	}
	
	public void printNodeArray () {
		
		Node [] nodes = genNodesArray();
		
		for (int i = 0; i < nodes.length; i++) {
			System.out.println("type: " + nodes[i].getInfo().getType() + " value: " + nodes[i].getInfo().getValue());
		}
	}
	

	/**
	 * Generate an array of the nodes tree with this as root
	 * using pre-order traversal;
	 * @return
	 */
	public Node[] genNodesArray() {
		List<Node> nodesList = new ArrayList<>();
		LinkedList<Node> stack = new LinkedList<>();
		stack.push(this);
		while (!stack.isEmpty()) {
			Node curr = stack.pop();
			nodesList.add(curr);
			List<Node> currChildren = curr.getChildren();
			for (int i = currChildren.size()-1; i >= 0; i--) {
				stack.push(currChildren.get(i));	
			}
		}
		int N = nodesList.size();
		Node[] nodes = new Node[N];
		for (int i = 0; i < N; i++) {
			nodes[i] = nodesList.get(i);
		}
		return nodes;
	}
	
	/**
	 * Only includes posTag, word, info, and children.
	 * Return the hashCode of the tree represented by this node. 
	 */
	@Override
	public int hashCode() { // exclude parent.
		final int prime = 31;
		int result = 17;
		result = prime * result + index;
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
		if (index != other.index) { return false; }
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
		String s = "("+index+")"+word;
		if (info != null) {
			s += "("+info.getType()+":"+info.getValue()+")";
		}
		return s;
	}
}
