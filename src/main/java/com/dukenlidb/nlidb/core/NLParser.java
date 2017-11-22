package com.dukenlidb.nlidb.core;

import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Natural language parser, a wrapper of the Stanford NLP parser.
 * @author keping
 *
 */
public class NLParser {
	public static MaxentTagger tagger;
	public static DependencyParser parser;

	private static NLParser initializedParser = null;

	private NLParser() {
		String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
		String modelPath = DependencyParser.DEFAULT_MODEL;
		tagger = new MaxentTagger(taggerPath);
		parser = DependencyParser.loadFromModelFile(modelPath);
	}

	public static NLParser getNLParser() {
		if (initializedParser == null) {
			initializedParser = new NLParser();
		}

		return initializedParser;
	}
	
}
