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
	
	private static void translateCondition(SQLQuery query, Node node) {
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
		query.add("WHERE", attribute+" "+compareSymbol+" "+value);
		query.add("FROM", attribute.split("\\.")[0]);
	}
	
	private static void translateNN(SQLQuery query, Node node) {
		query.add("SELECT", node.getInfo().getValue());
		query.add("FROM", node.getInfo().getValue().split("\\.")[0]);		
	}
	
	private static void translateNP(SQLQuery query, Node node) {
		translateNN(query, node);
		for (Node child : node.getChildren()) {
			if (child.getInfo().getType().equals("NN")) {
				translateNN(query, child);
			} else if (child.getInfo().getType().equals("ON") ||
					child.getInfo().getType().equals("VN")){
				translateCondition(query, child);
			}
		}
	}
	
	private static void translateGNP(SQLQuery query, Node node) {
		if (node.getInfo().getType().equals("FN")) {
			// TODO: Do something for the FN
			translateGNP(query, node.getChildren().get(0));
		} else if (node.getInfo().getType().equals("NN")) {
			translateNP(query, node);
		}
	}
	
	private static void translateComplexCondition(SQLQuery query, Node node) {
		// TODO;
	}
	
	private static void translateSClause(SQLQuery query, Node node) {
		if (!node.getInfo().getType().equals("SN")) { return; }
		translateGNP(query, node.getChildren().get(0));
	}
	
	private static void addJoinPath(SQLQuery query, SchemaGraph schema) {
		List<String> fromTables = new ArrayList<String>(query.getCollection("FROM"));
		if (fromTables.size() <= 1) { return; }
		String table1 = fromTables.get(0);
		String table2 = fromTables.get(1);
		Set<String> joinKeys = schema.getJoinKeys(table1, table2);
		for (String joinKey : joinKeys) {
			query.add("WHERE", table1+"."+joinKey+" = "+table2+"."+joinKey);
		}
	}
	
	public static SQLQuery translate(Node root, SchemaGraph schema) {
		SQLQuery query = new SQLQuery();
		if (!root.getWord().equals("ROOT")) {
			System.out.println("ROOT is not ROOT!");
			return query;
		}
		
		translateSClause(query, root.getChildren().get(0));
		if (root.getChildren().size() >= 2) {
			translateComplexCondition(query, root.getChildren().get(1));
		}
		
		if (schema != null) addJoinPath(query, schema);
		return query;
	}

}
