package model;

import java.util.List;

public class SyntacticEvaluator {

	int numOfInvalid;
	
	public SyntacticEvaluator() {
		numOfInvalid = 0;
	}
	
	/**
	 * a root is invalid if: 
	 * it has no child; 
	 * it has only one child and this child is not SN; 
	 * it has more than one child and other than the first child is not ON.
	 * @param node
	 * @return
	 */
	private static int checkROOT(Node node){
		int numOfInvalid = 0;
		List<Node> children = node.getChildren();
		int sizeOfChildren = children.size();
		
		if (sizeOfChildren == 0){
			numOfInvalid++;
			node.isInvalid = true;
		}
		else if (sizeOfChildren == 1 && !children.get(0).getInfo().getType().equals("SN")){
			numOfInvalid++;
			node.isInvalid = true;
		}
		else if (sizeOfChildren > 1){
			if (!children.get(0).getInfo().getType().equals("SN")){
				numOfInvalid++;
				node.isInvalid = true;
			}
			else {
				for (int j = 1; j < sizeOfChildren; j++){
					if (!children.get(j).getInfo().getType().equals("ON")){
						numOfInvalid++;
						node.isInvalid = true;
					}
				}
			}
		}
		return numOfInvalid;
	}
	
	/**
	 * a SN is not valid if: 
	 * it has more than 1 child; 
	 * it has 1 child but this child is not GNP (FN or NN).
	 * @param node
	 * @return
	 */
	private static int checkSN(Node node){
		int numOfInvalid = 0;
		List<Node> children = node.getChildren();
		int sizeOfChildren = children.size();
		
		//SN can only have one child from FN or NN
		if (sizeOfChildren != 1){
			numOfInvalid++;
			node.isInvalid = true;
		}
		else{
			String childType = children.get(0).getInfo().getType();
			if (!(childType.equals("NN") || childType.equals("FN"))){
				numOfInvalid++;
				node.isInvalid = true;
			}
		}
		
		return numOfInvalid;
	}
	
	/**
	 * a ON is invalid if: 
	 * (1) in ComplexCondition (its parent is ROOT):
	 * 		its number of children is not 2 (left & right subtrees);
	 * 		it has 2 children, but first one is not GNP, or second one is not GNP/VN/FN.
	 * (2) in Condition (its parent is NN):
	 * 		its number of children is not 1;
	 * 		it has 1 child, but the child is not VN.
	 * @param node
	 * @return
	 */
	private static int checkON(Node node){
		int numOfInvalid = 0;
		String parentType = node.getParent().getInfo().getType();
		List<Node> children = node.getChildren();
		int sizeOfChildren = children.size();
		
		if (parentType.equals("ROOT")){
			if (sizeOfChildren != 2){
				numOfInvalid++;
				node.isInvalid = true;
			}
			else{
				for (int j = 0; j<sizeOfChildren; j++){
					String childType = children.get(j).getInfo().getType();
					if (j==0){
						if (!(childType.equals("NN") || childType.equals("FN"))){
							numOfInvalid++;
							node.isInvalid = true;
							break;
						}
					}
					else if (j==1){
						if (childType.equals("ON")){
							numOfInvalid++;
							node.isInvalid = true;
							break;
						}
					}
				}
			}
		}
		else if (parentType.equals("NN")){
			if (sizeOfChildren != 1){
				numOfInvalid++;
				node.isInvalid = true;
			}
			else if (!children.get(0).getInfo().getType().equals("VN")){
				numOfInvalid++;
				node.isInvalid = true;
			}
		}
		
		return numOfInvalid;
	}
	
