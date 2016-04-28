package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.EmptyStackException;
import java.util.Stack;

import parsers.Tree;
import parsers.TreeNode;

/**
 * @author James
 *
 *         Parses the newick portion of a file For nexus files, additional
 *         node-number mapping is needed to rename files Identification of a
 *         file as either newick or nexus determines contents
 * 
 */
public class TreeParser {
//	/**
//	 * Nexus file identifier. We look for this as the first token to identify a
//	 * tree file as Nexus, or other.
//	 */
//	private static final String nexusFileID = "#NEXUS";
//	/** Begin tag. */
//	private static final String beginTag = "begin";
//	/** End tag. */
//	private static final String endTag = "end";
	// trees section
//	/** Tree section. */
//	private static final String treeSectionTag = "trees";
//	/** Tree ID. */
//	private static final String treeID = "tree";
//	/** Tree ID (same or similar to {@link #treeID}?). */
//	private static final String utreeID = "utree"; // two different tree IDs?

	/** Line (and tree information) termination. */
	private static final char lineTerminator = ';';
	/** Equality sign. */
//	private static final char equals = '=';
//	/** Nexus comment open. */
//	private static final char commentOpen = '[';
//	/** Nexus comment close. */
//	private static final char commentClose = ']';

	/**
	 * True: show debug output. False: suppress printing.
	 */
	private static boolean debugOutput = false;
	private StreamTokenizer tokenizer;
	/**
	 * Root node of the tree being parsed. Must be initialized outside the
	 * tokenizer.
	 */
	private TreeNode rootNode;

	/**
	 * Initializes parsing of a tree by creating a tokenizer and setting default
	 * properties (such as spacing, quoting characters).
	 * {@link #tokenize(long, String, JProgressBar)} is required to start the
	 * parsing.
	 * 
	 * @param b
	 *            Buffered reader that could start in the middle of a nexus file
	 *            or the start of a newick file (basically the beginning of a
	 *            newick tree, is run for each tree in a nexus file)
	 */
	public TreeParser(BufferedReader b) {
		tokenizer = new StreamTokenizer(b);
		tokenizer.eolIsSignificant(false);
		tokenizer.quoteChar('"');
		// tokenizer.quoteChar('\''); // TODO: check quote layering, quoted
		// quotes
		tokenizer.wordChars('\'', '\''); // quote problem, turn this into a
											// prime symbol?
		// 32 = space
		tokenizer.wordChars('!', '!'); // 33
		// 34 = "
		tokenizer.wordChars('#', '&'); // 35-38
		// 39-41 = '() newick
		tokenizer.wordChars('*', '+'); // 42-43
		// 44 = , newick
		tokenizer.wordChars('-', '/'); // 45-47
		// 48-59 = [0-9]:;
		tokenizer.wordChars('<', '<'); // 60
		// 61 = = nexus
		tokenizer.wordChars('>', '@'); // 62-64
		// 65-90 = [A-Z]
		// tokenizer.wordChars('[', '['); // 91 [ nexus comment character, treat
		// as char
		// 92 = \ (esc, support esc'd spaces)
		// 93 = ] nexus comment character
		tokenizer.wordChars('^', '`'); // 93-96
		// 97-122 = [a-z]
		tokenizer.wordChars('{', '~'); // 123-126
		// 127 = del
	}

	/**
	 * Debug printout function. Avoid using the system calls and use this, and
	 * set flag {@link #debugOutput} depending on debugging or not.
	 * 
	 * @param s
	 *            Display the string, for debugging.
	 */
	public void debugOutput(String s) {
		if (debugOutput)
			System.out.println(s);
	}

	/**
	 * Adds node at the top of the stack to the tree. TreeNode is already
	 * created based on Newick properties.
	 * 
	 * @param name
	 *            Name of the node.
	 * @param nodeStack
	 *            Stack of nodes that haven't been added to the tree yet. Nodes
	 *            are popped when they have names and all children are
	 *            processed.
	 * @return Newly added treeNode linked into the tree.
	 */
	private TreeNode popAndName(String name, Stack nodeStack) {
		if(nodeStack.isEmpty()) {
			return null;
		}
		TreeNode topNode = (TreeNode) nodeStack.pop();
		if (name == null) {
			topNode.label = "";
			topNode.setName("");
		} else {
			topNode.label = name;
			topNode.setName(name);
		}
		try {
			TreeNode parent = (TreeNode) nodeStack.peek();
			parent.addChild(topNode);
		} catch (EmptyStackException e) {
			if (topNode != rootNode)
				System.out.println("Parser error on node " + topNode);
		}
		topNode.setExtremeLeaves(); // sets leftmost and rightmost leaf,
									// non-recursive
		topNode.setNumberLeaves(); // sets number of leaves, non-recursive
		topNode.linkNodesInPreorder();
		topNode.linkNodesInPostorder();
		return topNode;
	}

