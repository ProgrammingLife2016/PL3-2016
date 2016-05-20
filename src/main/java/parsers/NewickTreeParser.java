package parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
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
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(in), Charset.defaultCharset()))) {
			String string = reader.readLine();
			if (string != null) {
				return parse(string);
			}
			return null;
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

			if ("(".equals(currentToken)) {
				NewickTree newChild = new NewickTree();
				current.addChild(newChild);
				stack.push(current);
				current = newChild;
			} else if (":".equals(currentToken)) {
				String distanceToken = tokenizer.nextToken();
				double distance = Double.parseDouble(distanceToken);
				current.setDistance(distance);
			} else if (",".equals(currentToken)) {
				current = stack.peek();
				NewickTree newChild = new NewickTree();
				current.addChild(newChild);
				current = newChild;

			} else if (")".equals(currentToken)) {
				current = stack.pop();
			} else if (";".equals(currentToken)) {
				break;
			} else {
				current.setName(currentToken);
			}
		}
		return root;
	}
}
