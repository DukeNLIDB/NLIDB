package model;

import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class QueryTree {
	List<ParseTree> results;
	
	public QueryTree() {}
	
	public QueryTree (ParseTree T){
		results = new ArrayList<ParseTree>();
		PriorityQueue<ParseTree> Q = new PriorityQueue<ParseTree>();
		Q.add(T);
		HashMap<Integer, ParseTree> H = new HashMap<Integer, ParseTree>();
		H.put(hashing(T), T);
		T.setEdit(0);
		
		while (Q.size() > 0){
			ParseTree oriTree = Q.poll();
			List<ParseTree> treeList = adjuster(oriTree);
			double treeScore = evaluate(oriTree);
			
			for (int i = 0; i < treeList.size(); i++){
				ParseTree currentTree = treeList.get(i);
				int hashValue = hashing(currentTree);
				if (oriTree.getEdit()<10 && !H.containsKey(hashValue)){
					H.put(hashValue, currentTree);
					currentTree.setEdit(oriTree.getEdit()+1);
					if (evaluate(currentTree) >= treeScore){
						Q.add(currentTree);
						results.add(currentTree);
					}
				}
			}
		}
	}
	
	public List<ParseTree> adjuster (ParseTree T){
		List<ParseTree> treeList = new ArrayList<ParseTree>();
		
		//TODO: generate all possible parse trees in one subtree move operation
		
		return treeList;
	}
	
	public double evaluate (ParseTree T){
		double score = 0;
		
		//TODO: generate the evaluation criteria
		return score;
	}
	
	public int hashing (ParseTree T){
		int hashValue = 0;
		
		//TODO: how to get a reasonable hash value for each parse tree (with different node orders)
		return hashValue;
	}
}
