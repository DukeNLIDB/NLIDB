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
	
	/**
	 * Number of nodes in the ParseTree, including root Node.
	 */
	private int N;
	/**
	 * An array of nodes, with the order in the sentence.
	 */
	private Node[] nodes;
	/**
	 * Root Node
	 */
	private Node root;
	
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
	public void removeMeaninglessNodes() {
		
		for (int i = 0; i < N; i ++) {
			NodeInfo temp = nodes[i].getInfo();
			if(temp.getValue().equals("meaningless")) {
				
				deleteNode(i);
			}
		}
	}

	public void deleteNode (int index) {
	
		if (index == N - 1) {
		
			nodes[index] = null;
		}

		else {
			for (int i = index + 1; i < N; i ++) {
			
				nodes[i - 1] = nodes[i];
			}
		}
		N --;
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
		// TODO Auto-generated method stub
		return null;
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
			System.out.println("Inside hasNext():");
			System.out.println("i = "+i);
			System.out.println("N = "+N);
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
