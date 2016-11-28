package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

public class TreeAdjustor {
	
	private static final int MAX_EDIT = 10;

	/**
	 * move one random terminal node (without children) to anywhere possible
	 * @param tree
	 * @return
	 */
	public static List<Node> adjust (ParseTree tree){ 
		List<Node> nodeList = new ArrayList<Node>();
		
		List<Node> noChildNodes = new LinkedList<Node>();
		for (int i = 0; i<tree.size(); i++){
			if (tree.nodes[i].getChildren().size() == 0)
				noChildNodes.add(tree.nodes[i]);
		}
		
		int numOfNoChildNodes = noChildNodes.size();
		Random r = new Random();
		int index = r.nextInt(numOfNoChildNodes);  //selected terminal node to be moved, index from 0 to numOfChildNodes-1
		Node moveNode = noChildNodes.get(index);
		System.out.println(moveNode);
		Node moveNodeParent = moveNode.getParent();
		
		for (int i = 0; i < tree.size(); i++){
			Node curNode = tree.nodes[i];
			List<Node> children = curNode.getChildren();
			int childrenSize = children.size();   //number of children of the target node
			if (!curNode.equals(moveNodeParent) && !curNode.equals(moveNode)){ //Object.equals(Object): value comparison rather than reference comparison
				for (int j = 0; j <= childrenSize; j++){
					nodeList.add(moveNode(tree.root,moveNode,curNode,childrenSize,j));
				}
			}
			else if (curNode.equals(moveNodeParent)){
				for(int j = 0; j < childrenSize; j++){
					if (!children.get(j).equals(moveNode))
						nodeList.add(moveNode(tree.root,moveNode,curNode,childrenSize,j));
				}
			}
		}
		return nodeList;
	}
	
	/**
	 * move the selected leaf node as a new child of a node in the tree
	 * @param r
	 * @param m
	 * @param c
	 * @param childrenSize
	 * @param i
	 * @return
	 */
	static Node moveNode (Node r, Node m, Node c, int childrenSize, int i){	
		Node root = r.clone();

		Node moveNode = findNode(root, m); System.out.println(moveNode);
		Node currentNode = findNode(root, c);  System.out.println(currentNode);

		System.out.println(childrenSize);
		System.out.println(i);
		Node moveNodeParent = moveNode.getParent();
		
		if (childrenSize == i){    //add a new child to the target node
			moveNodeParent.getChildren().remove(moveNode);
			moveNode.setParent(null);
			currentNode.getChildren().add(moveNode);  //TODO:add to the end of ArrayList<Node> children, needs to be added to every position
			moveNode.setParent(currentNode);
		}
		else if (i < childrenSize){   // convert ith child to this node
			Node downChild = currentNode.getChildren().get(i);
			moveNodeParent.getChildren().remove(moveNode);
			moveNode.setParent(null);
			currentNode.getChildren().add(i,moveNode); //maintain the pre order
			currentNode.getChildren().remove(downChild);
			moveNode.getChildren().add(downChild);
			moveNode.setParent(currentNode);
			downChild.setParent(moveNode);
		}

		return root;
	}
	
	static Node findNode(Node root, Node target){
		if (root == null) return null;
		if (root.equals(target)) return root;
		else if (!root.equals(target)) {
			for (Node child: root.getChildren()){
				Node result = findNode (child, target);
				if (result != null){
					return result;
				}
			}
			return null;
		}
		return null;
	}
	
	public static List<IParseTree> getAdjustedTrees(ParseTree tree) {
		List<IParseTree> results = new ArrayList<IParseTree>();
		PriorityQueue<ParseTree> Q = new PriorityQueue<ParseTree>();
		Q.add(tree);
		HashMap<Integer, ParseTree> H = new HashMap<Integer, ParseTree>();
		H.put(hashing(tree), tree);
		tree.setEdit(0);
		
		while (Q.size() > 0){
			ParseTree oriTree = Q.poll();
			List<Node> treeList = TreeAdjustor.adjust(oriTree);
			double treeScore = SyntacticEvaluator.numberOfInvalidNodes(oriTree);
			
			for (int i = 0; i < treeList.size(); i++){
				ParseTree currentTree = treeList.get(i);
				int hashValue = hashing(currentTree);
				if (oriTree.getEdit()<MAX_EDIT && !H.containsKey(hashValue)){
					H.put(hashValue, currentTree);
					currentTree.setEdit(oriTree.getEdit()+1);
					if (SyntacticEvaluator.numberOfInvalidNodes(currentTree) <= treeScore){
						Q.add(currentTree);
						results.add(currentTree);
					}
				}
			}
		}
		return results;
	}
}
