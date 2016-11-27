package model;

public class TreeAdjustorTest {
	public static void numberOfInvalidNodesTest(){
		//construct a tree in the paper
		ParseTree T = new ParseTree();
		T.N = 8;
		T.nodes = new Node[T.N];
		Node[] nodes = T.nodes;
		
		nodes[0] = new Node(0, "ROOT", "--");
		nodes[0].info = new NodeInfo("ROOT","ROOT");
		//nodes[0].setInfo(new NodeInfo("", ""));
		nodes[1] = new Node(1, "return", "--");
		nodes[1].info = new NodeInfo("SN","SELECT");
		//nodes[1].setInfo(new NodeInfo("SN","SELECT"));
		nodes[2] = new Node(2, "conference", "--");
		nodes[2].info = new NodeInfo("NN", "Pubkey");
		//nodes[2].setInfo(new NodeInfo("NN", "Pubkey"));
		nodes[3] = new Node(3, "area(each)", "--");
		nodes[3].info = new NodeInfo("NN", "Area");
		//nodes[3].setInfo(new NodeInfo("NN", "Area"));
		nodes[4] = new Node(4, "papers", "--");
		nodes[4].info = new NodeInfo("NN", "Title");
		//nodes[4].setInfo(new NodeInfo("NN", "Title"));
		nodes[5] = new Node(5, "citations", "--");
		nodes[5].info = new NodeInfo("NN", "I dont know!");
		//nodes[5].setInfo(new NodeInfo("NN", "I dont know!"));
		nodes[6] = new Node(6, "most", "--");
		nodes[6].info = new NodeInfo("FN", "MAX");
		//nodes[6].setInfo(new NodeInfo("FN", "MAX"));
		nodes[7] = new Node(7, "total", "--");
		nodes[7].info = new NodeInfo("FN", "SUM");
		//nodes[7].setInfo(new NodeInfo("FN", "SUM"));
		
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
		
		System.out.println(T.numberOfInvalidNodes(T));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		numberOfInvalidNodesTest();
	}

}
