package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * See the paper by Fei Li and H. V. Jagadish for the defined grammar.
 * @author keping
 *
 */
public class SQLTranslator {
	private SQLQuery query;
	private SchemaGraph schema;
	private Node root;
	
	public SQLTranslator(Node root, SchemaGraph schema) {
		this.root = root;
		this.schema = schema;
		query = new SQLQuery();
		
		translateSClause(root.getChildren().get(0));
		if (root.getChildren().size() >= 2) {
			translateComplexCondition(root.getChildren().get(1));
		}
		
		if (schema != null) addJoinPath();
	}
	
	public SQLQuery getResult() { return query; } 
	
	
	private static boolean isNumber(String str) {
	    int length = str.length();
	    if (length == 0) { return false; }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) { return false; }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9' && c != '.') { return false; }
	    }
	    return true;
	}
	
	private void translateCondition(Node node) {
		String attribute = "ATTRIBUTE";
		String compareSymbol = "=";
		String value = "VALUE";
		if (node.getInfo().getType().equals("VN")) {
			attribute = node.getInfo().getValue();
			value = node.getWord();
		} else if (node.getInfo().getType().equals("ON")) {
			compareSymbol = node.getInfo().getValue();
			Node VN = node.getChildren().get(0);
			attribute = VN.getInfo().getValue();
			value = VN.getWord();
		}
		if (!isNumber(value)) { value = "\""+value+"\""; }
		query.add("WHERE", attribute+" "+compareSymbol+" "+value);
		query.add("FROM", attribute.split("\\.")[0]);
	}

	private void translateNN(Node node) {
		translateNN(node, "");
	}
	private void translateNN(Node node, String valueFN) {
		if (!node.getInfo().getType().equals("NN")) { return; }
		if (!valueFN.equals("")) {
			query.add("SELECT", valueFN+"("+node.getInfo().getValue()+")");
		} else {
			query.add("SELECT", node.getInfo().getValue());
		}
		query.add("FROM", node.getInfo().getValue().split("\\.")[0]);		
	}
	
	private void translateNP(Node node) {
		translateNP(node, "");
	}
	private void translateNP(Node node, String valueFN) {
		translateNN(node, valueFN);
		for (Node child : node.getChildren()) {
			if (child.getInfo().getType().equals("NN")) {
				translateNN(child);
			} else if (child.getInfo().getType().equals("ON") ||
					child.getInfo().getType().equals("VN")){
				translateCondition(child);
			}
		}
	}
	
	private void translateGNP(Node node) {
		if (node.getInfo().getType().equals("FN")) {
			translateNP(node.getChildren().get(0), node.getInfo().getValue());
		} else if (node.getInfo().getType().equals("NN")) {
			translateNP(node);
		}
	}
	
	private void translateComplexCondition(Node node) {
		// TODO;
	}
	
	private void translateSClause(Node node) {
		if (!node.getInfo().getType().equals("SN")) { return; }
		translateGNP(node.getChildren().get(0));
	}
	
	private void addJoinPath() {
		List<String> fromTables = new ArrayList<String>(query.getCollection("FROM"));
		if (fromTables.size() <= 1) { return; }
		String table1 = fromTables.get(0);
		String table2 = fromTables.get(1);
		Set<String> joinKeys = schema.getJoinKeys(table1, table2);
		for (String joinKey : joinKeys) {
			query.add("WHERE", table1+"."+joinKey+" = "+table2+"."+joinKey);
		}
	}

}
