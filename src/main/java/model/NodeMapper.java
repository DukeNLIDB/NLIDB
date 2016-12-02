package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class to help map word {@link Node} in {@link ParseTree}
 * to SQL components (represented by class {@link NodeInfo}).
 * @author keping
 *
 */
public class NodeMapper {
	private WordNet wordNet;
	/**
	 * Key is the word. Value is the corresponding SQL component.
	 * For example: ("return", ("SN", "SELECT"))
	 */
	private Map<String, NodeInfo> map;
	
	
	/**
	 * Initialize the NodeMapper. (The mapper could be made configurable. It can also initialize
	 * by reading mappings from a file)
	 * @throws Exception 
	 */
	public NodeMapper() throws Exception {
		wordNet = new WordNet();
		map = new HashMap<String, NodeInfo>();
		map.put("return", new NodeInfo("SN", "SELECT")); // Select Node
		
		map.put("equals", new NodeInfo("ON", "="));		 // Operator Node
		map.put("less",    new NodeInfo("ON", "<"));
		map.put("greater",    new NodeInfo("ON", ">"));
		map.put("not",    new NodeInfo("ON", "!="));    //TODO: not is a operator node or logic node?
		map.put("before", new NodeInfo("ON", "<"));
		map.put("after", new NodeInfo("ON", ">"));
		map.put("more",    new NodeInfo("ON", ">"));
		map.put("older",    new NodeInfo("ON", ">"));
		map.put("newer", new NodeInfo("ON", "<"));
		
		map.put("fn",     new NodeInfo("FN", "AVG"));	 // Function Node
		map.put("average",     new NodeInfo("FN", "AVG"));
		map.put("most",     new NodeInfo("FN", "MAX"));
		map.put("total",     new NodeInfo("FN", "SUM"));
		map.put("number", new NodeInfo("FN","COUNT"));

		map.put("all",    new NodeInfo("QN", "ALL"));	 // Quantifier Node
		map.put("any",    new NodeInfo("QN", "ANY"));
		map.put("each",    new NodeInfo("QN", "EACH"));
		
		map.put("and",    new NodeInfo("LN", "AND"));	 // Logic Node
		map.put("or",    new NodeInfo("LN", "OR"));
		

	}
	
	/**
	 * <p>Return the a ranked list of candidate NodeInfos for this Node. This method
	 * will be called by the controller, and then the candidates will be passed on
	 * to the view for user to choose. If there is only one candidate in the list, 
	 * the choice is automatically made.</p>
	 * <p>The length of the list of NodeInfos is at least 1. We will have special type
	 * in NodeInfo if the Node doesn't correspond to any SQL component (the Node is
	 * meaningless).</p>
	 * <p>The returned list contains at most 6 elements.</p>
	 * <p>Treat all input as lower case.</p>
	 * @param node
	 * @param schema
	 * @return a ranked of NodeInfo
	 */
	public List<NodeInfo> getNodeInfoChoices(Node node, SchemaGraph schema) {
		List<NodeInfo> result = new ArrayList<NodeInfo>();   //final output
		if (node.getWord().equals("ROOT")) {
			result.add(new NodeInfo("ROOT", "ROOT"));
			return result;
		}
		Set<NodeInfo> valueNodes = new HashSet<NodeInfo>();  //used to store (type, value, score) of 100 sample values for every column in every table
		String word = node.getWord().toLowerCase(); // all words as lower case
		
		if (map.containsKey(word)) {
			result.add(map.get(word));
			return result;
		}
				
		for (String tableName : schema.getTableNames()) {
			result.add(new NodeInfo("NN", tableName,
					WordSimilarity.getSimilarity(word, tableName, wordNet)));    //map name nodes(table names)
			for (String colName : schema.getColumns(tableName)) {
				result.add(new NodeInfo("NN", tableName+"."+colName,
						WordSimilarity.getSimilarity(word, colName, wordNet)));    //map name nodes (attribute names)
				for (String value : schema.getValues(tableName, colName)) {
					if (word == null || value == null) {
						System.out.println("Comparing "+word+" and "+value);
						System.out.println("In table "+tableName+", column "+colName);
					}
					valueNodes.add(new NodeInfo("VN", tableName+"."+colName,
							WordSimilarity.getSimilarity(word, value, wordNet)));    //add every sample value into valueNodes
				}
			}
		}
		
		//map value nodes (table values), to get the value node with highest similarity, add its (type, value, score) into result
		// we want all candidates, not only the one with the highest similarity
		result.addAll(valueNodes);
		result.add(new NodeInfo("UNKNOWN", "meaningless", 1.0));
		Collections.sort(result, new NodeInfo.ReverseScoreComparator());
		return result;
	}

}
