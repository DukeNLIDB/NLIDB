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
	// TODO: all fields should be private in final version.
	
	/**
	 * Number of nodes in the ParseTree.
	 */
	int N;
	
	/**
	 * Order of parse tree reformulation (used in getAdjustedTrees())
	 */
	int edit;
	/**
	 * An array of nodes, with the order in the sentence.
	 */
	Node[] nodes;
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
		N = sentence.size()+1;
		nodes = new Node[3 * N];
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
	
	/**
	 * convert a root node representation to ParseTree representation
	 * @param root
	 * @return
	 */
	public static ParseTree nodeToTree(Node root){
		ParseTree tree = new ParseTree();
		tree.root = root;
		tree.N = Node.count(root);
		tree.nodes = new Node[tree.N];
		LinkedList<Node> stack = new LinkedList<Node>();
		stack.push(tree.root);
		int index = 0;
		while (!stack.isEmpty()){
			Node currentNode = stack.poll();
			tree.nodes[index++] = currentNode;
			List<Node> children = currentNode.getChildren();
			int numOfChildren = children.size();
			for (int i = numOfChildren-1; i>=0; i--){
				stack.push(children.get(i));
			}
		}
		return tree;
		
	}


	@Override
	public int size() {
		return N;
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
		// Put nodes back in Node[] using pre-order traversal
		List<Node> nodesList = new ArrayList<>();
		LinkedList<Node> stack = new LinkedList<>();
		stack.push(root);
		while (!stack.isEmpty()) {
			Node curr = stack.pop();
			nodesList.add(curr);
			List<Node> currChildren = curr.getChildren();
			for (int i = currChildren.size()-1; i >= 0; i--) {
				stack.push(currChildren.get(i));	
			}
		}
		N = nodesList.size();
		nodes = new Node[N];
		for (int i = 0; i < N; i++) {
			nodes[i] = nodesList.get(i);
		}
	}
	
	@Override
	
	/*I am assuming the tree is mapped as (b) in figure 7 on page 7 
	 *and the tree is mapped correctly in preorder*/
	public void insertImplicitNodes() {
		
		//iterate all node to find SN node.
		
		int SN_index = 0;
		
		for (int i = 0; i < N; i ++) {
		
			if (nodes[i].getInfo().getType().equals("SN")) {
			
				SN_index = i;
				break;	
			}
		}
		
		//start from SN node, get all children index
		
		int endOfLeftTree = SN_index;
		int startOfLeftTree = SN_index + 1;
		
		for (int i = startOfLeftTree; i < N; i ++) {
			
			if (nodes[i].getParent().getIndex() == endOfLeftTree) {
				endOfLeftTree = i;
			}
			else {
				break;
			}
		}
		int rightRoot = endOfLeftTree + 1;
		int endOfMidTree = implicitHelper(startOfLeftTree, endOfLeftTree, rightRoot);
		implicitHelper(rightRoot + 1, endOfMidTree, endOfMidTree);
	}
	
	public int implicitHelper (int startOfLeftTree, int endOfLeftTree, int rightRoot) {
		
		int startOfCurrentTree = rightRoot + 1;
		int endOfCurrentTree = rightRoot;
		boolean firstChild = true;
		
		for (int i = rightRoot + 1; i < N; i ++) {
			
			if (nodes[i].getParent().getIndex() == rightRoot &&
				!firstChild) {
				endOfCurrentTree = i - 1;
				break;
			}
			
			if(firstChild) {firstChild = false;}
		}
		//compare
		
		int [] hit = new int[N]; 
		int hitindex = 0;
		
		for (int i = startOfLeftTree; i < endOfLeftTree; i ++) {
			for (int j = startOfCurrentTree; j < endOfCurrentTree + 1; j ++) {
				if (nodes[i] == nodes[j]) {
					hit[hitindex] = i;
				}
			}
		}
		
		//insert
			
		for (int j = startOfLeftTree; j < endOfLeftTree; j ++) {
			boolean found = false;
			for (int i = 0; i < hitindex; i ++) {
				
				if (nodes[i] == nodes[j]) {  //corrected by Sandy, not sure
					found = true; 
					break;
				}
			}
			
			if (!found) {
				modNodes(nodes[j], endOfCurrentTree);
				endOfCurrentTree ++;
				N ++;
			}
		}
		
		return endOfCurrentTree;
	}
	
	public void modNodes (Node n, int endOfCurrentTree) {
		
		Node nn = new Node(endOfCurrentTree + 1, n.getWord(), n.getPosTag());
		nn.setParent(nodes[endOfCurrentTree]);
		nodes[endOfCurrentTree].setChild(nn);
		
		for (int i = N - 1; i > endOfCurrentTree; i --) {
		
			if (i == endOfCurrentTree + 1) {
				nodes[i] = nn;
			}
			else {
				nodes[i] = nodes[i - 1];
				nodes[i].setIndex(i);
			}
		}
		
	}
	
	@Override
	public ParseTree mergeLNQN(){   
		for (int i=0; i<N; i++){
			if (nodes[i].getInfo().getType().equals("LN") || nodes[i].getInfo().getType().equals("QN")){
				String word = "("+nodes[i].getWord()+")";
				String parentWord = nodes[i].getParent().getWord()+word;
				nodes[i].getParent().setWord(parentWord);
				removeNode(nodes[i]);
			}
		}
		ParseTree tree = nodeToTree(root);
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

	public class ParseTreeIterator implements Iterator<Node> {
		int i = 1;
		@Override
		public boolean hasNext() {
			return i < N; 
		}
		@Override
		public Node next() { return nodes[i++]; }
	}
	
	/**
	 * The default iterator in ParseTree returns the Nodes
	 * using their order in the sentence.
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
		sb.append(nodes[1].getWord());
		for (int i = 2; i < N; i++) {
			sb.append(" ").append(nodes[i].getWord());
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
