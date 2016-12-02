package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Just a wrapper for a String of sql query.
 * @author keping
 */
public class SQLQuery {
	private List<SQLQuery> blocks;
	private Map<String, Collection<String>> map;
	
	SQLQuery() {
		map = new HashMap<>();
		map.put("SELECT", new ArrayList<String>());
		map.put("FROM", new HashSet<String>());
		map.put("WHERE", new HashSet<String>());
		blocks = new ArrayList<SQLQuery>();
	}

	@Deprecated
	public SQLQuery(String s) {
		
	}
	
	/**
	 * Get the String query insides the SQLQuery.
	 * @return
	 */
	String get() { return toString(); }
	
	public void addBlock(SQLQuery query) {
		blocks.add(query);
		add("FROM", "BLOCK"+blocks.size());
	}
	
	Collection<String> getCollection(String keyWord) { return map.get(keyWord); }
	
	/**
	 * Add (key, value) to the SQL Query.
	 * For example, (SELECT, article.title) or (FROM, article).
	 * @param key
	 * @param val
	 */
	void add(String key, String value) {
		map.get(key).add(value);
	}
	
	
	/**
	 * Serve for the toString() method.
	 * @param SELECT (or FROM)
	 * @return one line of arguments of that query (SELECT, FROM)
	 */
	private StringBuilder toSBLine(Collection<String> SELECT) {
		StringBuilder sb = new StringBuilder();
		for (String val : SELECT) {
			if (sb.length() == 0) {
				sb.append(val);
			} else {
				sb.append(", ").append(val);
			}
		}
		return sb;
	}
	
	/**
	 * Similar to {@link #toSBLine(Collection)}, but that incorporates
	 * the information of "AND" and "OR".
	 * @param WHERE
	 * @return
	 */
	private StringBuilder toSBLineCondition(Collection<String> WHERE) {
		StringBuilder sb = new StringBuilder();
		for (String val : WHERE) {
			if (sb.length() == 0) {
				sb.append(val);
			} else {
				// currently only allow for "AND"
				// TODO: add "OR"
				sb.append(" AND ").append(val);
			}
		}
		return sb;
	}
	
	@Override
	public String toString() {
		if (map.get("SELECT").isEmpty() || map.get("FROM").isEmpty()) {
			return "Illegal Query"; 
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < blocks.size(); i++) {
			sb.append("BLOCK"+(i+1)+":").append("\n");
			sb.append(blocks.get(i).toString()).append("\n");
			sb.append("\n");
		}
		sb.append("SELECT ").append(toSBLine(map.get("SELECT"))).append("\n");
		sb.append("FROM ").append(toSBLine(map.get("FROM"))).append("\n");
		if (!map.get("WHERE").isEmpty()) {
			sb.append("WHERE ").append(toSBLineCondition(map.get("WHERE"))).append("\n");
		}
		sb.append(";\n");
		return sb.toString();
	}
	
	
}
