package com.dukenlidb.nlidb.service;

import com.dukenlidb.nlidb.model.DBConnectionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dukenlidb.nlidb.archive.model.NLParser;
import com.dukenlidb.nlidb.archive.model.Node;
import com.dukenlidb.nlidb.archive.model.NodeInfo;
import com.dukenlidb.nlidb.archive.model.NodeMapper;
import com.dukenlidb.nlidb.archive.model.ParseTree;
import com.dukenlidb.nlidb.archive.model.ParseTree.ParseTreeIterator;
import com.dukenlidb.nlidb.archive.model.SQLQuery;
import com.dukenlidb.nlidb.archive.model.SchemaGraph;

import java.sql.*;

import java.util.List;

@Service
public class TranslationService {
	private DBConnectionService dbConnectionService;

	private Connection conn = null;

	private NLParser parser;
	private SchemaGraph schema;
	private ParseTree parseTree;
	private NodeMapper nodeMapper;

	private ParseTreeIterator iter;
	private List<ParseTree> treeChoices;

	private Node node;
	private SQLQuery resultQuery;

	@Autowired
	public TranslationService(DBConnectionService dbConnectionService) {
		this.dbConnectionService = dbConnectionService;
		try {
			nodeMapper = new NodeMapper();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser = new NLParser(); // init parser;
	}

	/**
	 * process the natural language query and return a SQL translation
	 * @param query natural language query
	 * @return SQL translation of the input query
	 */
	public String translateToSQL(DBConnectionConfig config, String query) {
		try {
			conn = dbConnectionService.getConnection(config);
			schema = new SchemaGraph(conn); // read database schema
			System.out.println("Database Schema:\n\n"+schema.toString());

			parseQuery(query);
			mappingNodes();
			reformulateTree();
			toSQL();

			System.out.println("Resulted tree:\n" + parseTree.toString());


			conn.close();
			return resultQuery.toString();
		} catch(Exception e) {
			return e.getMessage();
		}
	}

	/**
	 * Parse natural language query and construct initial parse tree
	 * @param query natural language query
	 */
	private void parseQuery(String query) {
		System.out.println("Processing a query...");
		parseTree = new ParseTree(query, parser);
		System.out.println("Tree constructed");
	}

	/**
	 * Map nodes in parse tree into SQL components
	 */ 
	private void mappingNodes() {
		System.out.println("Mapping nodes to SQL components...");
		iter = parseTree.iterator();
		while (iter.hasNext()) {
			node = iter.next();
			List<NodeInfo> choices = nodeMapper.getNodeInfoChoices(node, schema);		

			if (choices.size() == 1) {
				System.out.println("Auto mapping:\n" + node.getWord() + " -> " + choices.get(0).toString());
				node.setInfo(choices.get(0));
			} else {
				// TODO: user confirms choices?
				System.out.println("Mapping choices:");
				for (int i=0; i<choices.size(); i++) {
					System.out.println(node.getWord() + " -> " + choices.get(i).toString() + " - Score: " + choices.get(i).getScore());
				}
				System.out.println("");
				node.setInfo(choices.get(0));
			}
		}
		System.out.println("Mapping completes.\n" + parseTree.toString());
	}

	private void reformulateTree() {
		System.out.println("Reconstructing parse tree...");
		parseTree.removeMeaninglessNodes();
		parseTree.mergeLNQN();
		treeChoices = parseTree.getAdjustedTrees();
		// TODO: user chooses adjusted tree
		parseTree = treeChoices.get(0);
		for (int i=0; i<treeChoices.size(); i++) {
			System.out.println("Tree" + i + " : " + treeChoices.get(i).toString());
		}

		System.out.println("Inserting implicit nodes...");
		parseTree.insertImplicitNodes();
	}

	private void toSQL() {
		resultQuery = parseTree.translateToSQL(schema);
	}

}