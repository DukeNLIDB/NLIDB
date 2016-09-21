package model;

/**
 * Just a wrapper for a String of sql query.
 * @author keping
 */
public class SQLQuery {
	private String s;
	
	public SQLQuery(String s) { this.s = s; }
	/**
	 * Get the String query insides the SQLQuery.
	 * @return
	 */
	public String get() { return s; }
	@Override
	public String toString() { return s; }
}
