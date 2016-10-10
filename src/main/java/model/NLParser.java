package model;

import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Natural language parser, a wrapper of the Stanford NLP parser.
 * @author keping
 *
 */
public class NLParser {
	MaxentTagger tagger;
	DependencyParser parser;

	public NLParser() {
		String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
		String modelPath = DependencyParser.DEFAULT_MODEL;
		tagger = new MaxentTagger(taggerPath);
		parser = DependencyParser.loadFromModelFile(modelPath);
	}
	
}
