package model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class SchemaGraph {
	
	/**
	 * table name, column name, column type
	 */
	private Map<String, Map<String, String>> tables;
	
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
		Statement stmt = c.createStatement();
		tables = new HashMap<>();
		
		String[] types = {"TABLE"};
		ResultSet rs = meta.getTables(null, null, "%", types);
		
		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			tables.put(tableName, new HashMap<>());
			
			Map<String, String> table = tables.get(tableName);
			ResultSet cols = stmt.executeQuery("SELECT * FROM "+tableName);
			ResultSetMetaData rsmd = cols.getMetaData();
			int colCount = rsmd.getColumnCount();
			for (int i = 1; i <= colCount; i++) {
				table.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
			}
		}
		if (stmt != null) { stmt.close(); }
		System.out.println("Schema graph retrieved.");
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
}