	/**
	 * Newick tokenizer: converts a string (tree as a string) into a tree
	 * object. The stream tokenizer should be initialized before calling this
	 * function.
	 * 
	 * @param fileLength
	 *            Length of the file, for progress bar movements. For nexus
	 *            files, this would be the relative position of the next
	 *            semicolon = the size of the tree in bytes.
	 * @param streamName
	 *            Name of the tree or file that is being loaded. Nexus files
	 *            have names ("tree <name> = ((...));", newick trees are named
	 *            by file name.
	 * @param progressBar
	 *            Reference to a progress bar widgit, embedded perhaps in place
	 *            of the new canvas for this tree. If this is null, create a new
	 *            progress bar here.
	 * @return Tree parsed from the stream.
	 */
	public Tree tokenize(long fileLength, String streamName) {
		final char openBracket = '(', closeBracket = ')', childSeparator = ',', treeTerminator = lineTerminator,
				quote = '\'', doubleQuote = '"', infoSeparator = ':';
		int progress = 0;
		rootNode = new TreeNode();
		Tree t = new Tree();
		t.setRootNode(rootNode);
		t.setFileName(streamName);
		Stack nodeStack = new Stack();
		nodeStack.push(rootNode);
		int thisToken;
		TreeNode lastNamed = null;
		boolean EOT = false;
		boolean nameNext = true;
		int percentage = 0;
		try {
			while (EOT == false && (thisToken = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
				switch (thisToken) {
				// case quote:
				case doubleQuote:
				case StreamTokenizer.TT_WORD:
					if (!nameNext)
						System.err.println("Error: didn't expect this name here: " + tokenizer.sval);
					lastNamed = popAndName(tokenizer.sval, nodeStack);
					progress += tokenizer.sval.length();
					nameNext = false;
					break;
				case StreamTokenizer.TT_NUMBER:
					if (nameNext)
						lastNamed = popAndName(tokenizer.sval, nodeStack);
					else {
						if (lastNamed != null)
							lastNamed.setWeight(tokenizer.nval);
						else
							System.err.println("Error: can't set value " + tokenizer.nval + " to a null node");
						lastNamed = null;
					}
					progress += (new Double(tokenizer.nval).toString()).length();
					nameNext = false;
					break;
				case infoSeparator:
					if (nameNext)
						lastNamed = popAndName(null, nodeStack);
					progress += 1;
					nameNext = false;
					break;
				case treeTerminator:
				case StreamTokenizer.TT_EOF:
					if (nameNext)
						lastNamed = popAndName(null, nodeStack);
					EOT = true;
					progress += 1;
					nameNext = false;
					break;
				case openBracket:
					nodeStack.push(new TreeNode());
					progress += 1;
					nameNext = true;
					break;
				case closeBracket:
					if (nameNext)
						lastNamed = popAndName(null, nodeStack);
					progress += 1;
					nameNext = true;
					break;
				case childSeparator:
					if (nameNext)
						lastNamed = popAndName(null, nodeStack);
					nodeStack.push(new TreeNode());
					progress += 1;
					nameNext = true;
					break;
				default:
					debugOutput("default " + (char) thisToken);
					break;
				}
			}
		} catch (IOException e) {
		}
		if (!nodeStack.isEmpty())
			System.err.println("Node stack still has " + nodeStack.size() + " things");
		t.postProcess();
		return t;
	}

	/**
	 * Test application function.
	 * 
	 * @param args
	 *            Program arguments. Only first argument used (for filename).
	 */
	public static void main(String[] args) {
		String fileName = "/home/daniel/Projects/TU/Y2/TI2806_Context/PL3-2016/Data/TB10/340tree.rooted.TKK.nwk";
		long start = System.currentTimeMillis();
		File f = new File(fileName);
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));
			TreeParser tp = new TreeParser(r);
			Tree t = tp.tokenize(f.length(), f.getName());
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find file: " + fileName);
		}
		System.out.println("Parsed in " + ((System.currentTimeMillis() - start) / 1000.0) + " s");
		System.exit(0);
	}

}
