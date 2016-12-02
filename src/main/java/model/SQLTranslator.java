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
	private int blockCounter = 1;
	
	public SQLTranslator(Node root, SchemaGraph schema) {
		this(root, schema, false);
	}
	
	/**
	 * Translating a block, starting from translateGNP.
	 * @param root
	 * @param schema
	 */
	public SQLTranslator(Node root, SchemaGraph schema, boolean block) {
		if (!block) {
			this.schema = schema;
			query = new SQLQuery();
			
			translateSClause(root.getChildren().get(0));
			if (root.getChildren().size() >= 2) {
				translateComplexCondition(root.getChildren().get(1));
			}
			
			if (schema != null) addJoinPath();
		} else {
			this.schema = schema;
			query = new SQLQuery();
			translateGNP(root);
		}
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
			if (node.getChildren().isEmpty()) { return; }
			translateNP(node.getChildren().get(0), node.getInfo().getValue());
		} else if (node.getInfo().getType().equals("NN")) {
			translateNP(node);
		}
	}
	
	private void translateComplexCondition(Node node) {
		if (!node.getInfo().getType().equals("ON")) { return; }
		if (node.getChildren().size() != 2) { return; }
		SQLTranslator transLeft = new SQLTranslator(node.getChildren().get(0), schema, true);
		SQLTranslator transRight= new SQLTranslator(node.getChildren().get(1), schema, true);
		query.addBlock(transLeft.getResult());
		query.addBlock(transRight.getResult());
		query.add("WHERE", "BLOCK"+(blockCounter++)+" "+node.getInfo().getValue()+" "+"BLOCK"+(blockCounter++));
	}
	
	private void translateSClause(Node node) {
		if (!node.getInfo().getType().equals("SN")) { return; }
		translateGNP(node.getChildren().get(0));
	}
	
	private void addJoinKeys(String table1, String table2) {
		Set<String> joinKeys = schema.getJoinKeys(table1, table2);
		for (String joinKey : joinKeys) {
			query.add("WHERE", table1+"."+joinKey+" = "+table2+"."+joinKey);
		}
	}
	
	private void addJoinPath(List<String> joinPath) {
		for (int i = 0; i < joinPath.size()-1; i++) {
			addJoinKeys(joinPath.get(i), joinPath.get(i+1));
		}
	}
	
	private void addJoinPath() {
		List<String> fromTables = new ArrayList<String>(query.getCollection("FROM"));
		if (fromTables.size() <= 1) { return; }
		for (int i = 0; i < fromTables.size()-1; i++) {
			for (int j = i+1; j < fromTables.size(); j++) {
				List<String> joinPath = schema.getJoinPath(fromTables.get(i), fromTables.get(j));
				addJoinPath(joinPath);
			}
		}
	}

}
