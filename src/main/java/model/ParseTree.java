package model;

import java.io.StringReader;
import java.util.Iterator;
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
	 * Order of parse tree reformulation (used in QueryTree)
	 */
	int edit;
	/**
	 * An array of nodes, with the order in the sentence.
	 */
	Node[] nodes;
	/**
	 * Root Node. Supposed to be "return" or some other word corresponding to "SELECT".
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
		N = sentence.size();
		nodes = new Node[N];
		for (int i = 0; i < N; i++) {
			nodes[i] = new Node(i, 
					sentence.get(i).word(), tagged.get(i).tag());
		}
		root = nodes[0];
		for (TypedDependency typedDep : gs.allTypedDependencies()) {
			int from = typedDep.gov().index();
			if (from == 0) { continue; } // skip ROOT
			int to   = typedDep.dep().index();
			// String label = typedDep.reln().getShortName(); // omitting the label
			nodes[to-1].parent = nodes[from-1];
			nodes[from-1].children.add(nodes[to-1]);
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

	public void configureDeletingNode (int index) {
	
		nodes[index].parent.children.remove(nodes[index]);
		for (int i = 0; i < nodes[index].children.size(); i ++) {
			nodes[index].children.get(i).parent = nodes[index].parent; 
		}
		nodes[index] = null;
	}
	
	
	
	@Override
	public List<IParseTree> getAdjustedTrees() {
		// TODO Auto-generated method stub
		return null;
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
		int i = 0;
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
		sb.append(nodes[0].getWord());
		for (int i = 1; i < N; i++) {
			sb.append(" ").append(nodes[i].getWord());
		}
		return sb.toString();
	}
	
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
