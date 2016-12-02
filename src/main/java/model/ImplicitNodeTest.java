package model;

import java.util.List;

public class ImplicitNodeTest {

	public static void main(String[] args) {
		
		ParseTree tree = new ParseTree();
		Node[] nodes = new Node[20];

		nodes[0] = new Node(0, "ROOT", "ROOT");
		nodes[0].info = new NodeInfo("ROOT","ROOT");

		nodes[1] = new Node(1, "return", "--"); // posTag not useful
		nodes[1].info = new NodeInfo("SN", "SELECT");

		nodes[2] = new Node(2, "author", "--");
		nodes[2].info = new NodeInfo("NN", "authorship.author");

		nodes[3] = new Node(3, "more", "--");
		nodes[3].info = new NodeInfo("ON", ">");

		nodes[4] = new Node(4, "paper", "--");
		nodes[4].info = new NodeInfo("NN", "in.pubkey");

		nodes[5] = new Node(5, "VLDB", "--");
		nodes[5].info = new NodeInfo("VN", "in.area");

		nodes[6] = new Node(6, "after", "--");
		nodes[6].info = new NodeInfo("ON", ">");

		nodes[7] = new Node(7, "2000", "--");
		nodes[7].info = new NodeInfo("VN", "in.year");

		nodes[8] = new Node(8, "Bob", "--");
		nodes[8].info = new NodeInfo("VN", "authorship.author");
		
		tree.root = nodes[0];
		tree.root.setChild(nodes[1]);
		tree.root.setChild(nodes[3]);
		nodes[1].setParent(nodes[0]);
		nodes[3].setParent(nodes[0]);

		nodes[1].setChild(nodes[2]);
		nodes[2].setParent(nodes[1]);

		nodes[3].setChild(nodes[4]);
		nodes[3].setChild(nodes[8]);
		nodes[4].setParent(nodes[3]);
		nodes[8].setParent(nodes[3]);

		nodes[4].setChild(nodes[5]);
		nodes[4].setChild(nodes[6]);
		nodes[5].setParent(nodes[4]);
		nodes[6].setParent(nodes[4]);

		nodes[6].setChild(nodes[7]);
		nodes[7].setParent(nodes[6]);
		
		System.out.println("Before");
		System.out.println(tree.toString());
		
		tree.insertImplicitNodes();
		
		System.out.println("After");
		System.out.println(tree.toString());
		
	}
	
}
