package model;

import java.util.Comparator;

/**
 * Immutable class indicating the SQL component for a Node.
 * @author keping
 *
 */
public class NodeInfo {
	// TODO: all fields should be private in final version.
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
	public NodeInfo(NodeInfo ni){
		this.type = ni.type;
		this.value = ni.value;
		this.score = ni.score;
	}
	@Override
	public String toString() {
		return type+": "+value;
	}
	public String getType() { return type; }
	public String getValue() {
		return value;
	}
	
	public double getScore(){
		return score;
	}
	
	public static class ReverseScoreComparator implements Comparator<NodeInfo> {
		@Override
		public int compare(NodeInfo a, NodeInfo b) {
			if (a.score < b.score) { return 1; }
			else if (a.score > b.score) { return -1; }
			else { return 0; }
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeInfo other = (NodeInfo) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public boolean ExactSameSchema (NodeInfo other) {

		if (type == null || other.getType() == null || value == null || other.getValue() == null) {
			return false;
		}

		if (type.equals(other.getType()) && value.equals(other.getValue())) {

			return true;
		}

		return false;
	}

	public boolean sameSchema (NodeInfo other) {

		if (type == null || other.getType() == null || value == null || other.getValue() == null) {
			return false;
		}

		int indexOfDot_Other = other.getValue().indexOf('.');

		int indexOfDot = value.indexOf('.');

		if (indexOfDot_Other == -1) {

			indexOfDot_Other = other.getValue().length();
		}

		if (indexOfDot == -1) {

			indexOfDot = value.length();
		}

		if (other.getValue().substring(0, indexOfDot_Other - 1)
			.equals(value.substring(0, indexOfDot - 1))) {

			return true;
		}


		return false;
	}
	
}
