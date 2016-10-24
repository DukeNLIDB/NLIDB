package model;

import java.util.HashSet;
import java.util.Set;

/**
 * A class with only static methods to help calculate similarity between two words.
 * @author keping
 *
 */
public final class WordSimilarity {
	private WordSimilarity() { } 
	
	/**
	 * WordNet WUP similarity.
	 * @param word1
	 * @param word2
	 * @return
	 */
	private static double semanticalSimilarity(String word1, String word2, WordNet wordNet) {
		return wordNet.similarity(word1, word2);
	}
	
	/**
	 * Jaccord Coefficient
	 * @param word1
	 * @param word2
	 * @return
	 */
	private static double lexicalSimilarity(String word1, String word2) {
		Set<Character> charSet1 = new HashSet<>();
		Set<Character> charSet2 = new HashSet<>();
		Set<Character> commonSet= new HashSet<>();
		for (char c : word1.toCharArray()) { charSet1.add(c); }
		for (char c : word2.toCharArray()) { charSet2.add(c); }
		for (char c : charSet1) {
			if (charSet2.contains(c)) { commonSet.add(c); }
		}
		double jaccord = commonSet.size() / (double) (charSet1.size() +
				charSet2.size() + commonSet.size());
		return Math.sqrt(jaccord);
	}
	
	/**
	 * The similarity score between two words. This score is a combination of 
	 * semantic similarity and lexical similarity.
	 * @param word1
	 * @param word2
	 * @return similarity score between word1 and word2
	 */
	public static double getSimilarity(String word1, String word2, WordNet wordNet) {
		return Math.max(semanticalSimilarity(word1, word2, wordNet),
				lexicalSimilarity(word1, word2));
	}
	
}
