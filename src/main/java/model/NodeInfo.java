package model;

import java.util.Comparator;

/**
 * Immutable class indicating the SQL component for a Node.
 * @author keping
 *
 */
public class NodeInfo {
	private String type;
	private String value;
	/**
	 * Similarity score of the Node to the column/table name in schema.
	 */
	private double score = 1.0;
	public NodeInfo(String type, String value) {
		this.type = type;
		this.value = value;
	}
	public NodeInfo(String type, String value, double score) {
		this(type, value);
		this.score = score;
	}
	@Override
	public String toString() {
		return type+": "+value;
	}
	
	public static class ReverseScoreComparator implements Comparator<NodeInfo> {
		@Override
		public int compare(NodeInfo a, NodeInfo b) {
			if (a.score < b.score) { return 1; }
			else if (a.score > b.score) { return -1; }
			else { return 0; }
		}
	}
}
