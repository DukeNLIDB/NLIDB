package model;

import java.util.ArrayList;
import app.Controller;

public class ParseTreeNodeMapper {
	private Controller ctrl;
	public ParseTreeNodeMapper(Controller ctrl) {
		this.ctrl = ctrl;
	}
	
	public ParseTree mapTreeNode(ParseTree tree, SchemaGraph schemaGraph) {
		// TODO
		while (! tree.nodesMapped() ) {
			ctrl.showNodes(getPossibleNodes(tree, schemaGraph));
			// TODO: wait for user to choose.
			tree = ctrl.getUserChoiceNode();
		}
		return tree;
	}
	
	private ArrayList<ParseTree> getPossibleNodes(ParseTree tree, SchemaGraph schemaGraph) {
		// TODO
		return null;
	}
	
}
