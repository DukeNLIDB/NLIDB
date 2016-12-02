package model;

import java.util.Collections;
import java.util.List;

public class TreeAdjustorTest {
	public static void numberOfInvalidNodesTest(){
		//construct a tree in the paper, 
		//current test case is Figure 3 (a), output should be 3 (node 6 should not be invalid)
		ParseTree T = new ParseTree();
		Node[] nodes = new Node[9];
		
		nodes[0] = new Node(0, "ROOT", "--");
		nodes[0].info = new NodeInfo("ROOT","ROOT");
		nodes[1] = new Node(1, "return", "--");
		nodes[1].info = new NodeInfo("SN","SELECT");
		nodes[2] = new Node(2, "author", "--");
		nodes[2].info = new NodeInfo("NN", "Author");
		nodes[3] = new Node(3, "paper", "--");
		nodes[3].info = new NodeInfo("NN", ">");
		nodes[4] = new Node(4, "more", "--");
		nodes[4].info = new NodeInfo("ON", "Title");
		nodes[5] = new Node(5, "Bob", "--");
		nodes[5].info = new NodeInfo("VN", "Author");
		nodes[6] = new Node(6, "VLDB", "--");
		nodes[6].info = new NodeInfo("VN", "Journal");
		nodes[7] = new Node(7, "after", "--");
		nodes[7].info = new NodeInfo("ON", ">");
		nodes[8] = new Node(8, "2000", "--");
		nodes[8].info = new NodeInfo("VN", "Year");
		
		T.root = nodes[0];
		nodes[0].children.add(nodes[1]);
		nodes[1].parent = nodes[0];
		nodes[1].children.add(nodes[2]);
		nodes[2].parent = nodes[1];
		nodes[2].children.add(nodes[3]);
		nodes[3].parent = nodes[2];
		nodes[2].children.add(nodes[5]);
		nodes[5].parent = nodes[2];
		nodes[2].children.add(nodes[7]);
		nodes[7].parent = nodes[2];
		nodes[3].children.add(nodes[4]);
		nodes[4].parent = nodes[3];
		nodes[5].children.add(nodes[6]);
		nodes[6].parent = nodes[5];
		nodes[7].children.add(nodes[8]);
		nodes[8].parent = nodes[7];
		
		System.out.println("===========test for Running SyntacticEvaluator.numberOfInvalidNodes===========");
		System.out.println("Input tree: "+T.toString());
		System.out.println("Number of Invalid nodes: "+SyntacticEvaluator.numberOfInvalidNodes(T)+"\n");
		System.out.println("Invalid nodes: ");
		for (int i = 1; i < nodes.length; i++){
			if (nodes[i].isInvalid)
				System.out.println(nodes[i]);
		}
	}
	
	public static void mergeLNQNTest() {
		ParseTree T = new ParseTree();
		Node[] nodes = new Node[9];
		
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
		
		System.out.println("===========test for Running mergeLNQN===========");
		System.out.println("Input tree: "+T.toString());
		ParseTree tree = T.mergeLNQN();
		System.out.println("Output tree: "+tree.toString());
	}
	
	public static void adjustTest(){
		ParseTree T = new ParseTree();
		Node[] nodes = new Node[9];
		
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
		
		System.out.println("===========test for Running adjust() in TreeAdjustor===========");
		System.out.println("Input tree: "+T.toString());
		List<ParseTree> treeList = TreeAdjustor.adjust(T);
		System.out.println("Output size: "+treeList.size());
		System.out.println("Output trees:");
		for (int j = 0; j < treeList.size(); j++){
		System.out.println("Tree "+j+" :");
		System.out.println(treeList.get(j));
		}
	}
	
