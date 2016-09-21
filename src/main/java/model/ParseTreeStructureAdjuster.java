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
		while (! tree.nodesMapped() ) {
			ctrl.showNodes(getPossibleStructures(tree, schemaGraph));
			// TODO: wait for user to choose.
			tree = ctrl.getUserChoiceStructure();
		}
		return tree;
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
