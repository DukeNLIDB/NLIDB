package model;

import java.util.List;

/**
 * An interface for a parse tree. 
 * @author keping
 *
 */
public interface IParseTree extends Iterable<Node> {

	/**
	 * Get size of the ParseTree, including the root Node.
	 * @return number of nodes
	 */
	public int size();
	
	/**
	 * Return the number of edit of the ParseTree.
	 */
	public int getEdit();
	
	/**
	 * Set the number of edit of the ParseTree.
	 */
	public void setEdit(int edit);
	
	/**
	 * Restructure the parse tree by removing meaningless nodes.
	 * The Node object should contain information indicating whether
	 * it is meaningful. It is meaningful if it corresponds to an SQL component.
	 */
	public void removeMeaninglessNodes();
	
	/**
	 * Restructure the parse tree by merging Logic Nodes and Quantifier Nodes with their parents.
	 */
	public ParseTree mergeLNQN();
	
	/**
	 * Insert implicit nodes, mostly about the symmetry for comparison.
	 */
	public void insertImplicitNodes();
	
	/**
	 * Get a list of structurally adjusted parse trees.
	 * @return a list of adjusted trees
	 */
	public List<ParseTree> getAdjustedTrees();
	
	/**
	 * Translate the parse tree into an SQL query.
	 * @return
	 */
	public SQLQuery translateToSQL(SchemaGraph schema);
	
	/**
	 * Convert the tree to a String for easier debugging.
	 * @return string representation
	 */
	public String toString();
	
	/**
	 * Check equality for two IParseTree objects, for searching 
	 * them in a HashMap.
	 * @param other
	 * @return true if they are equal
	 */
	public boolean equals(Object obj);
	
	/**
	 * Get the hashCode for the parse tree. So that trees can be 
	 * stored in a HashMap and equal trees can be seen as one.
	 * @return hashCode for the object
	 */
	public int hashCode();
}
