package model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
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
	
	public void insertImplicitNodes() {

		List <Node> childrenOfRoot = root.getChildren();
		
		// no condition
		if (childrenOfRoot.size() <= 1) {
			
			
			return;
		}
		
		//phase 1, add nodes under select to left subtree
		
		System.out.println("Phase 1, add nodes under select node to left subtree");

		int IndexOfSN = 0;
		for (int i = 0; i < childrenOfRoot.size(); i ++) {
			
			if (childrenOfRoot.get(i).getInfo().getType().equals("SN")) {
				
				IndexOfSN = i;
				break;
			}
		}

		//start from the name node 

		Node SN = childrenOfRoot.get(IndexOfSN);
		List <Node> SN_children = SN.getChildren();

		int IndexOfSN_NN = 0;


		for (int i = 0; i < SN_children.size(); i ++) {

			if (SN_children.get(i).getInfo().getType().equals("NN")) {

				IndexOfSN_NN = i;
				break;
			}
		}

		//add them to left subtree of all branches

		Node copy;
		int indexOfAppendedNode; 
		Node SN_NN = SN_children.get(IndexOfSN_NN);

		for (int i = 0; i < childrenOfRoot.size(); i ++) {

			if (i != IndexOfSN) {
				
				Node [] nodes_SN_NN = childrenOfRoot.get(i).genNodesArray();
				indexOfAppendedNode = nameNodeToBeAppended(nodes_SN_NN);

				if (indexOfAppendedNode != -1) {

					copy = SN_NN.clone();
					copy.setOutside(true);

					nodes_SN_NN[indexOfAppendedNode].setChild(copy);
					copy.setParent(nodes_SN_NN[indexOfAppendedNode]);
				}
			}
		}
		
		System.out.println(toString() + '\n');
		
		
		//phase 2, compare left core node with right core node
		
		System.out.println("Phase 2, core node insertion");

		int indexOfRightCoreNode = -1;
		int indexOfLeftCoreNode = -1;

		for (int i = 0; i < childrenOfRoot.size(); i ++) {
			
			if (i != IndexOfSN) {
				
				Node [] nodes = childrenOfRoot.get(i).genNodesArray();
				int startOfRightBranch = endOfLeftBranch(nodes) + 1;
				int sizeOfRightTree = nodes[startOfRightBranch].getChildren().size() + 1;

				//if right tree only contains numbers, skip it

				if (sizeOfRightTree != 1 || !isNumeric(nodes[startOfRightBranch].getWord())) {

					indexOfLeftCoreNode = coreNode(nodes, true);
					indexOfRightCoreNode = coreNode(nodes, false);

					//if left core node exists

					if (indexOfLeftCoreNode != -1) {

						boolean doInsert = false;
 
						//if right subtree neither have core node nor it only contains number
						if (indexOfRightCoreNode == -1) {

							//copy core node only
						
							doInsert = true;
						}

						//if right core node & left core node are different schema

						else if (!nodes[indexOfRightCoreNode].getInfo().
								 ExactSameSchema(nodes[indexOfLeftCoreNode].getInfo())) {

							//copy core node only

							doInsert = true;
						}

						if (doInsert) {

							copy = nodes[indexOfLeftCoreNode].clone();
							copy.children = new ArrayList<Node>();
							copy.setOutside(true);
							
							
							boolean insertAroundFN = false;

							int indexOfNewRightCN = IndexToInsertCN(nodes);

							if (indexOfNewRightCN == -1) {

								for (int j = nodes.length - 1; j >  endOfLeftBranch(nodes); j --) {

									if (nodes[j].getInfo().getType().equals("FN")) {

										indexOfNewRightCN = j + 1;
										insertAroundFN = true;
										break;
									}
								}
							}
							
							if (insertAroundFN) {

								//THIS ONLY HANDLES FN NODE HAS NO CHILD OR ONE NAME NODE CHILD

								List <Node> FN_children = nodes[indexOfNewRightCN - 1].getChildren();
								
								for (int j = 0; j < FN_children.size(); j ++) {
									
									copy.setChild(FN_children.get(j));
									FN_children.get(j).setParent(copy);
								}

								copy.setParent(nodes[indexOfNewRightCN - 1]);
								nodes[indexOfNewRightCN - 1].children = new ArrayList<Node>();
								nodes[indexOfNewRightCN - 1].setChild(copy); 
							}

							else {
								
								//if right subtree only contains VN, adjust index

								if (indexOfNewRightCN == -1) {

									indexOfNewRightCN = endOfLeftBranch(nodes) + 1;
								}

								copy.setChild(nodes[indexOfNewRightCN]);
								copy.setParent(nodes[indexOfNewRightCN].getParent());
								nodes[indexOfNewRightCN].getParent().removeChild(nodes[indexOfNewRightCN]);
								nodes[indexOfNewRightCN].getParent().setChild(copy);
								nodes[indexOfNewRightCN].setParent(copy);
								
							}
						}
						
						System.out.println(toString());

						//phase 3, map each NV under left core node to right core node
						
						System.out.println("Phase 3, transfer constrain nodes from left to right");
						
						List <Node> NV_children_left = nodes[indexOfLeftCoreNode].getChildren();

						for (int j = 0; j < NV_children_left.size(); j ++) {

							Node [] nodes_new = childrenOfRoot.get(i).genNodesArray();
							indexOfRightCoreNode = coreNode(nodes_new, false);
							List <Node> NV_children_right = nodes_new[indexOfRightCoreNode].getChildren();

							boolean found_NV = false;

							Node curr_left = NV_children_left.get(j);
							String curr_left_type = curr_left.getInfo().getType();

							for (int k = 0; k < NV_children_right.size(); k ++) {

								//compare

								Node curr_right = NV_children_right.get(k);

								//strictly compare, exact match ON

								if (curr_left_type.equals("ON")) {

									if (curr_left.equals(curr_right)) {

										found_NV = true;
										break;
									}
								}

								else {

									if (curr_left.getInfo().sameSchema(curr_right.getInfo())) {

										found_NV = true;
										break;
									}
								}
							}

							if (!found_NV) {

								//insert

								copy = curr_left.clone();
								nodes_new[indexOfRightCoreNode].setChild(copy);
								copy.setOutside(true);
								copy.setParent(nodes_new[indexOfRightCoreNode]);
							}
						}

						System.out.println(toString());
						
						//phase 4, insert function node
						
						System.out.println("Phase 4, insert missing function node");

						Node [] nodes_final_temp = childrenOfRoot.get(i).genNodesArray();

						int indexOfLeftFN_Tail = -1;

						for (int j = indexOfLeftCoreNode; j > 0; j --) {

							if (nodes_final_temp[j].getInfo().getType().equals("FN")) {

								indexOfLeftFN_Tail = j;
								break;
							}
						}

						if (indexOfLeftFN_Tail != -1) {

							//ASSUMPTION: if FN exists, it always before core node

							for (int k = 1; k < indexOfLeftFN_Tail + 1; k ++) {

								Node [] nodes_final = childrenOfRoot.get(i).genNodesArray();
								indexOfRightCoreNode = coreNode(nodes_final, false);

								boolean found_FN = false;

								for (int j = endOfLeftBranch(nodes_final) + 1; j < indexOfRightCoreNode; j ++) {

									if (nodes_final[j].getInfo().ExactSameSchema(nodes_final[k].getInfo())) {

										found_FN = true;
									}
								}

								if(!found_FN) {
									copy = nodes_final[k].clone();
									copy.setOutside(true);
									copy.children = new ArrayList<Node>();

									nodes[0].removeChild(nodes_final[endOfLeftBranch(nodes_final) + 1]);
									nodes[0].setChild(copy);

									copy.setParent(nodes[0]);
									copy.setChild(nodes[endOfLeftBranch(nodes_final) + 1]);
									nodes[endOfLeftBranch(nodes_final) + 1].setParent(copy);
								}
							}
						}
						System.out.println(toString());
					}
				}
			}
		}
	}

	/**
	 * find the index in the right tree to append core node
	 */

	public int IndexToInsertCN (Node [] nodes) {


		for (int i = endOfLeftBranch(nodes) + 1; i < nodes.length; i ++) {

			if (nodes[i].getInfo().getType().equals("NN")) {

				return i;
			}
		}

		return -1;
	}

	/**
	 * Appending the name node under SELECT to the last name node in leftsubtree
	 */

	public int nameNodeToBeAppended (Node [] nodes) {

		for (int i = endOfLeftBranch(nodes); i > 0; i --) {

			if (nodes[i].getInfo().getType().equals("NN")) {

				return i;
			}
		}

		return -1;
	}
	
	/**
	 * find the index of the last node in the left subtree
	 */

	public int endOfLeftBranch (Node [] nodes) {

		for (int i = 2; i < nodes.length; i ++) {

			if(nodes[i].getParent().equals(nodes[0])) {
				
				return i - 1;
			}

		}

		return -1;
	}

	/**
	 * check if right branch contains only number
	 */
	public boolean isNumeric(String str)  {  
  		try  {  
    		double d = Double.parseDouble(str);  
  		}  
  		catch(NumberFormatException e) {  
    		return false;  
  		}  
  		return true;  
	}

	/**
	 * find index of core node
	 */

	public int coreNode (Node [] nodes, boolean left) {

		int startIndex = 1;
		int endIndex = endOfLeftBranch(nodes);

		if (!left) {

			startIndex = endOfLeftBranch(nodes) + 1;
			endIndex = nodes.length - 1;
		}

		for (int i = startIndex; i <= endIndex; i ++) {

			if (nodes[i].getInfo().getType().equals("NN")) {

				return i;
			}
		}

		return -1;
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
			curNode.getParent().setChild(child);
		}
	}
	
	public ParseTree addON(){
		Node root = this.root.clone();
		Node on = new Node (0,"equals", "postag");
		on.info = new NodeInfo ("ON", "=");
		root.setChild(on);
		on.setParent(root);	
		ParseTree tree = new ParseTree(root);
		return tree;
	}

	/**
	 * For now, return the first three trees for choices.
	 * First order on higher validity score, second order on lower edits.
	 */
	@Override
	public List<ParseTree> getAdjustedTrees() {
		List<ParseTree> result = TreeAdjustor.getAdjustedTrees(this);
		Collections.sort(result, (t1, t2) -> {
			if (t1.getScore() != t2.getScore()) {
				return - t1.getScore() + t2.getScore();
			} else {
				return t1.getEdit() - t2.getEdit();
			}
		});
		return result.subList(0, 4);
	}	
	
	/**
	 * Only for testing.
	 * @return
	 */
	@Deprecated
	public SQLQuery translateToSQL() {
		return translateToSQL(null);
	}
	
	@Override
	public SQLQuery translateToSQL(SchemaGraph schema) {
		SQLTranslator translator = new SQLTranslator(root, schema);
		return translator.getResult(); 
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
	
	/**
	 * Score of a tree measures the syntactic legality of 
	 * the tree. It is negative number of Invalid nodes.
	 * @return
	 */
	public int getScore(){
		return - SyntacticEvaluator.numberOfInvalidNodes(this);
	}
	
}
