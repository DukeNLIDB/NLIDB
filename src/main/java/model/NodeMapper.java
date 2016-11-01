package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		map.put("FN",     new NodeInfo("FN", "AVG"));	 // Function Node
		map.put("all",    new NodeInfo("QN", "ALL"));	 // Quantifier Node
		map.put("and",    new NodeInfo("FN", "AND"));	 // Logic Node
		map.put("or",    new NodeInfo("FN", "OR"));
		map.put("less",    new NodeInfo("FN", "<"));
		map.put("greater",    new NodeInfo("FN", ">"));
		map.put("not",    new NodeInfo("FN", "!="));
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
	 * @param node
	 * @param schema
	 * @return a ranked of NodeInfo
	 */
	public List<NodeInfo> getNodeInfoChoices(Node node, SchemaGraph schema) {
		List<NodeInfo> result = new ArrayList<NodeInfo>();
		String word = node.getWord();
		
		if (map.containsKey(word)) {
			result.add(map.get(word));
			return result;
		}
				
		for (String tableName : schema.getTableNames()) {
			result.add(new NodeInfo("NN", tableName,
					WordSimilarity.getSimilarity(word, tableName, wordNet)));
			for (String colName : schema.getColumns(tableName)) {
				result.add(new NodeInfo("NN", tableName+"."+colName,
						WordSimilarity.getSimilarity(word, colName, wordNet)));
				for (String value: schema.getValues(tableName, colName)){
					result.add(new NodeInfo("VN", tableName+"."+colName+":"+value,
							WordSimilarity.getSimilarity(word, value, wordNet)));
				}
			}
		}
		
		// TODO: search for Value Node (VN).
		
		
		result.add(new NodeInfo("UNKNOWN", "meaningless", 1.0));
		Collections.sort(result, new NodeInfo.ReverseScoreComparator());
		if (result.size() <= 6) { return result; }
		else { return result.subList(0, 6); }
	}

}
