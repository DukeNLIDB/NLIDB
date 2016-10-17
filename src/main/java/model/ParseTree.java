package model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * Immutable class for parseTree.
 * @author keping
 *
 */
public class ParseTree {
	/**
	 * Immutable inner class representing a dependency relation.
	 * @author keping
	 */
	class Dep {
		int from;
		int to;
		String label;
		Dep(int from, int to, String label) {
			this.from = from;
			this.to = to;
			this.label = label;
		}
	}

	
	/**
	 * Length including ROOT.
	 */
	private int N;
	/**
	 * words.get(0).equals("ROOT")
	 */
	private ArrayList<String> words;
	private ArrayList<String> tags;
	private ArrayList<List<Dep>> children;
	private ArrayList<Dep> parent;
	/**
	 * If this is not null, then nodes have already been mapped.
	 */
	private ArrayList<Node> nodes; 
	
	/**
	 * Construct a parse tree using the stanford NLP parser. Only one sentence.
	 * @param text input text.
	 */
	public ParseTree(String text, NLParser parser) {
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		List<HasWord> sentence = null;
		for (List<HasWord> sentenceHasWord : tokenizer) {
			sentence = sentenceHasWord;
			break;
		}
		List<TaggedWord> tagged = parser.tagger.tagSentence(sentence);
		GrammaticalStructure gs = parser.parser.predict(tagged);
		this.N = sentence.size()+1;
		words = new ArrayList<String>(N+1);
		tags = new ArrayList<String>(N+1);
		words.add("ROOT");
		tags.add("ROOT");
		for (int i = 0; i < N-1; i++) {
			words.add(sentence.get(i).word());
			tags.add(tagged.get(i).tag());
		}
		
		children = new ArrayList<>(N+1);
		parent   = new ArrayList<>(N+1);
		for (int i = 0; i < N+1; i++) {
			children.add(null);
			parent.add(null);
		}
		
		for (TypedDependency typedDep : gs.allTypedDependencies()) {
			int from = typedDep.gov().index();
			int to   = typedDep.dep().index();
			String label = typedDep.reln().getShortName();
			Dep dep = new Dep(from, to, label);
			parent.set(to, dep);
			if (children.get(from) == null) {
				children.set(from, new ArrayList<Dep>());
			}
			children.get(from).add(dep);
		}
		
		nodes = new ArrayList<>(Arrays.asList(new Node[N]));
	}
	
	public int length() { return N; }
	
	public void setNode(int i, Node node) { this.nodes.set(i, node); }
	public void setNodes(ArrayList<Node> nodes) { this.nodes = nodes; }
	
	public String getWord(int i) {
		return words.get(i);
	}
	
	/**
	 * Don't print out ROOT;
	 */
	@Override
	public String toString() {
		String s = "";
		for (int i = 1; i < N; i++) {
			s += "("+i+")" + words.get(i)+"/"+tags.get(i)+" ";
		}
		s += "\nDependencies:\n";
		for (Dep dep : parent) {
			if (dep != null) {
				s += dep.label+"("+dep.from+"->"+dep.to+")"+" ";
			}
		}
		return s;
	}
	
	
	public String sentenceToString() {
		String s = "";
		if (N >= 2) { s += words.get(1); }
		for (int i = 2; i < N; i++) {
			s += " "+words.get(i);
		}
		return s;
	}
	
	public String nodesToString() {
		String s = "";
		for (int i = 1; i < N; i++) {
			s += "("+i+")"+words.get(i)+"("+nodes.get(i)+")\n";
		}
		return s;
	}
	
	public static void main(String[] args) {
		NLParser parser = new NLParser();
		String text = "I can almost always tell when movies use fake dinosaurs.";
		ParseTree tree = new ParseTree(text, parser);
		System.out.println(tree);
	}
}
