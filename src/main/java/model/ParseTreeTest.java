package model;

import java.util.List;

public class ParseTreeTest {

	/**
	 * <p>We want to translate: <br>"Return all titles of theory papers before 1970."
	 * <br> into (in for inproceedings): 
	 * <br>SELECT in.title <br>FROM in 
	 * <br>WHERE in.area = 'Theory' AND in.year < 1970;</p>
	 * 
	 * <p>The direct parsing result of this natural language input is: </p>
	 * 
	 * <p><pre>
	 *           root
	 *            |
	 *          return
	 *            |  `---\---------\
	 *          titles   1970       .
	 *          /   \      \
	 *       all   papers  before
	 *              /  \ 
	 *            of   theory
	 * </pre></p>
	 * 
	 * <p>Suppose we have already successfully gone through the process of 
	 * nodes mapping and structural adjustment. Then we should arrive at a ParseTree
	 * like this: (in for inproceedings)</p>
	 * 
	 * <p><pre>
	 *    root
	 *      |
	 *  return(SN:SELECT)
	 *      |
	 *  titles(NN:in.title)
	 *      | `-------------\
	 *  theory(VN:in.area)  before(ON:<)
	 *                       |
	 *                     1970(VN:in.year)
	 * </pre></p>
	 * 
	 * <p>The next step is to translate this "perfect" ParseTree word-to-word to
	 * an SQL query, which is what this method is testing for.</p>
	 */
	public static void testTranslation1() {
		// (1) Let's construct the perfect ParseTree for testing.
		ParseTree tree = new ParseTree();
		Node[] nodes = new Node[6];

		nodes[0] = new Node(0, "ROOT", "ROOT");
		nodes[0].info = new NodeInfo("ROOT","ROOT");
		nodes[1] = new Node(1, "return", "--"); // posTag not useful
		nodes[1].info = new NodeInfo("SN", "SELECT");
		nodes[2] = new Node(2, "titles", "--");
		nodes[2].info = new NodeInfo("NN", "in.title");
		nodes[3] = new Node(3, "theory", "--");
		nodes[3].info = new NodeInfo("VN", "in.area");
		nodes[4] = new Node(4, "before", "--");
		nodes[4].info = new NodeInfo("ON", "<");
		nodes[5] = new Node(5, "1970", "--");
		nodes[5].info = new NodeInfo("VN", "in.year");
		
		tree.root = nodes[0];
		tree.root.getChildren().add(nodes[1]);
		nodes[1].children.add(nodes[2]);
		nodes[2].parent = nodes[1];
		nodes[2].children.add(nodes[3]);
		nodes[2].children.add(nodes[4]);
		nodes[3].parent = nodes[2];
		nodes[4].parent = nodes[2];
		nodes[4].children.add(nodes[5]);
		nodes[5].parent = nodes[4];
/*		
		System.out.println(tree);
		
		// (2) Do the translation.
		SQLQuery query = tree.translateToSQL();
		
		// (3) Print out the query and see.
		System.out.println(query);
*/

		System.out.println("===========test for Running SyntacticEvaluator.numberOfInvalidNodes===========");
		System.out.println("Input tree: "+tree.toString());
		System.out.println("Number of Invalid nodes: "+SyntacticEvaluator.numberOfInvalidNodes(tree)+"\n");
		System.out.println("Invalid nodes: ");
		for (int i = 1; i < tree.size(); i++){
			if (nodes[i].isInvalid)
				System.out.println(nodes[i]);
		}

		System.out.println("===========test for Running mergeLNQN===========");
		System.out.println("Input tree: "+tree.toString());
		ParseTree newTree = tree.mergeLNQN();
		System.out.println("Output tree: "+newTree.toString());
		System.out.println("===========test for Running adjust() in TreeAdjustor===========");
		System.out.println("Input tree: "+tree.toString());
		List<ParseTree> treeList = TreeAdjustor.adjust(tree);
		System.out.println("Output size: "+treeList.size());
		System.out.println("Output trees:");
		for (int j = 0; j < treeList.size(); j++){
			System.out.println("Tree "+j+" :");
			System.out.println(treeList.get(j).toString());
		}
		
		System.out.println("===========test for Running getAdjustedTrees() in TreeAdjustor===========");
		System.out.println("Number of possible trees for choice:");
		List<ParseTree> result = TreeAdjustor.getAdjustedTrees(tree);
		System.out.println(result.size());
		for (ParseTree t:result)
			System.out.println(t);
	}
	
	/**
	 * Using natural language input "Return all titles of theory papers before 1970."
	 * <p>The original tree:</p>
	 * <p><pre>
	 *           root
	 *            |
	 *          return
	 *            |  `---\---------\
	 *          titles   1970       .
	 *          /   \      \
	 *       all   papers  before
	 *              /  \ 
	 *            of   theory
	 * </pre></p>
	 * 
	 * <p>The tree after removing meaningless nodes:</p>
	 * <p><pre>
	 *    root
	 *      |
	 *  return(SN:SELECT)
	 *      |     `----------\
	 *  titles(NN:in.title) 1970(VN:in.year)
	 *      |                | 
	 *  theory(VN:in.area)  before(ON:<)
	 * </pre></p>
	 * 
	 * <p>Still need the adjustor to swap the position of "1970" and "before".</p>
	 */
	public static void removeMeaninglessNodesTest() {
		String input = "Return all titles of theory papers before 1970.";
		NLParser parser = new NLParser();
		ParseTree tree = new ParseTree(input, parser);
		System.out.println("ParseTree: ");
		System.out.println(tree);
		
		// Set NodeInfo
		Node[] nodes = tree.genNodesArray();
		nodes[1].info = new NodeInfo("SN", "SELECT");
		nodes[2].info = new NodeInfo("UNKNOWN", "meaningless");
		nodes[3].info = new NodeInfo("NN", "in.title");
		nodes[4].info = new NodeInfo("UNKNOWN", "meaningless");
		nodes[5].info = new NodeInfo("VN", "in.area");
		nodes[6].info = new NodeInfo("UNKNOWN", "meaningless");
		nodes[7].info = new NodeInfo("ON", "<");
		nodes[8].info = new NodeInfo("VN", "in.year");
		nodes[9].info = new NodeInfo("UNKNOWN", "meaningless");
		
		System.out.println("After setting nodeinfo:");
		System.out.println(tree);
		
		tree.removeMeaninglessNodes();
		
		System.out.println("After removing meaningless nodes");
		System.out.println(tree);
		
		SQLQuery query = tree.translateToSQL();
		
		System.out.println(query);
		
	}
	
	public static void main(String[] args) {
		testTranslation1();
		//removeMeaninglessNodesTest();
	}
	
}
