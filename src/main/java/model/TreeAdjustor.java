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
	public static List<ParseTree> adjust (ParseTree tree){ 
		List<ParseTree> treeList = new ArrayList<ParseTree>();
		
		List<Node> noChildNodes = new LinkedList<Node>();
		for (int i = 0; i<tree.size(); i++){
			if (tree.nodes[i].getChildren().size() == 0)
				noChildNodes.add(tree.nodes[i]);
		}
		
		int numOfNoChildNodes = noChildNodes.size();
		Random r = new Random();
		int index = r.nextInt(numOfNoChildNodes);  //selected terminal node to be moved, index from 0 to numOfChildNodes-1
		Node moveNode = noChildNodes.get(index);
		int moveNodeIndex = moveNode.getIndex();
		System.out.println(moveNode);
		System.out.println(moveNodeIndex);
		Node moveNodeParent = moveNode.getParent();
		
		for (int i = 0; i < tree.size(); i++){
			if (!tree.nodes[i].equals(moveNodeParent) && !tree.nodes[i].equals(moveNode)){ //Object.equals(Object): value comparison rather than reference comparison
				Node curNode = tree.nodes[i];
				List<Node> curChildren = curNode.getChildren();
				int curChildrenSize = curChildren.size();
				if (curChildrenSize == 0){
					treeList.add(moveNode(tree,moveNodeIndex,i,curChildrenSize,0));
				}
				else {
					for (int j = 0; j <= curChildrenSize; j++){
						treeList.add(moveNode(tree,moveNodeIndex,i,curChildrenSize,j));
					}
				}
			}
		}
		return treeList;
	}
	
	/**
	 * move the selected leaf node as a new child of a node in the tree
	 * @param T
	 * @param MoveNode
	 * @param targetNode
	 * @param childrenSize
	 * @param i
	 * @return
	 */
	static ParseTree moveNode (ParseTree T, int MoveNode, int targetNode, int childrenSize, int i){	
		Node newRoot = T.root.clone();

		//int indexOfMoveNode = temp.nodes.indexOf(moveNode);
		Node tempMoveNode = temp.nodes[MoveNode];
		System.out.println("tempMoveNode: "+tempMoveNode);
		//System.out.println(tempMoveNode);
		Node moveNodeParent = tempMoveNode.getParent();
		moveNodeParent.getChildren().remove(tempMoveNode);
		tempMoveNode.setParent(null);
		
		Node curNode = temp.nodes[targetNode];
		
		if (childrenSize == i){    //add a new child to the target node
			curNode.setChild(tempMoveNode);
			tempMoveNode.setParent(curNode);
		}
		else if (i < childrenSize){   // convert ith child to this node
			Node downChild = curNode.getChildren().get(i);
			curNode.getChildren().remove(downChild);
			curNode.getChildren().add(tempMoveNode);
			tempMoveNode.getChildren().add(downChild);
			tempMoveNode.setParent(curNode);
			downChild.setParent(tempMoveNode);
		}

		temp.generateNewTree();  //change temp, and return temp

		return temp;
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
			List<ParseTree> treeList = TreeAdjustor.adjust(oriTree);
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
