package model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Random;

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
		
		//iterate all node and find if there is missing nodes
		
		int SN_index = 0;
		
		for (int i = 0; i < N; i ++) {
		
			if (nodes[i].getInfo().getType().equals("SN")) {
			
				SN_index = i;
				break;	
			}
		}
		
		
		int [] leftTreeNodes = new int [N];
		
		int parentIndex = 0;
		
		for (int i = SN_index + 1; i < N; i ++) {
			
			
			
		}
		
		
	}
	
	@Override
	public void mergeLNQN(){   //not change Node.index, but change the numbering in nodes[]
		for (int i=0; i<N; i++){
			if (nodes[i].getInfo().getType().equals("LN") || nodes[i].getInfo().getType().equals("QN")){
				String word = "("+nodes[i].getWord()+")";
				String parentWord = nodes[i].getParent().getWord()+word;
				nodes[i].getParent().setWord(parentWord);
				removeNode(nodes[i]);
			}
		}
		
		generateNewTree();
	}

	void removeNode (Node curNode) {   //remove this node by changing parent-children relationship
		curNode.getParent().getChildren().remove(curNode);
		for (Node child: curNode.getChildren()) {
			child.setParent(curNode.getParent()); 
		}
	}
	
	void generateNewTree(){
		List<Node> tempTree = new ArrayList<Node>();
		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(root);
		System.out.println(root);
		//add nodes from original tree into tempTree in pre order
		while (!queue.isEmpty()){
			Node curNode = queue.poll();
			System.out.println(curNode);
			tempTree.add(curNode);
			List<Node> curChildren = curNode.getChildren();
			int curChildrenSize = curChildren.size();
			for (int i = curChildrenSize-1; i >= 0; i--)
				queue.push(curChildren.get(i));
		}
		N = tempTree.size();
		for (int i = 0; i < N; i++){
			nodes[i] = tempTree.get(i);
		}
		root = nodes[0];
	}

	@Override
	public List<IParseTree> getAdjustedTrees() {
		List<IParseTree> results = new ArrayList<IParseTree>();
		PriorityQueue<ParseTree> Q = new PriorityQueue<ParseTree>();
		Q.add(this);
		HashMap<Integer, ParseTree> H = new HashMap<Integer, ParseTree>();
		H.put(hashing(this), this);
		this.setEdit(0);
		
		while (Q.size() > 0){
			ParseTree oriTree = Q.poll();
			List<ParseTree> treeList = adjustor(oriTree);
			double treeScore = numberOfInvalidNodes(oriTree);
			
			for (int i = 0; i < treeList.size(); i++){
				ParseTree currentTree = treeList.get(i);
				int hashValue = hashing(currentTree);
				if (oriTree.getEdit()<10 && !H.containsKey(hashValue)){
					H.put(hashValue, currentTree);
					currentTree.setEdit(oriTree.getEdit()+1);
					if (numberOfInvalidNodes(currentTree) <= treeScore){
						Q.add(currentTree);
						results.add(currentTree);
					}
				}
			}
		}
		return results;
	}
	
	
	List<ParseTree> adjustor (ParseTree T){ //move one random terminal node (without children) to anywhere possible
		List<ParseTree> treeList = new ArrayList<ParseTree>();
		
		List<Node> noChildNodes = new LinkedList<Node>();
		for (int i = 0; i<T.size(); i++){
			if (T.nodes[i].getChildren() == null)
				noChildNodes.add(T.nodes[i]);
		}
		int numOfNoChildNodes = noChildNodes.size();
		Random r = new Random();
		int index = r.nextInt(numOfNoChildNodes);  //selected terminal node to be moved, index from 0 to numOfChildNodes-1
		Node moveNode = noChildNodes.get(index);
		Node moveNodeParent = moveNode.getParent();
		
		for (int i = 0; i < T.size(); i++){
			if (!T.nodes[i].equals(moveNodeParent)){ //Object.equals(Object): value comparison rather than reference comparison
				Node curNode = T.nodes[i];
				List<Node> curChildren = curNode.getChildren();
				curNode.setChild(moveNode);
				for (Node curChild: curChildren)
					moveNode.setChild(curChild);
				treeList.add(generateNewTree(T));
			}
		}
		return treeList;
	}
	
	ParseTree generateNewTree(ParseTree T){
		ParseTree newTree = new ParseTree();
		List<Node> tempTree = new ArrayList<Node>();
		LinkedList<Node> queue = new LinkedList<Node>();
		queue.add(T.root);
		//add nodes from original tree into tempTree in pre order
		while (!queue.isEmpty()){
			Node curNode = queue.poll();
			System.out.println(curNode);
			tempTree.add(curNode);
			List<Node> curChildren = curNode.getChildren();
			int curChildrenSize = curChildren.size();
			for (int i = curChildrenSize-1; i >= 0; i--)
				queue.push(curChildren.get(i));
		}
		newTree.N = tempTree.size();
		for (int i = 0; i < newTree.N; i++){
			newTree.nodes[i] = tempTree.get(i);
		}
		newTree.root = newTree.nodes[0];
		return newTree;
	}
	
	/**
	 * Number of invalid tree nodes according to the grammar:
	 * Q -> (SClause)(ComplexCindition)*
	 * SClause -> SELECT + GNP
	 * ComplexCondition -> ON + (LeftSubTree*RightSubTree)
	 * LeftSubTree -> GNP
	 * RightSubTree -> GNP | VN | FN
	 * GNP -> (FN + GNP) | NP
	 * NP -> NN + (NN)*(Condition)*
	 * Condition -> VN | (ON + VN)
	 * 
	 * +: parent-child relationship
	 * *: sibling relationship
	 * |: or
	 */	
	int numberOfInvalidNodes (ParseTree T){	
		int numOfInv = 0;   //number of invalid tree nodes
		for (int i=1; i<T.size(); i++){  //starting from SN (leave out ROOT)
			Node curNode = T.nodes[i];
			String curType = curNode.getInfo().getType();
			String parentType = curNode.getParent().getInfo().getType();
			List<Node> children = curNode.getChildren();
			int sizeOfChildren = children.size();
			if (curType.equals("SN")){ // select node
				//SN can only be child of root
				if (!parentType.equals("ROOT")){   
					numOfInv++;
					curNode.isInvalid = true;
				}
				//SN can only have one child from FN or NN
				else if (sizeOfChildren != 1){
					numOfInv++;
					curNode.isInvalid = true;
				}
				else{
					String childType = children.get(0).getInfo().getType();
					if (!(childType.equals("NN") || childType.equals("FN"))){
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
			}
			else if (curType.equals("ON")){  //operator node
				if (parentType.equals("ROOT")){
					if (sizeOfChildren == 0){
						numOfInv++;
						curNode.isInvalid = true;
					}
					else{
						for (int j = 0; j<sizeOfChildren; j++){
							String childType = children.get(j).getInfo().getType();
							if (childType.equals("ON")){
								numOfInv++;
								curNode.isInvalid = true;
								break;
							}
						}
					}
				}
				else if (parentType.equals("NN")){
					if (sizeOfChildren != 1){
						numOfInv++;
						curNode.isInvalid = true;
					}
					else if (!children.get(0).getInfo().getType().equals("VN")){
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
			}
			else if (curType.equals("NN")){  //name node
				//NP=NN+NN*Condition. Second NN has no child.
				if (parentType.equals("NN")){
					if (sizeOfChildren != 0){   //this rule is different from figure 7 (a), but I think this makes sense
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
				//SN+GNP, or ON+GNP, or FN+GNP. and GNP=NP=NN+NN*Condition. First NN can have any number of children from NN,ON,VN.
				else if (parentType.equals("SN") || parentType.equals("FN") || parentType.equals("ON")){
					if (sizeOfChildren != 0){
						for (int j = 0; j < sizeOfChildren; j++){
							String childType = children.get(j).getInfo().getType();
							if (!(childType.equals("NN") || childType.equals("VN") || childType.equals("ON"))){
								numOfInv++;
								curNode.isInvalid = true;
								break;
							}
						}
					}
				}
				//NN cannot be a child of VN
				else if (parentType.equals("VN")){
					numOfInv++;
					curNode.isInvalid = true;
				}
			}
			else if (curType.equals("VN")){  //value node
				if (sizeOfChildren != 0){  //VN cannot have children
					numOfInv++;
					curNode.isInvalid = true;
				}
				else if (!(parentType.equals("ON") || parentType.equals("NN"))){  //VN can only be child of ON and NN
					numOfInv++;
					curNode.isInvalid = true;
				}
			}
			else if (curType.equals("FN")){  //function nodes
				//ON+FN, or ON+GNP, or SN+GNP, or FN+GNP. and GNP=FN+GNP
				//FN can be child of ON, without children or only 1 child of NN or FN
				//FN can be child of SN, wih only 1 child of NN or FN
				//FN can be child of FN, wih only 1 child of NN or FN
				if (sizeOfChildren == 0){
					if (!parentType.equals("ON")){
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
				else if (sizeOfChildren == 1){
					String childType = children.get(0).getInfo().getType();
					if (!(parentType.equals("ON") || parentType.equals("SN") || parentType.equals("FN"))){
						numOfInv++;
						curNode.isInvalid = true;
					}
					else if (!(childType.equals("NN") || childType.equals("FN"))){
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
				else{
					numOfInv++;
					curNode.isInvalid = true;
				}
			}
		}
		
		return numOfInv;
	}
	
	int hashing (ParseTree T){
		int hashValue = 0;
		
		//TODO: how to get a reasonable hash value for each parse tree (with different node orders)
		return hashValue;
	}
	
	

	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public SQLQuery translateToSQL() {
		return SQLTranslator.translate(root);
	}

	@Override
	public boolean equals(IParseTree other) {
		// TODO Auto-generated method stub
		return false;
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
	
}
