package model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class ParseTree implements IParseTree {
	// TODO: all fields should be private in final version.
	
	/**
	 * Number of nodes in the ParseTree, including root Node.
	 */
	int N;
	
	/**
	 * Order of parse tree reformulation (used in QueryTree)
	 */
	int edit;
	/**
	 * An array of nodes, with the order in the sentence.
	 */
	Node[] nodes;
	/**
	 * Root Node
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
		nodes = new Node[N];
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
	
	@Override
	public void removeMeaninglessNodes() {
		
		for (int i = 0; i < N; i ++) {
			NodeInfo temp = nodes[i].getInfo();
			if(temp.getValue().equals("meaningless")) {
				configureDeletingNode(i);
			}
		}
		
		int Ntemp = N;
		for (int i = 0; i < Ntemp; i ++) {
			if (nodes[i] == null) {
				if (i != Ntemp - 1) {
					nodes[i] = nodes[i + 1];
				}
				N --; 
			}
		}
	}
	
	@Override
	public void mergeLNQN(){
		for (int i=0; i<N; i++){
			if (nodes[i].getInfo().getValue().equals("LN") || nodes[i].getInfo().getValue().equals("QN")){
				String word = "("+nodes[i].getWord()+")";
				String parentWord = nodes[i].parent.getWord()+word;
				nodes[i].parent.setWord(parentWord);
				configureDeletingNode(i);
			}
		}
		
		int Ntemp = N;
		for (int i = 0; i < Ntemp; i ++) {
			if (nodes[i] == null) {
				if (i != Ntemp - 1) {
					nodes[i] = nodes[i + 1];
				}
				N --; 
			}
		}
	}

	void configureDeletingNode (int index) {
	
		nodes[index].parent.children.remove(nodes[index]);
		for (int i = 0; i < nodes[index].children.size(); i ++) {
			nodes[index].children.get(i).parent = nodes[index].parent; 
		}
		nodes[index] = null;
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
	
	
	List<ParseTree> adjustor (ParseTree T){
		List<ParseTree> treeList = new ArrayList<ParseTree>();
		
		
		return treeList;
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
		for (int i=0; i<T.size(); i++){
			Node curNode = T.nodes[i];
			String curType = curNode.getInfo().getType();
			String parentType = curNode.parent.getInfo().getType();
			List<Node> children = curNode.children;
			int sizeOfChildren = children.size();
			if (curType.equals("SN")){ // select node
				//SN can only be child of root
				if (!parentType.equals("ROOT"))   
					numOfInv++;
				//SN can only have one child from FN or NN
				else if (sizeOfChildren != 1)
					numOfInv++;
				else{
					String childType = children.get(0).getInfo().getType();
					if (!(childType.equals("NN") || childType.equals("FN")))
						numOfInv++;
				}
			}
			else if (curType.equals("ON")){  //operator node
				if (parentType.equals("ROOT")){
					if (sizeOfChildren == 0)
						numOfInv++;
					else{
						for (int j = 0; j<sizeOfChildren; j++){
							String childType = children.get(j).getInfo().getType();
							if (childType.equals("ON")){
								numOfInv++;
								break;
							}
						}
					}
				}
				else if (parentType.equals("NN")){
					if (sizeOfChildren != 1)
						numOfInv++;
					else if (!children.get(0).getInfo().getType().equals("VN"))
						numOfInv++;
				}
			}
			else if (curType.equals("NN")){  //name node
				//NP=NN+NN*Condition. Second NN has no child.
				if (parentType.equals("NN")){
					if (sizeOfChildren != 0)
						numOfInv++;
				}
				//SN+GNP, or ON+GNP, or FN+GNP. and GNP=NP=NN+NN*Condition. First NN can have any number of children from NN,ON,VN.
				else if (parentType.equals("SN") || parentType.equals("FN") || parentType.equals("ON")){
					if (sizeOfChildren != 0){
						for (int j = 0; j < sizeOfChildren; j++){
							String childType = children.get(j).getInfo().getType();
							if (!(childType.equals("NN") || childType.equals("VN") || childType.equals("ON"))){
								numOfInv++;
								break;
							}
						}
					}
				}
				//NN cannot be a child of VN
				else if (parentType.equals("VN")){
					numOfInv++;
				}
			}
			else if (curType.equals("VN")){  //value node
				if (curNode.children != null)  //VN cannot have children
					numOfInv++;
				else if (!(parentType.equals("ON") || parentType.equals("NN")))  //VN can only be child of ON and NN
					numOfInv++;
			}
			else if (curType.equals("FN")){  //function nodes
				//ON+FN, or ON+GNP, or SN+GNP, or FN+GNP. and GNP=FN+GNP
				//FN can be child of ON, without children or only 1 child of NN or FN
				//FN can be child of SN, wih only 1 child of NN or FN
				//FN can be child of FN, wih only 1 child of NN or FN
				if (sizeOfChildren == 0){
					if (!parentType.equals("ON"))
						numOfInv++;
				}
				else if (sizeOfChildren == 1){
					String childType = children.get(0).getInfo().getType();
					if (!(parentType.equals("ON") || parentType.equals("SN") || parentType.equals("FN")))
						numOfInv++;
					else if (!(childType.equals("NN") || childType.equals("FN")))
						numOfInv++;
				}
				else
					numOfInv++;
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
	public void insertImplicitNodes() {
		// TODO Auto-generated method stub

	}

	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SQLQuery translateToSQL() {
		SQLQuery query = new SQLQuery();
		if (!root.getInfo().getType().equals("SN")) { return query; }
		for (Node NN : root.getChildren()) {
			if (!NN.getInfo().getType().equals("NN")) { continue; }
			query.add("SELECT", NN.getInfo().getValue());
			query.add("FROM", NN.getInfo().getValue().split(":")[0]);
			for (Node VN : NN.getChildren()) {
				String compareSymbol = "=";
				if (!VN.getChildren().isEmpty()) {
					Node ON = VN.getChildren().get(0);
					if (ON.getInfo().getType().equals("ON")) {
						compareSymbol = ON.getInfo().getValue();
					}
				}
				query.add("WHERE", VN.getInfo().getValue() + " " +
						compareSymbol + " " + VN.getWord());
			}
		}
		return query;
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
		String s = "";
		s += nodes[1].getWord();
		for (int i = 2; i < N; i++) {
			s += " "+nodes[i].getWord();
		}
		return s;
	}
	
	@Override
	public String toString() {
		// TODO
		return "";
	}
	
}
