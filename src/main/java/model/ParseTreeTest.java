package model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ParseTreeTest {
	private static boolean setUpDone = false;
	NLParser parser;
	
	@Before
	public void setUp() {
		if (setUpDone) { return; }
		parser = new NLParser();
	}
	
	@Test
	public void testParseTree() {
		String input = "I can almost always tell when movies use fake dinosaurs.";
		System.out.println(new ParseTree(input, parser));
	}

}
