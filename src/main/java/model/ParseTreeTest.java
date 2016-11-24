package model;


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
	 *  return(SN:SELECT)
	 *      |
	 *  (all)titles(NN:in.title)
	 *      | `-------------\
	 *  theory(VN:in.area)  1970(VN:in.year)
	 *                       |
	 *                     before(ON:<)
	 * </pre></p>
	 * 
	 * <p>The next step is to translate this "perfect" ParseTree word-to-word to
	 * an SQL query, which is what this method is testing for.</p>
	 */
	public static void testTranslation1() {
		// (1) Let's construct the perfect ParseTree for testing.
		ParseTree tree = new ParseTree();
		tree.N = 5;
		tree.nodes = new Node[tree.N];
		Node[] nodes = tree.nodes;

		nodes[0] = new Node(0, "return", "--"); // posTag not useful
		nodes[0].info = new NodeInfo("SN", "SELECT");
		nodes[1] = new Node(1, "titles", "--");
		nodes[1].info = new NodeInfo("NN", "in.title");
		nodes[2] = new Node(2, "theory", "--");
		nodes[2].info = new NodeInfo("VN", "in.area");
		nodes[3] = new Node(3, "1970", "--");
		nodes[3].info = new NodeInfo("VN", "in.year");
		nodes[4] = new Node(4, "before", "--");
		nodes[4].info = new NodeInfo("ON", "<");
		
		nodes[0].children.add(nodes[1]);
		nodes[1].parent = nodes[0];
		nodes[1].children.add(nodes[2]);
		nodes[1].children.add(nodes[3]);
		nodes[2].parent = nodes[1];
		nodes[3].parent = nodes[1];
		nodes[3].children.add(nodes[4]);
		nodes[4].parent = nodes[3];
		
		tree.root = nodes[0];	
		
		// (2) Do the translation.
		SQLQuery query = tree.translateToSQL();
		
		// (3) Print out the query and see.
		System.out.println(query);
		
	}
	
	public static void main(String[] args) {
		testTranslation1();
	}
	
}