	public static void getAdjustedTreesTest(){
		ParseTree T = new ParseTree();
		Node[] nodes = new Node[8];
		
		nodes[0] = new Node(0, "ROOT", "--");
		nodes[0].info = new NodeInfo("ROOT","ROOT");
		nodes[1] = new Node(1, "return", "--");
		nodes[1].info = new NodeInfo("SN","SELECT");
		nodes[2] = new Node(2, "conference", "--");
		nodes[2].info = new NodeInfo("NN", "Author");
		nodes[3] = new Node(3, "area", "--");
		nodes[3].info = new NodeInfo("NN", "Title");
		nodes[4] = new Node(4, "papers", "--");
		nodes[4].info = new NodeInfo("NN", "Author");
		nodes[5] = new Node(5, "citations", "--");
		nodes[5].info = new NodeInfo("NN", "Journal");
		nodes[6] = new Node(6, "most", "--");
		nodes[6].info = new NodeInfo("FN", ">");
		nodes[7] = new Node(7, "total", "--");
		nodes[7].info = new NodeInfo("FN", "Year");
		
		T.root = nodes[0];
		nodes[0].children.add(nodes[1]);
		nodes[1].parent = nodes[0];
		nodes[1].children.add(nodes[2]);
		nodes[2].parent = nodes[1];
		nodes[2].children.add(nodes[3]);
		nodes[3].parent = nodes[2];
		nodes[2].children.add(nodes[4]);
		nodes[4].parent = nodes[2];
		nodes[4].children.add(nodes[5]);
		nodes[5].parent = nodes[4];
		nodes[5].children.add(nodes[6]);
		nodes[6].parent = nodes[5];
		nodes[5].children.add(nodes[7]);
		nodes[7].parent = nodes[5];
		
		System.out.println("===========test for Running getAdjustedTrees() in TreeAdjustor===========");
		System.out.println("The original tree:");
		System.out.println(T);
		System.out.println("Number of possible trees for choice:");
		List<ParseTree> result = TreeAdjustor.getAdjustedTrees(T);
		System.out.println(result.size());
		Collections.sort(result, (t1, t2) -> (- t1.getScore() + t2.getScore()));
		System.out.println("The three trees with highest scores look like:");
		for (int i = 0; i < 5; i++) {
			System.out.println(result.get(i));
		}
	}
	
	public static void testAddON (){
		ParseTree T = new ParseTree();
		Node[] nodes = new Node[8];
		
		nodes[0] = new Node(0, "ROOT", "--");
		nodes[0].info = new NodeInfo("ROOT","ROOT");
		nodes[1] = new Node(1, "return", "--");
		nodes[1].info = new NodeInfo("SN","SELECT");
		nodes[2] = new Node(2, "conference", "--");
		nodes[2].info = new NodeInfo("NN", "Author");
		nodes[3] = new Node(3, "area", "--");
		nodes[3].info = new NodeInfo("NN", "Title");
		nodes[4] = new Node(4, "papers", "--");
		nodes[4].info = new NodeInfo("NN", "Author");
		nodes[5] = new Node(5, "citations", "--");
		nodes[5].info = new NodeInfo("NN", "Journal");
		nodes[6] = new Node(6, "most", "--");
		nodes[6].info = new NodeInfo("FN", ">");
		nodes[7] = new Node(7, "total", "--");
		nodes[7].info = new NodeInfo("FN", "Year");
		
		T.root = nodes[0];
		nodes[0].children.add(nodes[1]);
		nodes[1].parent = nodes[0];
		nodes[1].children.add(nodes[2]);
		nodes[2].parent = nodes[1];
		nodes[2].children.add(nodes[3]);
		nodes[3].parent = nodes[2];
		nodes[2].children.add(nodes[4]);
		nodes[4].parent = nodes[2];
		nodes[4].children.add(nodes[5]);
		nodes[5].parent = nodes[4];
		nodes[5].children.add(nodes[6]);
		nodes[6].parent = nodes[5];
		nodes[5].children.add(nodes[7]);
		nodes[7].parent = nodes[5];
		
		System.out.println("===========test for Running addON() in ParseTree===========");
		System.out.println("The original tree:");
		System.out.println(T);
		ParseTree tree = T.addON();
		System.out.println("After adding ON:");
		System.out.println(tree);
		System.out.println("The original tree:");
		System.out.println(T);
	}

	public static void main(String[] args) {
//		numberOfInvalidNodesTest();
//		mergeLNQNTest();
//		adjustTest();
		getAdjustedTreesTest();
//		testAddON();
	}

}
