package model;

import java.util.ArrayList;

import app.Controller;

public class ParseTreeStructureAdjuster {
	private Controller ctrl;
	public ParseTreeStructureAdjuster(Controller ctrl) {
		this.ctrl = ctrl;
	}

	public ParseTree adjustStructure(ParseTree tree, SchemaGraph schemaGraph) {
		// TODO

		return null;
	}
	
	private ArrayList<ParseTree> getPossibleStructures(
			ParseTree tree, SchemaGraph schemaGraph) {
		// TODO:
		return null;
	}
	
	public QueryTree parseTreeToQueryTree(ParseTree parseTree) {
		QueryTree queryTree = new QueryTree();
		// TODO
		return queryTree;
	}
}
