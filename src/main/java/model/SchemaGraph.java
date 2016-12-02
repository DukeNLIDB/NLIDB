package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SchemaGraph {
	
	/**
	 * table name, column name, column type
	 */
	private Map<String, Map<String, String>> tables;
	//table name, column name, column values
	private Map<String, Map<String, Set<String>>> tableRows;
	
	/**
	 * table name, primary key (set of column names).
	 * Two tables are connected only if pubkey of table1 is a
	 * column of table2, but NOT the pubkey of table2. Graph no direction.
	 */
	private Map<String, Set<String>> keys;
	
	/**
	 * table1Name, table2Name
	 */
	private Map<String, Set<String>> connectivity;
	
	/**
	 * Construct a schemaGraph from database meta data.
	 * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/sql/
	 * DatabaseMetaData.html#getTables%28java.lang.String,%20java.lang.
	 * String,%20java.lang.String,%20java.lang.String%5b%5d%29">document of getTables</a>
	 * @param meta
	 * @throws SQLException
	 */
	public SchemaGraph(Connection c) throws SQLException {
		System.out.println("Retrieving schema graph...");
		DatabaseMetaData meta = c.getMetaData();
		tables = new HashMap<>();
		tableRows = new HashMap<>();
		String[] types = {"TABLE"};
		ResultSet rsTable = meta.getTables(null, null, "%", types);
		

	    Statement stmt = c.createStatement();
		while (rsTable.next()) {
			String tableName = rsTable.getString("TABLE_NAME");
			tables.put(tableName, new HashMap<>());
			tableRows.put(tableName, new HashMap<>());
			
			Map<String, String> table = tables.get(tableName);
			Map<String, Set<String>> tableRow = tableRows.get(tableName);
			
			ResultSet rsColumn = meta.getColumns(null, null, tableName, null);
			while (rsColumn.next()){
				/*retrieve column info for each table, insert into tables*/
				String columnName = rsColumn.getString("COLUMN_NAME");
				String columnType = rsColumn.getString("TYPE_NAME");
				table.put(columnName, columnType); 
				/*draw random sample of size 10000 from each table, insert into tableRows*/
				String query = "SELECT " + columnName + " FROM " + tableName + " ORDER BY RANDOM() LIMIT 2000;";
				ResultSet rows = stmt.executeQuery(query);
				tableRow.put(columnName, new HashSet<String>());
				Set<String> columnValues = tableRow.get(columnName);
				while (rows.next()){
					String columnValue = rows.getString(1);
					//testing if the last column read has a SQL NULL
					if (!rows.wasNull())
						columnValues.add(columnValue);
				}
			}			
		}
		if (stmt != null) { stmt.close(); }
		readPrimaryKeys(meta);
		findConnectivity();
		System.out.println("Schema graph retrieved.");
	}

	private void readPrimaryKeys(DatabaseMetaData meta) throws SQLException {
		keys = new HashMap<>();
		for (String tableName : tables.keySet()) {
			ResultSet rsPrimaryKey = meta.getPrimaryKeys(null, null, tableName);
			keys.put(tableName, new HashSet<String>());
		    while (rsPrimaryKey.next()) {
		    	keys.get(tableName).add(rsPrimaryKey.getString("COLUMN_NAME"));
		    }
		}
//		System.out.println(keys);
	}
	
	private void findConnectivity() {
		connectivity = new HashMap<String, Set<String>>();
		for (String tableName : tables.keySet()) {
			connectivity.put(tableName, new HashSet<String>());
		}
		for (String table1 : tables.keySet()) {
			for (String table2 : tables.keySet()) {
				if (table1.equals(table2)) { continue; }
				if (!getJoinKeys(table1, table2).isEmpty()) {
					connectivity.get(table1).add(table2);
					connectivity.get(table2).add(table1);
				}
			}
		}
	}

	public Set<String> getJoinKeys(String table1, String table2) {
		Set<String> table1Keys = keys.get(table1);
		Set<String> table2Keys = keys.get(table2);
		if (table1Keys.equals(table2Keys)) { return new HashSet<String>(); }
		boolean keys1ContainedIn2 = true;
		for (String table1Key : table1Keys) {
			if (!tables.get(table2).containsKey(table1Key)) {
				keys1ContainedIn2 = false;
				break;
			}
		}
		if (keys1ContainedIn2) { return new HashSet<String>(table1Keys); }
		
		boolean keys2ContainedIn1 = true;
		for (String table2Key : table2Keys) {
			if (!tables.get(table1).containsKey(table2Key)) {
				keys2ContainedIn1 = false;
				break;
			}
		}
		if (keys2ContainedIn1) { return new HashSet<String>(table2Keys); }
		
		return new HashSet<String>();
	}
	
	/**
	 * Return a list of String as join path in the form of:
	 * <br> table1 table3 table2
	 * <br> Shortest join path is found using BFS.
	 * <br> The join keys can be found using {@link #getJoinKeys(String, String)} 
	 * @param table1
	 * @param table2
	 * @return
	 */
	public List<String> getJoinPath(String table1, String table2) {
		if (!tables.containsKey(table1) || !tables.containsKey(table2)) {
			return new ArrayList<String>();
		}
		// Assume table1 and table2 are different.
		// Find shortest path using BFS.
		HashMap<String, Boolean> visited = new HashMap<>();
		for (String tableName : tables.keySet()) {
			visited.put(tableName, false);
		}
		HashMap<String, String> prev = new HashMap<>(); // the parent tableName
		LinkedList<String> queue = new LinkedList<>();
		queue.addLast(table1);
		visited.put(table1, true);
		boolean found = false;
		while (!queue.isEmpty() && !found) {
			String tableCurr = queue.removeFirst();
			for (String tableNext : connectivity.get(tableCurr)) {
				if (!visited.get(tableNext)) {
					visited.put(tableNext, true);
					queue.addLast(tableNext);
					prev.put(tableNext, tableCurr);
				}
				if (tableNext.equals(table2)) { found = true; }
			}
		}

		LinkedList<String> path = new LinkedList<>();
		if (visited.get(table2)) {
			String tableEnd = table2; 
			path.push(tableEnd);
			while (prev.containsKey(tableEnd)) {
				tableEnd = prev.get(tableEnd);
				path.push(tableEnd);
			}
		}
		return path;
	}
	
	public Set<String> getTableNames() {
		return tables.keySet();
	}
	
	public Set<String> getColumns(String tableName) {
		return tables.get(tableName).keySet();
	}
	
	public Set<String> getValues(String tableName, String columnName){
		return tableRows.get(tableName).get(columnName);
	}

	@Override
	public String toString() {
		String s = "";
		for (String tableName : tables.keySet()) {
			s += "table: "+tableName+"\n";
			s += "{";
			Map<String, String> columns = tables.get(tableName);
			for (String colName : columns.keySet()) {
				s += colName+": "+columns.get(colName)+"\t";
			}
			s += "}\n\n";
		}
		return s;
	}
	
	public static void main(String[] args) throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dblp", "dblpuser", "dblpuser");
		SchemaGraph schema = new SchemaGraph(connection);
		System.out.println("The join path between article and authorship:");
		System.out.println(schema.getJoinPath("article", "authorship"));
		System.out.println("The join path between authorship and article:");
		System.out.println(schema.getJoinPath("authorship", "article"));
		System.out.println("The join path between inproceedings and authorship:");
		System.out.println(schema.getJoinPath("inproceedings", "authorship"));
		System.out.println("The join path between article and inproceedings:");
		System.out.println(schema.getJoinPath("article", "inproceedings"));
		System.out.println("----------------------------------------------");
		System.out.println("The join keys between article and authorship:");
		System.out.println(schema.getJoinKeys("article", "authorship"));
		System.out.println("The join keys between article and inproceedings:");
		System.out.println(schema.getJoinKeys("article", "inproceedings"));
		System.out.println("The join keys between inproceedings and authorship:");
		System.out.println(schema.getJoinKeys("inproceedings", "authorship"));
	}
}
