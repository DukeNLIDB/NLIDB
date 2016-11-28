# NLIDB
Natural Language Interface to DataBases

This is a project managed using maven. Just in case, if you don't know about maven, checkout this wonderful [tutorial](https://www.udemy.com/apachemaven/), which you have to pay for though...

Right now it uses the dblp database on local machine. To connect to the database, make sure you have database "dblp" on your localhost with post 5432, accessible to user "dblpuser" with password "dblpuser". Or modify the `startConnection()` method in class `app.Controller` to connect to database.

To get hands on the development, import it into eclipse, but first make sure you've installed the following eclipse plugins:

1. m2eclipse (for using maven in eclipse)
2. e(fx)clipse (for using javafx smoothly in eclipse)

To use WordNet inside the project (I'm using MIT JWI as the interface, which is already included in maven `pom.xml`):

1. Create a folder "lib" in the project base directory.
2. Download [WordNet](https://wordnet.princeton.edu/wordnet/download/) into that "lib" directory just created.
3. Extract the downloaded WordNet. 
4. Finally just make sure "$(basedir)/lib/WordNet-3.0/dict/" exists. (Or you have to modify the path inside class `model.WordNet`.)

The entry point of the application is the `main()` method in `ui.UserView` class. 

*****

### The next steps:

**NOTE** Whatever functionality you are writing, be sure to provide the **test cases**.

1. [done] ~~Download the _Microsoft Academic Search Database_ and try connecting to it.~~ I(Keping) just couldn't find how to get that database downloaded and used in SQL, so I decided to first just use our dblp database in hw1.
2. [done] Use _Stanford NLP_ to parse a natural language sentence.
3. [done] According the data structure in _Stanford NLP_, design the data structure for class **ParseTree**. For now let's just make it feasible, without thinking about memory and time efficiency.
4. [done] A basic implementation of SchemaGraph.
5. [done] **ParseTreeNodeMapper**
6. [in-progress]**ParseTreeStructureAdjuster**:
    * [tests needed] Remove meaningless nodes.
    * Merge logic nodes and quantifier nodes with their parents.
    * Reorder the nodes.
    * Insert implicit nodes.
7. [in-progress] **QueryTreeTranslator**:
    * [done] A basic translator for a single table. "SELECT ... FROM ... WHERE ...;"
    * Add "AND", "OR" logic for "WHERE".
    * Add function without groupby for "SELECT".
    * Add group by.
    * Add join for multiple tables.
    * Add quantifier: all, each, any.
    * ...
8. ...

* UI design is conducted in parallel with the requirements of the above tasks.

*****

Here is the grammar rules of syntactically valid parse trees:

1. Q -> (SClause)(ComplexCondition)\*
2. SClause -> SELECT + GNP
3. ComplexCondition -> ON + (leftSubtree\*rightSubtree)
4. leftSubtree -> GNP
5. rightSubtree -> GNP | VN | MIN | MAX
6. GNP -> (FN + GNP) | NP
7. NP -> NN + (NN)\*(condition)\*
8. condition -> VN | (ON + VN)

Note:  
All terminal nodes are defined in the paper.  
\+ represents a parent-child relationship.  
\* represents a sibling relationship.  
One Query (Q) can must have one SClause and zero or more ComplexConditions.  
A ComplexCondition must have one ON, with a leftSubtree and a rightSubtree.  
An NP is: one NN (since an SQL query has to select at least one attribute), whose children
are multiple NNs and Conditions. (All other selected attributes and conditions are stacked
here to form a wide "NP" tree.)
