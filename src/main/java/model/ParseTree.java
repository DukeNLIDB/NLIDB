package model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class ParseTree implements IParseTree {
	
	/**
	 * Order of parse tree reformulation (used in getAdjustedTrees())
	 */
	int edit;
	// We no longer use an array to store the nodes!
	/**
	 * Root Node. Supposed to be "ROOT".
	 */
	Node root;
	
	/**
	 * Empty constructor, only for testing.
	 */
	public ParseTree() { }
	
	/**
	 * Construct a parse tree using the stanford NLP parser. Only one sentence.
	 * Here we are omitting the information of dependency labels (tags).
	 * @param text input text.
	 */
	public ParseTree(String text, NLParser parser) {
		// pre-processing the input text
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		List<HasWord> sentence = null;
		for (List<HasWord> sentenceHasWord : tokenizer) {
			sentence = sentenceHasWord;
			break;
		}
		// part-of-speech tagging
		List<TaggedWord> tagged = parser.tagger.tagSentence(sentence);
		// dependency syntax parsing
		GrammaticalStructure gs = parser.parser.predict(tagged);
		
		// Reading the parsed sentence into ParseTree
		int N = sentence.size()+1;
		Node[] nodes = new Node[N];
		root = new Node(0, "ROOT", "ROOT");
		nodes[0] = root;
		for (int i = 0; i < N-1; i++) {
			nodes[i+1] = new Node(i+1, 
					sentence.get(i).word(), tagged.get(i).tag());
		}
		for (TypedDependency typedDep : gs.allTypedDependencies()) {
			int from = typedDep.gov().index();
			int to   = typedDep.dep().index();
			// String label = typedDep.reln().getShortName(); // omitting the label
			nodes[to].parent = nodes[from];
			nodes[from].children.add(nodes[to]);
		}
	}

	public ParseTree(Node node) {
		root = node.clone();
	}
	public ParseTree(ParseTree other) {
		this(other.root);
	}
	
	@Override
	public int size() {
		return root.genNodesArray().length;
	}

	@Override
	public int getEdit() {
		return edit;
	}
	
	@Override
	public void setEdit(int edit){
		this.edit = edit;
	}
	
	/**
	 * Helper method for {@link #removeMeaninglessNodes()}.
	 * (1) If curr node is meaning less, link its children to its parent.
	 * (2) Move on to remove the meaningless nodes of its children.
	 */
	private void removeMeaninglessNodes(Node curr) {
		if (curr == null) { return; }
		List<Node> currChildren = new ArrayList<>(curr.getChildren());
		for (Node child : currChildren) {
			removeMeaninglessNodes(child);
		}
		if (curr != root && curr.getInfo().getType().equals("UNKNOWN")) {
			curr.parent.getChildren().remove(curr);
			for (Node child : curr.getChildren()) {
				curr.parent.getChildren().add(child);
				child.parent = curr.parent;
			}	
		}

	}
	
	/**
	 * Remove a node from tree if its NodeInfo is ("UNKNOWN", "meaningless").
	 * To remove the meaningless node, link the children of this node
	 * to its parent.
	 */
	@Override
	public void removeMeaninglessNodes() {
		if (root.getChildren().get(0).getInfo() == null) {
			System.out.println("ERR! Node info net yet mapped!");
		}
		// Remove meaningless nodes.
		removeMeaninglessNodes(root);
	}
	
	@Override
	
	/*I am assuming the tree is mapped as (b) in figure 7 on page 7 
	 *and the tree is mapped correctly in preorder*/
	public void insertImplicitNodes() {
		List <Node> childrenOfRoot = root.getChildren();
		
		// no condition
		if (childrenOfRoot.size() <= 1) {
			
			return;
		}
		
		//one or more condition
		
		for (int i = 0; i < childrenOfRoot.size(); i ++) {
			
			if (childrenOfRoot.get(i).getInfo().getType().equals("SN")) {
				
				
			}
		}
	}
	
	
	
	@Override
	public ParseTree mergeLNQN(){   
		Node[] nodes = this.root.genNodesArray();
		for (int i=0; i<this.size(); i++){
			if (nodes[i].getInfo().getType().equals("LN") || nodes[i].getInfo().getType().equals("QN")){
				String word = "("+nodes[i].getWord()+")";
				String parentWord = nodes[i].getParent().getWord()+word;
				nodes[i].getParent().setWord(parentWord);
				removeNode(nodes[i]);
			}
		}
		ParseTree tree = new ParseTree (root);
		return tree;
	}

	private void removeNode (Node curNode) {   //remove this node by changing parent-children relationship
		curNode.getParent().getChildren().remove(curNode);
		for (Node child: curNode.getChildren()) {
			child.setParent(curNode.getParent()); 
		}
	}

	@Override
	public List<ParseTree> getAdjustedTrees() {
		return TreeAdjustor.getAdjustedTrees(this);
	}	
	
	@Override
	public SQLQuery translateToSQL() {
		return SQLTranslator.translate(root);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 17;
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParseTree other = (ParseTree) obj;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		return true;
	}

	/**
	 * Return an array of nodes in the tree, shallow copy.
	 * @return
	 */
	public Node[] genNodesArray() {
		return root.genNodesArray();
	}
	
	/**
	 * Pre-order iterator
	 * @author keping
	 */
	public class ParseTreeIterator implements Iterator<Node> {
		LinkedList<Node> stack = new LinkedList<>();
		ParseTreeIterator() {
			stack.push(root);
		}
		@Override
		public boolean hasNext() {
			return !stack.isEmpty(); 
		}
		@Override
		public Node next() {
			Node curr = stack.pop();
			List<Node> children = curr.getChildren();
			for (int i = children.size()-1; i >= 0; i--) {
				stack.push(children.get(i));
			}
			return curr;
		}
	}
	
	/**
	 * The default iterator in ParseTree returns the Nodes
	 * using pre-order of the tree.
	 */
	@Override
	public ParseTreeIterator iterator() { return new ParseTreeIterator(); }
	
	/**
	 * Get the natural language sentence corresponding to this
	 * parse tree.
	 * @return sentence
	 */
	public String getSentence() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Node node : this) {
			if (first) {
				sb.append(node.getWord());
				first = false;
			} else {
				sb.append(" ").append(node.getWord());
			}
		}
		return sb.toString();
	}
	
	/**
	 * toString like "curr -> [child1, child2, ...]"
	 * @param curr
	 * @return
	 */
	private String nodeToString(Node curr) {
		if (curr == null) { return ""; }
		String s = curr.toString() + " -> ";
		s += curr.getChildren().toString() + "\n";
		for (Node child : curr.getChildren()) {
			s += nodeToString(child);
		}
		return s;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Sentence: ").append(getSentence()).append("\n");
		sb.append(nodeToString(root));
		return sb.toString();
	}
	
	public int getScore(){
		return SyntacticEvaluator.numberOfInvalidNodes(this);
	}
	
}
