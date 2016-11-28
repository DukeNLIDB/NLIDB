package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TreeAdjustor {

	/**
	 * move one random terminal node (without children) to anywhere possible
	 * @param tree
	 * @return
	 */
	public static List<ParseTree> adjustor (ParseTree tree){ 
		List<ParseTree> treeList = new ArrayList<ParseTree>();
		
		List<Node> noChildNodes = new LinkedList<Node>();
		for (int i = 0; i<T.size(); i++){
			if (T.nodes[i].getChildren().size() == 0)
				noChildNodes.add(T.nodes[i]);
		}
		
		int numOfNoChildNodes = noChildNodes.size();
		Random r = new Random();
		int index = r.nextInt(numOfNoChildNodes);  //selected terminal node to be moved, index from 0 to numOfChildNodes-1
		Node moveNode = noChildNodes.get(index);
		int moveNodeIndex = moveNode.getIndex();
		System.out.println(moveNode);
		System.out.println(moveNodeIndex);
		Node moveNodeParent = moveNode.getParent();
		//System.out.println(moveNodeParent+"\n");
		
		for (int i = 0; i < this.size(); i++){
			if (!this.nodes[i].equals(moveNodeParent) && !this.nodes[i].equals(moveNode)){ //Object.equals(Object): value comparison rather than reference comparison
				Node curNode = this.nodes[i];
				List<Node> curChildren = curNode.getChildren();
				int curChildrenSize = curChildren.size();
				if (curChildrenSize == 0){
					treeList.add(moveNode(this,moveNodeIndex,i,curChildrenSize,0));
					System.out.println("T after calling moveNode(): ");
					for (int n = 0; n<this.N; n++){
						System.out.println(this.nodes[n].getWord()+this.nodes[n].getIndex());
					}
					for (int n = 0; n<this.N; n++){
						List<Node> children = this.nodes[n].children;
						int sizeOfChildren = children.size();
						if (sizeOfChildren != 0){
							for (int j=0; j<sizeOfChildren; j++)
								System.out.println(n+"=>"+children.get(j).getIndex());
						}
					}
				}
				else {
					for (int j = 0; j <= curChildrenSize; j++){
						treeList.add(moveNode(this,moveNodeIndex,i,curChildrenSize,j));
						System.out.println("T after calling moveNode(): ");
						for (int n = 0; n<this.N; n++){
							System.out.println(this.nodes[n].getWord()+this.nodes[n].getIndex());
						}
						for (int n = 0; n<this.N; n++){
							List<Node> children = this.nodes[n].children;
							int sizeOfChildren = children.size();
							if (sizeOfChildren != 0){
								for (int k=0; k<sizeOfChildren; k++)
									System.out.println(n+"=>"+children.get(k).getIndex());
							}
						}
					}
				}
			}
			//ParseTree adjustedTree = T.generateNewTree1();
			//ParseTree adjustedTree = new ParseTree();
		}
		//return adjustedTree;
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
	ParseTree moveNode (ParseTree T, int MoveNode, int targetNode, int childrenSize, int i){
		System.out.println("T At the beginning of moveNode: "+MoveNode+" "+targetNode+" "+childrenSize+" "+i);
		for (int n = 0; n<T.N; n++){
			System.out.print(T.nodes[n]);
		}
		for (int n = 0; n<T.N; n++){
			List<Node> children = T.nodes[n].children;
			int sizeOfChildren = children.size();
			if (sizeOfChildren != 0){
				for (int j=0; j<sizeOfChildren; j++)
					System.out.println(n+"=>"+children.get(j).getIndex());
			}
		}
		System.out.println("\n");
		
		ParseTree temp = T.generateNewTree1();
		System.out.println("Getting T from generateNewTree1() ");
		for (int n = 0; n<T.N; n++){
			System.out.print(T.nodes[n]);
		}
		for (int n = 0; n<T.N; n++){
			List<Node> children = T.nodes[n].children;
			int sizeOfChildren = children.size();
			if (sizeOfChildren != 0){
				for (int j=0; j<sizeOfChildren; j++)
					System.out.println(n+"=>"+children.get(j).getIndex());
			}
		}
		System.out.println("\n");
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
			System.out.println("downChild: "+downChild);
			curNode.getChildren().remove(downChild);
			curNode.getChildren().add(tempMoveNode);
			tempMoveNode.getChildren().add(downChild);
			System.out.println(tempMoveNode.getChildren());
			tempMoveNode.setParent(curNode);
			downChild.setParent(tempMoveNode);
		}
		
		System.out.println("T after node moves and befor generateNewTree()");
		for (int n = 0; n<T.N; n++){
			System.out.print(T.nodes[n]);
		}
		for (int n = 0; n<T.N; n++){
			System.out.println(T.nodes[n].parent+"=>"+T.nodes[n]);
		}
		System.out.println("\n");
		
		System.out.println("temp after node moves and befor generateNewTree()");
		for (int n = 0; n<temp.N; n++){
			System.out.print(temp.nodes[n]);
		}
		for (int n = 0; n<temp.N; n++){
			System.out.println(temp.nodes[n].parent+"=>"+T.nodes[n]);
		}
		System.out.println("\n");
		
		temp.generateNewTree();  //change temp, and return temp
		System.out.println("T at the end of moveNode() ");
		for (int n = 0; n<T.N; n++){
			System.out.print(T.nodes[n]);
		}
		for (int n = 0; n<T.N; n++){
			List<Node> children = T.nodes[n].children;
			int sizeOfChildren = children.size();
			if (sizeOfChildren != 0){
				for (int j=0; j<sizeOfChildren; j++)
					System.out.println(n+"=>"+children.get(j).getIndex());
			}
		}
		System.out.println("\n");
		return temp;
	}
}
