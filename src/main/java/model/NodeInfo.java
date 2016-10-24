package model;

public class NodeInfo {
	public String type;
	public String value;
	public NodeInfo(String type, String value) {
		this.type = type;
		this.value = value;
	}
	@Override
	public String toString() {
		return type+": "+value;
	}
}
