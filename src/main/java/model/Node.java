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
	double prob = 2; // 2 means no probability
	public Node(int index, String type, String value) {
		this.index = index;
		this.type = type;
		this.value = value;
	}
	public int getIndex() { return index; }
	public double getProb() { return prob; }
	public void setProb(double prob) { this.prob = prob; }
	@Override
	public String toString() {
		return type+": "+value;
	}
	
	public class ReverseProbComparator implements Comparator<Node> {
		@Override
		public int compare(Node a, Node b) {
			return - (int) (a.prob - b.prob);
		}
	}

}
