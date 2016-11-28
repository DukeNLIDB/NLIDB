package model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.WordnetStemmer;

public class WordNet {
	String sep = File.separator;
	String wordNetDir = "lib" + sep + "WordNet-3.0" + sep + "dict";
	URL url;
	IRAMDictionary dict;
	WordnetStemmer stemmer;
	
	public WordNet() throws Exception {
		url = new URL("file", null, wordNetDir);
		dict = new RAMDictionary(url, ILoadPolicy.NO_LOAD);
		dict.open();
		System.out.println("Loading wordNet...");
		dict.load(true); // load dictionary into memory
		System.out.println("WordNet loaded.");
		
		stemmer = new WordnetStemmer(dict);
	}
	
	/**
	 * Find the similarity of two nouns.
	 * @param word1
	 * @param word2
	 * @return
	 */
	public double similarity(String word1, String word2) {
//		System.out.println("Finding similarity between: "+word1+" and "+word2);
		// remove all special characters from words
		if (word1.equals("") || word2.equals("")) { return 0.0; }
		word1 = word1.replaceAll("[^a-zA-Z0-9]", "");
		word2 = word2.replaceAll("[^a-zA-Z0-9]", "");
		if (word1.equals("") || word2.equals("")) { return 0.0; }
		// ? why NullPointerException here ??? Doesn't seem to be my fault!
		// Here special symbols in word causes Exception.
		List<String> stems1 = stemmer.findStems(word1, POS.NOUN);
		List<String> stems2 = stemmer.findStems(word2, POS.NOUN);
		
		if (stems1.isEmpty() || stems2.isEmpty()) {
//			System.out.println("One word cannot be identified in WordNet");
			return 0.0;
		}
		
		ArrayList<Set<ISynset>> visited1, visited2;
		visited1 = new ArrayList<>();
		visited2 = new ArrayList<>();

		List<IWordID> wordIDs1 = new ArrayList<>();
		for (String stem : stems1) {
			IIndexWord indexWord = dict.getIndexWord(stem, POS.NOUN);
			if (indexWord != null) {
				wordIDs1.addAll(dict.getIndexWord(stem, POS.NOUN).getWordIDs());
			}
		}
		if (wordIDs1.isEmpty()) { return 0.0; }
		List<ISynset> synsets1 = new ArrayList<>();
		for (IWordID wID : wordIDs1) { synsets1.add(dict.getWord(wID).getSynset());	}
		visited1.add(new HashSet<ISynset> (synsets1));
		
		List<IWordID> wordIDs2 = new ArrayList<>();
		for (String stem : stems2) {
			IIndexWord indexWord = dict.getIndexWord(stem, POS.NOUN);
			if (indexWord != null) {
				wordIDs2.addAll(dict.getIndexWord(stem, POS.NOUN).getWordIDs());
			}
		}
		if (wordIDs2.isEmpty()) { return 0.0; }
		List<ISynset> synsets2 = new ArrayList<>();
		for (IWordID wID : wordIDs2) { synsets2.add(dict.getWord(wID).getSynset()); }
		visited2.add(new HashSet<ISynset> (synsets2));
		
		boolean commonFound = false;
		ISynset commonSynset = null;
		boolean endSearch1 = false;
		boolean endSearch2 = false;
		
		int commonSynsetPos1 = -1;
		int commonSynsetPos2 = -1;
		
		while (!commonFound && !(endSearch1 && endSearch2)) {
			int sz1 = visited1.size();
			int sz2 = visited2.size();
			if (!commonFound && !endSearch1) { // check the newest of 1 against all of 2
				for (int i = 0; i < sz2; i++) {
					if (intersection(visited1.get(sz1-1), visited2.get(i)) != null) {
						commonSynsetPos1 = sz1-1;
						commonSynsetPos2 = i;
						commonSynset = intersection(visited1.get(sz1-1), visited2.get(i));
						commonFound = true;
						break;
					}
				}
			}
			if (!commonFound && !endSearch2) { // check the newest of 2 against all of 1
				for (int i = 0; i < sz1; i++) {
					if (intersection(visited1.get(i), visited2.get(sz2-1)) != null) {
						commonSynsetPos1 = i;
						commonSynsetPos2 = sz2-1;
						commonSynset = intersection(visited1.get(i), visited2.get(sz2-1));
						commonFound = true;
						break;
					}
				}
			}
			if (!commonFound) {
				if (!endSearch1) {
					Set<ISynset> hyperSet1 = getHyperSet(visited1.get(sz1-1));
					if (hyperSet1.isEmpty()) { endSearch1 = true; }
					else { visited1.add(hyperSet1); }
				}
				if (!endSearch2) {
					Set<ISynset> hyperSet2 = getHyperSet(visited2.get(sz2-1));
					if (hyperSet2.isEmpty()) { endSearch2 = true; }
					else { visited2.add(hyperSet2); }
				}
			}
		}
		
		if (commonSynset == null) { return 0.0; }
				
//		System.out.println("Common ancestor synset found: ");
//		System.out.println(commonSynset.getWord(1).getLemma());
//		System.out.println(commonSynset.getGloss());
//		System.out.println("Common synset pos1: "+commonSynsetPos1);
//		System.out.println("Common synset pos2: "+commonSynsetPos2);
//		System.out.println("Depth of this common ancestor is:"+findDepth(commonSynset));

		int N1 = commonSynsetPos1;
		int N2 = commonSynsetPos2;
		int N3 = findDepth(commonSynset);
		
		return 2*N3 / (double) (N1+N2+2*N3);
	}
	