	/**
	 * a NN is invalid if: 
	 * it is the second NN in "NP=NN+NN*Condition", and it has children.
	 * it is the first NN in "GNP=NP=NN+NN*Condition", and its child is not NN, VN, ON.
	 * @param node
	 * @return
	 */
	private static int checkNN(Node node){
		int numOfInvalid = 0;
		String parentType = node.getParent().getInfo().getType();
		List<Node> children = node.getChildren();
		int sizeOfChildren = children.size();
		
		//NP=NN+NN*Condition. Second NN has no child.
		if (parentType.equals("NN")){
			if (sizeOfChildren != 0){   //this rule is different from figure 7 (a), but I think this makes sense
				numOfInvalid++;
				node.isInvalid = true;
			}
		}
		//SN+GNP, or ON+GNP, or FN+GNP. and GNP=NP=NN+NN*Condition. First NN can have any number of children from NN,ON,VN.
		else if (parentType.equals("SN") || parentType.equals("FN") || parentType.equals("ON")){
			if (sizeOfChildren != 0){
				for (int j = 0; j < sizeOfChildren; j++){
					String childType = children.get(j).getInfo().getType();
					if (!(childType.equals("NN") || childType.equals("VN") || childType.equals("ON"))){
						numOfInvalid++;
						node.isInvalid = true;
						break;
					}
				}
			}
		}
		
		return numOfInvalid;
	}
	
	/**
	 * a VN is invalid if:
	 * it has children.
	 * @param node
	 * @return
	 */
	private static int checkVN(Node node){
		int numOfInvalid = 0;
		//String parentType = node.getParent().getInfo().getType();
		List<Node> children = node.getChildren();
		int sizeOfChildren = children.size();
		
		if (sizeOfChildren != 0){  //VN cannot have children
			numOfInvalid++;
			node.isInvalid = true;
		}
		/*
		else if (!(parentType.equals("ON") || parentType.equals("NN"))){  //VN can only be child of ON and NN
			numOfInvalid++;
			node.isInvalid = true;
		}
		*/
		return numOfInvalid;
	}
	
	/**
	 * a FN is valid if:
	 * ON+FN, or ON+GNP, or SN+GNP, or FN+GNP. and GNP=FN+GNP,
	 * FN can be child of ON, without children or only 1 child of NN or FN, 
	 * FN can be child of SN, with only 1 child of NN or FN, 
	 * FN can be child of FN, with only 1 child of NN or FN. 
	 * @param node
	 * @return
	 */
	private static int checkFN(Node node){
		int numOfInvalid = 0;
		String parentType = node.getParent().getInfo().getType();
		List<Node> children = node.getChildren();
		int sizeOfChildren = children.size();
		
		if (sizeOfChildren == 0){
			if (!parentType.equals("ON")){
				numOfInvalid++;
				node.isInvalid = true;
			}
		}
		else if (sizeOfChildren == 1){
			String childType = children.get(0).getInfo().getType();
			if (!(parentType.equals("ON") || parentType.equals("SN") /*|| parentType.equals("FN")*/)){
				numOfInvalid++;
				node.isInvalid = true;
			}
			else if (!(childType.equals("NN") /*|| childType.equals("FN")*/)){
				numOfInvalid++;
				node.isInvalid = true;
			}
		}
		else{
			numOfInvalid++;
			node.isInvalid = true;
		}
		
		return numOfInvalid;
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
	 * 
	 * Basic rule: Check invalidity only considering its children
	 * @param T
	 * @return
	 */
	public static int numberOfInvalidNodes (ParseTree T){	
		int numOfInvalid = 0;   //number of invalid tree nodes
		for (Node curNode : T) {
			String curType = curNode.getInfo().getType();
			if (curType.equals("ROOT")){ //ROOT
				numOfInvalid = numOfInvalid + checkROOT(curNode);
			}
			if (curType.equals("SN")){ // select node
				numOfInvalid = numOfInvalid + checkSN(curNode);
			}
			else if (curType.equals("ON")){  //operator node
				numOfInvalid = numOfInvalid + checkON(curNode);
			}
			else if (curType.equals("NN")){  //name node
				numOfInvalid = numOfInvalid + checkNN(curNode);
			}
			else if (curType.equals("VN")){  //value node
				numOfInvalid = numOfInvalid + checkVN(curNode);
			}
			else if (curType.equals("FN")){  //function nodes
				numOfInvalid = numOfInvalid + checkFN(curNode);
			}
		}
		return numOfInvalid;
	}
	
}
