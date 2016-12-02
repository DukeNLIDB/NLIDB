package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class TreeAdjustor {
	
	private static final int MAX_EDIT = 15;
	
	/**
	 * Return the node in the tree that equals to the targetNode.
	 * @param tree
	 * @param targetNode
	 * @return
	 */
	private static Node find(ParseTree tree, Node targetNode) {
		for (Node node : tree) {
			if (node.equals(targetNode)) { return node; }
		}
		return null;
	}
	
	/**
	 * Swap this parent node and a child node.
	 * @param parent
	 * @param child
	 */
	private static void swap(Node parent, Node child) {
		// swap the attributes directly.
		NodeInfo childInfo = child.info;
		String childWord = child.word;
		String childPosTag = child.posTag;
		child.info = parent.info;
		child.word = parent.word;
		child.posTag = parent.posTag;
		parent.info = childInfo;
		parent.word = childWord;
		parent.posTag = childPosTag;
	}
	
	/**
	 * Make the child node a rightmost sibling of the target Node.
	 * @param target
	 * @param child
	 */
	private static void makeSibling(Node target, Node child) {
		List<Node> children = target.getChildren();
		target.children = new ArrayList<Node>();;
		for (Node anyChild : children) {
			if (anyChild != child) { target.getChildren().add(anyChild); }
		}
		target.parent.children.add(child);
		child.parent = target.parent;
	}
	
	/**
	 * Make a sibling the rightmost child of the target.
	 * @param target
	 * @param sibling
	 */
	private static void makeChild(Node target, Node sibling) {
		List<Node> siblings = target.parent.children;
		target.parent.children = new ArrayList<Node>();
		for (Node anySibling : siblings) {
			if (anySibling != sibling) {
				target.parent.children.add(anySibling);
			}
		}
		target.children.add(sibling);
		sibling.parent = target;
	}
	
	/**
	 * <p>Return a list of adjusted trees after one adjustment to the input tree
	 * at the target Node.</p>
	 * <p>Four possible adjustments can be made to that node:</p>
	 * <ol>
	 *   <li>Swap this node with its child. (all possible positions)</li>
	 *   <li>Make child its rightmost sibling.</li> 
	 *   <li>Make sibling its rightmost child.</li>
	 *   <li>Swap leftmost child with other children</li>
	 * </ol>
	 * @param tree
	 * @param targetNode
	 * @return
	 */
	private static Set<ParseTree> adjust(ParseTree tree, Node target) {
		Set<ParseTree> adjusted = new HashSet<>();
		if (target.parent == null) { return adjusted; }
		// (1) Swap target with its children.
		for (Node child : target.getChildren()) {
			ParseTree tempTree = new ParseTree(tree);
			swap(find(tempTree, target), find(tempTree, child));
			adjusted.add(tempTree);
		}
		// (2) Make child its rightmost sibling.
		for (Node child : target.getChildren()) {
			ParseTree tempTree = new ParseTree(tree);
			makeSibling(find(tempTree, target), find(tempTree, child));
			adjusted.add(tempTree);
		}
		// (3) Make its sibling its rightmost child.
		for (Node sibling : target.parent.getChildren()) {
			if (sibling == target) { continue; }
			ParseTree tempTree = new ParseTree(tree);
			makeChild(find(tempTree, target), find(tempTree, sibling));
			adjusted.add(tempTree);
		}
		// (4) Swap leftmost child with other children.
		if (target.getChildren().size() >= 2) {
			List<Node> children = target.getChildren();
			for (int i = 1; i < children.size(); i++) {
				ParseTree tempTree = new ParseTree(tree);
				swap(find(tempTree, children.get(0)),
					 find(tempTree, children.get(i)));
				adjusted.add(tempTree);
			}
		}
		return adjusted;
	}

	/**
	 * Return a set of adjusted trees after one adjustment to the input tree.
	 * @param tree
	 * @return
	 */
	public static List<ParseTree> adjust(ParseTree tree) { 
		Set<ParseTree> treeList = new HashSet<ParseTree>();
		for (Node node : tree) {
			treeList.addAll(adjust(tree, node));
		}
		return new ArrayList<ParseTree>(treeList);
	}
	

	public static List<ParseTree> getAdjustedTrees(ParseTree tree) {
		List<ParseTree> results = new ArrayList<ParseTree>();
		// The top of the pq is the most valid tree (highest score, lowest number of invalid nodes)
		PriorityQueue<ParseTree> queue = new PriorityQueue<ParseTree>((t1,t2) -> ( - t1.getScore() + t2.getScore() ));
		HashMap<Integer, ParseTree> H = new HashMap<Integer, ParseTree>();
		queue.add(tree);
		results.add(tree);
		H.put(tree.hashCode(), tree);
		tree.setEdit(0);
		
		ParseTree treeWithON = tree.addON();
		queue.add(treeWithON);
		results.add(treeWithON);
		H.put(treeWithON.hashCode(), treeWithON);
		treeWithON.setEdit(0);
		
		while (queue.size() > 0){
			ParseTree oriTree = queue.poll();
			if (oriTree.getEdit() >= MAX_EDIT) { continue; }
			List<ParseTree> treeList = TreeAdjustor.adjust(oriTree);
			double numInvalidNodes = SyntacticEvaluator.numberOfInvalidNodes(oriTree);
			
			for (int i = 0; i < treeList.size(); i++){
				ParseTree currentTree = treeList.get(i);
				int hashValue = currentTree.hashCode();
				if ( !H.containsKey(hashValue) ) {
					H.put(hashValue, currentTree);
					currentTree.setEdit(oriTree.getEdit()+1);
					if (SyntacticEvaluator.numberOfInvalidNodes(currentTree) <= numInvalidNodes) {
						queue.add(currentTree);
						results.add(currentTree);
					}
				}
			}
		}
		return results;
	}
}
