package parsers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Stack;
import java.util.StringTokenizer;

import newick.NewickTree;

/**
 * 
 * Parser for the Newick Tree format. Can be used to parse phylogenetic trees.
 * Inspired by the <a href="https://github.com/AbeelLab/LifeTiles">PL-2015
 * LifeTiles Project</a>
 *
 */
public class NewickTreeParser {

	/**
	 * Parses the Newick Tree that is contained in the given file.
	 * 
	 * @param in
	 *            The file to parse
	 * @return The parsed NewickTree
	 * @throws IOException
	 */
	public static NewickTree parse(File in) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(in))) {
			String string = reader.readLine();
			return parse(string);
		}
	}
	
	
	/**
	 * Parses the input String that resembles a Newick Tree.
	 * 
	 * @param in
	 *            The inputstring.
	 * @return The parsed NewickTree
	 */
	public static NewickTree parse(String in) {
		NewickTree root = new NewickTree();
		NewickTree current = root;
		StringTokenizer tokenizer = new StringTokenizer(in, "(:,):", true);
		Stack<NewickTree> stack = new Stack<>();

		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();

			// Enter a new Tree
			if ("(".equals(currentToken)) {
				NewickTree newChild = new NewickTree();
				current.addChild(newChild);
				stack.push(current);
				current = newChild;
			}

			// Add the distance
			else if (":".equals(currentToken)) {
				String distanceToken = tokenizer.nextToken();
				double distance = Double.parseDouble(distanceToken);
				current.setDistance(distance);
			}

			// Add the next child to the current parent node
			else if (",".equals(currentToken)) {
				current = stack.peek();
				NewickTree newChild = new NewickTree();
				current.addChild(newChild);
				current = newChild;

			}

			// Retreive the finished parent node
			else if (")".equals(currentToken)) {
				current = stack.pop();
			}

			// Stop parsing
			else if (";".equals(currentToken)) {
				break;
			}

			// Add the name to the current node.
			else {
				current.setName(currentToken);
			}
		}
		return root;
	}
	
	/**
	 * For testing purposes only, will be removed.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(parse("(A:10,(B:3,C:9):12)").toString());
		try {
			String res = parse(
					new File("/home/daniel/Projects/TU/Y2/TI2806_Context_PL/"
			+ "PL3-2016/Data/TB10/340tree.rooted.TKK.nwk"))
							.toString();
			System.out.println(res);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
