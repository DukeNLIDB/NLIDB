package model;

import java.util.Iterator;
import java.util.List;

public class ParseTree implements IParseTree {
	
	public ParseTree(String text, NLParser parser) {
		// TODO
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeMeaninglessNodes() {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertImplicitNodes() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IParseTree> getAdjustedTrees() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getScore() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SQLQuery translateToSQL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(IParseTree other) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class ParseTreeIterator implements Iterator<Node> {

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Node next() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	/**
	 * The default iterator in ParseTree returns the Nodes
	 * using their order in the sentence.
	 */
	@Override
	public ParseTreeIterator iterator() {
		return new ParseTreeIterator();
	}
	
	/**
	 * Get the natural language sentence corresponding to this
	 * parse tree.
	 * @return sentence
	 */
	public String getSentence() {
		// TODO
		return "sentence";
	}
	
}
