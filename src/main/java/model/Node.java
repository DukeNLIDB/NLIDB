package model;

import java.util.Comparator;

/**
 * Node to indicate SQL component.
 * @author keping
 *
 */
public class Node {
	int index;
	String type;
	String value;
	double score = 2; // higher score means higher certainty
	public Node(int index, String type, String value) {
		this.index = index;
		this.type = type;
		this.value = value;
	}
	public int getIndex() { return index; }
	public double getProb() { return score; }
	public void setProb(double prob) { this.score = prob; }
	@Override
	public String toString() {
		return type+": "+value;
	}
	
	public static class ReverseScoreComparator implements Comparator<Node> {
		@Override
		public int compare(Node a, Node b) {
			return - (int) (a.score - b.score);
		}
	}

}
