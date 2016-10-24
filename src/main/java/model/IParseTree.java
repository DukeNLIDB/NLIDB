package model;

import java.util.List;

/**
 * An interface for a parse tree. 
 * @author keping
 *
 */
public interface IParseTree extends Iterable<Node> {

	/**
	 * Get size of the ParseTree.
	 * @return number of nodes
	 */
	public int size();
	
	/**
	 * Restructure the parse tree by removing meaningless nodes.
	 * The Node object should contain information indicating whether
	 * it is meaningful. It is meaningful if it corresponds to an SQL component.
	 */
	public void removeMeaninglessNodes();
	
	/**
	 * Insert implicit nodes, mostly about the symmetry for comparison.
	 */
	public void insertImplicitNodes();
	
	/**
	 * Get a list of structurally adjusted parse trees.
	 * @return a list of adjusted trees
	 */
	public List<IParseTree> getAdjustedTrees();
	
	/**
	 * Get a score indicating the syntactic and semantic validity 
	 * of the parse tree for an SQL query.
	 * @return validity score
	 */
	public double getScore();
	
	/**
	 * Translate the parse tree into an SQL query.
	 * @return
	 */
	public SQLQuery translateToSQL();
	
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
	public boolean equals(IParseTree other);
	
	/**
	 * Get the hashCode for the parse tree. So that trees can be 
	 * stored in a HashMap and equal trees can be seen as one.
	 * @return hashCode for the object
	 */
	public int hashCode();
}