	private int findDepth(ISynset synset) {
		if (synset.getRelatedSynsets(Pointer.HYPERNYM).isEmpty()) { return 0; }
		List<Set<ISynset>> list = new ArrayList<>();
		Set<ISynset> set = new HashSet<>();
		set.add(synset);
		list.add(set);
		boolean topReached = false;
		int depth = -1;
		while (!topReached) {
			Set<ISynset> nextSet = new HashSet<>();
			for (ISynset syn : list.get(list.size()-1)) {
				List<ISynsetID> hyperIDs = syn.getRelatedSynsets(Pointer.HYPERNYM);
				if (!hyperIDs.isEmpty()) {
					for (ISynsetID hyperID : hyperIDs) { nextSet.add(dict.getSynset(hyperID)); }
				} else {
					topReached = true;
					depth = list.size()-1;
					break;
				}
			}
			list.add(nextSet);
		}
		return depth;
	}
	
	private Set<ISynset> getHyperSet(Set<ISynset> set) {
		Set<ISynset> hyperSet = new HashSet<>();
		for (ISynset syn : set) {
			List<ISynsetID> hyperIDs = syn.getRelatedSynsets(Pointer.HYPERNYM);
			if (!hyperIDs.isEmpty()) {
				for (ISynsetID hyperID : hyperIDs) { hyperSet.add(dict.getSynset(hyperID)); }
			}
		}
		return hyperSet;
	}
	
	private ISynset intersection(Set<ISynset> set1, Set<ISynset> set2) {
		for (ISynset syn2 : set2) {
			if (set1.contains(syn2)) { return syn2; }
		}
		return null;
	}

	/**
	 * Testing method
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		WordNet net = new WordNet();
		String word1 = "scopes";
		String word2 = "book";
		System.out.printf("WUP similarity between %s and %s is: %f\n", word1, word2, net.similarity(word1, word2));
//		String word = "SCOPES";
//		List<IWordID> wordIDs = net.dict.getIndexWord(word, POS.NOUN).getWordIDs();
//		List<ISynset> synsets = new ArrayList<>();
//		for (IWordID wID : wordIDs) { synsets.add(net.dict.getWord(wID).getSynset());	}
//		
//		for (ISynset syn : synsets) {
//			System.out.println(syn.getGloss());
//			System.out.println("Words in this synset:");
//			for (IWord w : syn.getWords()) {
//				System.out.println(w.getLemma());
//			}
//		}
//		
//		ISynset hyper = net.dict.getSynset(synsets.get(0).getRelatedSynsets(Pointer.HYPERNYM).get(0));
//		System.out.println(hyper.getGloss());;
//		System.out.println(hyper.getWords().get(0).getLemma());
		
	}

}
