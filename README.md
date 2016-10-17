# NLIDB
Natural Language Interface to DataBases

To work on the development, import it into eclipse, but first make sure you've installed the following eclipse plugins:

1.  m2eclipse
2.  e(fx)clipse

The next steps are:

1. [done] ~~Download the _Microsoft Academic Search Database_ and try connecting to it.~~ I(Keping) just couldn't find how to get that database downloaded and used in SQL, so I decided to first just use our dblp database in hw1.
2. [done] Use _Stanford NLP_ to parse a natural language sentence.
3. [done] According the data structure in _Stanford NLP_, design the data structure for class **ParseTree**. For now let's just make it feasible, without thinking about memory and time efficiency.
4. [done] A basic implementation of SchemaGraph.
5. Write the three most important classes, write test cases first!
  * **ParseTreeNodeMapper**
  * **ParseTreeStructureAdjuster**
  * **QueryTreeTranslator**
6. ...

* UI prettifying can be conducted along with other tasks.
