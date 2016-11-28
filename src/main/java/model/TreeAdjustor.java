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
		for (int i=1; i<T.size(); i++){  //starting from SN (leave out ROOT)
			Node curNode = T.nodes[i];
			String curType = curNode.getInfo().getType();
			String parentType = curNode.getParent().getInfo().getType();
			List<Node> children = curNode.getChildren();
			int sizeOfChildren = children.size();
			if (curType.equals("SN")){ // select node
				//SN can only be child of root
				if (!parentType.equals("ROOT")){   
					numOfInv++;
					curNode.isInvalid = true;
				}
				//SN can only have one child from FN or NN
				else if (sizeOfChildren != 1){
					numOfInv++;
					curNode.isInvalid = true;
				}
				else{
					String childType = children.get(0).getInfo().getType();
					if (!(childType.equals("NN") || childType.equals("FN"))){
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
			}
			else if (curType.equals("ON")){  //operator node
				if (parentType.equals("ROOT")){
					if (sizeOfChildren == 0){
						numOfInv++;
						curNode.isInvalid = true;
					}
					else{
						for (int j = 0; j<sizeOfChildren; j++){
							String childType = children.get(j).getInfo().getType();
							if (childType.equals("ON")){
								numOfInv++;
								curNode.isInvalid = true;
								break;
							}
						}
					}
				}
				else if (parentType.equals("NN")){
					if (sizeOfChildren != 1){
						numOfInv++;
						curNode.isInvalid = true;
					}
					else if (!children.get(0).getInfo().getType().equals("VN")){
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
			}
			else if (curType.equals("NN")){  //name node
				//NP=NN+NN*Condition. Second NN has no child.
				if (parentType.equals("NN")){
					if (sizeOfChildren != 0){   //this rule is different from figure 7 (a), but I think this makes sense
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
				//SN+GNP, or ON+GNP, or FN+GNP. and GNP=NP=NN+NN*Condition. First NN can have any number of children from NN,ON,VN.
				else if (parentType.equals("SN") || parentType.equals("FN") || parentType.equals("ON")){
					if (sizeOfChildren != 0){
						for (int j = 0; j < sizeOfChildren; j++){
							String childType = children.get(j).getInfo().getType();
							if (!(childType.equals("NN") || childType.equals("VN") || childType.equals("ON"))){
								numOfInv++;
								curNode.isInvalid = true;
								break;
							}
						}
					}
				}
				//NN cannot be a child of VN
				else if (parentType.equals("VN")){
					numOfInv++;
					curNode.isInvalid = true;
				}
			}
			else if (curType.equals("VN")){  //value node
				if (sizeOfChildren != 0){  //VN cannot have children
					numOfInv++;
					curNode.isInvalid = true;
				}
				else if (!(parentType.equals("ON") || parentType.equals("NN"))){  //VN can only be child of ON and NN
					numOfInv++;
					curNode.isInvalid = true;
				}
			}
			else if (curType.equals("FN")){  //function nodes
				//ON+FN, or ON+GNP, or SN+GNP, or FN+GNP. and GNP=FN+GNP
				//FN can be child of ON, without children or only 1 child of NN or FN
				//FN can be child of SN, wih only 1 child of NN or FN
				//FN can be child of FN, wih only 1 child of NN or FN
				if (sizeOfChildren == 0){
					if (!parentType.equals("ON")){
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
				else if (sizeOfChildren == 1){
					String childType = children.get(0).getInfo().getType();
					if (!(parentType.equals("ON") || parentType.equals("SN") || parentType.equals("FN"))){
						numOfInv++;
						curNode.isInvalid = true;
					}
					else if (!(childType.equals("NN") || childType.equals("FN"))){
						numOfInv++;
						curNode.isInvalid = true;
					}
				}
				else{
					numOfInv++;
					curNode.isInvalid = true;
				}
			}
		}
		
		return numOfInv;
	}
	
	public static List<IParseTree> getAdjustedTrees(ParseTree tree) {
		List<IParseTree> results = new ArrayList<IParseTree>();
		PriorityQueue<ParseTree> Q = new PriorityQueue<ParseTree>();
		Q.add(this);
		HashMap<Integer, ParseTree> H = new HashMap<Integer, ParseTree>();
		H.put(hashing(this), this);
		this.setEdit(0);
		
		while (Q.size() > 0){
			ParseTree oriTree = Q.poll();
			List<ParseTree> treeList = TreeAdjustor.adjust(oriTree);
			double treeScore = numberOfInvalidNodes(oriTree);
			
			for (int i = 0; i < treeList.size(); i++){
				ParseTree currentTree = treeList.get(i);
				int hashValue = hashing(currentTree);
				if (oriTree.getEdit()<MAX_EDIT && !H.containsKey(hashValue)){
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
}
