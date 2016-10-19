package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class ParseTreeNodeMapper {
	private ParseTree tree;
	private int N;
	private SchemaGraph schema;
	private ArrayList<Node> nodes;
	private Queue<List<Node>> choicesQueue;
	private WordNet wordNet;
	
	/**
	 * Create a shallow copy, only deep of the nodes part.
	 * @param tree
	 * @param schema
	 */
	public ParseTreeNodeMapper(ParseTree tree, SchemaGraph schema, WordNet wordNet) {
		this.schema = schema;
		this.tree = tree;
		this.N = tree.length();
		this.wordNet = wordNet;
		this.nodes = new ArrayList<Node>(Arrays.asList(new Node[tree.length()]));
		choicesQueue = new LinkedList<>();
		
		mapNodes();
	}
	
	private void mapNode(int i) {
		String word = tree.getWord(i);
		String nodeType = "";
		String nodeValue = "";
		if (word.equals("return")) {
			nodeType = "SN";
			nodeValue = "SELECT";
		} else {
			searchName(i);
			return; 
		}
		chooseNode(new Node(i, nodeType, nodeValue));
	}
	
	/**
	 * Search the word over database table and column names.
	 * Add the first 5 choices to a bucket in choicesQueue.
	 * @param word
	 */
	private void searchName(int i) {
		List<Node> choices = new ArrayList<>();
		String word = tree.getWord(i);
		
		for (String tableName : schema.getTableNames()) {
			choices.add(new Node(i,"NN",tableName,similarity(word, tableName)));
			for (String colName : schema.getColumns(tableName)) {
				choices.add(new Node(i,"NN",tableName+"."+colName,
						similarity(word, colName)));
			}
		}
		
		Collections.sort(choices, new Node.ReverseScoreComparator());
		List<Node> shortlistedChoices = new ArrayList<>();
		for (int j = 0; j < Math.min(5, choices.size()); j++) {
			shortlistedChoices.add(choices.get(j));
		}
		choicesQueue.add(shortlistedChoices);
	}
	
	/**
	 * WordNet WUP similarity.
	 * @param word1
	 * @param word2
	 * @return
	 */
	private double semanticalSimilarity(String word1, String word2) {
		return wordNet.similarity(word1, word2);
	}
	/**
	 * Jaccord Coefficient
	 * @param word1
	 * @param word2
	 * @return
	 */
	private double lexicalSimilarity(String word1, String word2) {
		Set<Character> charSet1 = new HashSet<>();
		Set<Character> charSet2 = new HashSet<>();
		Set<Character> commonSet= new HashSet<>();
		for (char c : word1.toCharArray()) { charSet1.add(c); }
		for (char c : word2.toCharArray()) { charSet2.add(c); }
		for (char c : charSet1) {
			if (charSet2.contains(c)) { commonSet.add(c); }
		}
		double jaccord = commonSet.size() / (double) (charSet1.size() +
				charSet2.size() + commonSet.size());
		return Math.sqrt(jaccord);
	}
	private double similarity(String word1, String word2) {
		return Math.max(semanticalSimilarity(word1, word2),
				lexicalSimilarity(word1, word2));
	}
	
	/**
	 * Search the word over database values.
	 * @param word
	 */
	private void searchValue(String word) {
		// TODO
	}
	
	/**
	 * Map nodes from left to right. For uncertain nodes, enqueue the choices
	 * to choicesQueue.
	 */
	private void mapNodes() {
		for (int i = 1; i < N; i++) {
			mapNode(i);
		}
	}
	
	public boolean hasNextChoices() {
		return !choicesQueue.isEmpty();
	}
	
	public List<Node> nextChoices() {
		return choicesQueue.poll();
	}
	
	public void chooseNode(Node node) {
		nodes.set(node.index, node);
	}
	
	public ParseTree getMappedTree() {
		tree.setNodes(nodes);
		return tree;
	}

	
}
