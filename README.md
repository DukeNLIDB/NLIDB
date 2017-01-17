

# Natural Language Interface to DataBases (NLIDB)

### [The final report is here](https://github.com/DukeNLIDB/NLIDB/blob/master/report/final/final.pdf).

### How it works.
1. Parse the input and map nodes to SQL components and database attributes.
![nodes_mapping_example](https://github.com/DukeNLIDB/NLIDB/blob/master/report/final/figures/nodes_mapping_example.png)
![gui_nodes_mapping](https://github.com/DukeNLIDB/NLIDB/blob/master/report/final/figures/gui_nodes_mapping.png)

2. Adjust the structure of the parse tree to make it syntactically valid.
![gui_tree_adjustor](https://github.com/DukeNLIDB/NLIDB/blob/master/report/final/figures/gui_tree_adjustor1.png)

3. Translate the parse tree to an SQL query.
![gui_translation](https://github.com/DukeNLIDB/NLIDB/blob/master/report/final/figures/gui_translation.png)

*****

### Grammar rules of syntactically valid parse trees:

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

*****

### For developers:

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
