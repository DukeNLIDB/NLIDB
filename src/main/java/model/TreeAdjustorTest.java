package model;

import java.util.ArrayList;
import java.util.List;

public class TreeAdjustorTest {
	public static void numberOfInvalidNodesTest(){
		//construct a tree in the paper, current test case is Figure 3 (c), output should be 0
		ParseTree T = new ParseTree();
		T.N = 9;
		T.nodes = new Node[T.N];
		Node[] nodes = T.nodes;
		
		nodes[0] = new Node(0, "ROOT", "--");
		nodes[0].info = new NodeInfo("ROOT","ROOT");
		nodes[1] = new Node(1, "return", "--");
		nodes[1].info = new NodeInfo("SN","SELECT");
		nodes[2] = new Node(2, "author", "--");
		nodes[2].info = new NodeInfo("NN", "Author");
		nodes[3] = new Node(3, "more", "--");
		nodes[3].info = new NodeInfo("ON", "Title");
		nodes[4] = new Node(4, "paper", "--");
		nodes[4].info = new NodeInfo("NN", ">");
		nodes[5] = new Node(5, "Bob", "--");
		nodes[5].info = new NodeInfo("VN", "Author");
		nodes[6] = new Node(6, "VLDB", "--");
		nodes[6].info = new NodeInfo("VN", "Journal");
		nodes[7] = new Node(7, "after", "--");
		nodes[7].info = new NodeInfo("ON", ">");
		nodes[8] = new Node(8, "2000", "--");
		nodes[8].info = new NodeInfo("VN", "Year");
		
		nodes[0].children.add(nodes[1]);
		nodes[1].parent = nodes[0];
		nodes[0].children.add(nodes[3]);
		nodes[3].parent = nodes[0];
		nodes[1].children.add(nodes[2]);
		nodes[2].parent = nodes[1];
		nodes[3].children.add(nodes[4]);
		nodes[4].parent = nodes[3];
		nodes[3].children.add(nodes[5]);
		nodes[5].parent = nodes[3];
		nodes[4].children.add(nodes[6]);
		nodes[6].parent = nodes[4];
		nodes[4].children.add(nodes[7]);
		nodes[7].parent = nodes[4];
		nodes[7].children.add(nodes[8]);
		nodes[8].parent = nodes[7];
		
		System.out.println(T.numberOfInvalidNodes(T)+"\n");
		for (int i = 1; i < T.N; i++){
			if (nodes[i].isInvalid)
				System.out.println(i);
		}
	}
	
	public static void mergeLNQNTest() {
		ParseTree T = new ParseTree();
		T.N = 9;
		T.nodes = new Node[T.N];
		Node[] nodes = T.nodes;
		
		nodes[0] = new Node(0, "ROOT", "--");
		nodes[0].info = new NodeInfo("ROOT","ROOT");
		nodes[1] = new Node(1, "return", "--");
		nodes[1].info = new NodeInfo("SN","SELECT");
		nodes[2] = new Node(2, "conference", "--");
		nodes[2].info = new NodeInfo("NN", "Author");
		nodes[3] = new Node(3, "area", "--");
		nodes[3].info = new NodeInfo("NN", "Title");
		nodes[4] = new Node(4, "each", "--");
		nodes[4].info = new NodeInfo("QN", ">");
		nodes[5] = new Node(5, "papers", "--");
		nodes[5].info = new NodeInfo("NN", "Author");
		nodes[6] = new Node(6, "citations", "--");
		nodes[6].info = new NodeInfo("NN", "Journal");
		nodes[7] = new Node(7, "most", "--");
		nodes[7].info = new NodeInfo("FN", ">");
		nodes[8] = new Node(8, "total", "--");
		nodes[8].info = new NodeInfo("FN", "Year");
		
		T.root = nodes[0];
		nodes[0].children.add(nodes[1]);
		nodes[1].parent = nodes[0];
		nodes[1].children.add(nodes[2]);
		nodes[2].parent = nodes[1];
		nodes[2].children.add(nodes[3]);
		nodes[3].parent = nodes[2];
		nodes[2].children.add(nodes[5]);
		nodes[5].parent = nodes[2];
		nodes[3].children.add(nodes[4]);
		nodes[4].parent = nodes[3];
		nodes[5].children.add(nodes[6]);
		nodes[6].parent = nodes[5];
		nodes[6].children.add(nodes[7]);
		nodes[7].parent = nodes[6];
		nodes[6].children.add(nodes[8]);
		nodes[8].parent = nodes[6];
		
		T.mergeLNQN();
		for (int i = 0; i<T.N; i++)
			System.out.println(i+": "+T.nodes[i].getWord());
		for (int i = 0; i<T.N; i++){
			List<Node> children = T.nodes[i].children;
			int sizeOfChildren = children.size();
			if (sizeOfChildren != 0){
				for (int j=0; j<sizeOfChildren; j++)
					System.out.println(i+"=>"+children.get(j).getIndex());
			}
		}
	}
	
	public static void adjustorTest(){
		ParseTree T = new ParseTree();
		T.N = 9;
		T.nodes = new Node[T.N];
		Node[] nodes = T.nodes;
		
		nodes[0] = new Node(0, "ROOT", "--");
		nodes[0].info = new NodeInfo("ROOT","ROOT");
		nodes[1] = new Node(1, "return", "--");
		nodes[1].info = new NodeInfo("SN","SELECT");
		nodes[2] = new Node(2, "conference", "--");
		nodes[2].info = new NodeInfo("NN", "Author");
		nodes[3] = new Node(3, "area", "--");
		nodes[3].info = new NodeInfo("NN", "Title");
		nodes[4] = new Node(4, "each", "--");
		nodes[4].info = new NodeInfo("QN", ">");
		nodes[5] = new Node(5, "papers", "--");
		nodes[5].info = new NodeInfo("NN", "Author");
		nodes[6] = new Node(6, "citations", "--");
		nodes[6].info = new NodeInfo("NN", "Journal");
		nodes[7] = new Node(7, "most", "--");
		nodes[7].info = new NodeInfo("FN", ">");
		nodes[8] = new Node(8, "total", "--");
		nodes[8].info = new NodeInfo("FN", "Year");
		
		T.root = nodes[0];
		nodes[0].children.add(nodes[1]);
		nodes[1].parent = nodes[0];
		nodes[1].children.add(nodes[2]);
		nodes[2].parent = nodes[1];
		nodes[2].children.add(nodes[3]);
		nodes[3].parent = nodes[2];
		nodes[2].children.add(nodes[5]);
		nodes[5].parent = nodes[2];
		nodes[3].children.add(nodes[4]);
		nodes[4].parent = nodes[3];
		nodes[5].children.add(nodes[6]);
		nodes[6].parent = nodes[5];
		nodes[6].children.add(nodes[7]);
		nodes[7].parent = nodes[6];
		nodes[6].children.add(nodes[8]);
		nodes[8].parent = nodes[6];
		
		//ParseTree tree = T.generateNewTree1();
		List<ParseTree> tree = T.adjustor();
		System.out.println(tree.size());
		//for (int i = 0; i<tree.N; i++)
		//	System.out.println(i+": "+tree.nodes[i].getWord());
		//int number = adjustedTrees.size();
		//System.out.println(number);
	}

	public static void main(String[] args) {
		//test mergeLNQN and numberOfInvalidNodes methods
		//numberOfInvalidNodesTest();
		//mergeLNQNTest();
		adjustorTest();
	}

}
