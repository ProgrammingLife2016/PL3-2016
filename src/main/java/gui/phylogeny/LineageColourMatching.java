package gui.phylogeny;

import java.util.HashMap;

/**
 * Matches phylogenetic lineages to their corresponding colours, as required for the phylogenetic tree.
 */
public class LineageColourMatching {

	/**
	 * List of all possible lineages.
	 */
	private static String[] lineagelist = {
			"LIN1", "LIN2", "LIN3", "LIN4", "LIN5",
			"LIN6", "LIN7", "LINanimal", "LINB", "LINCALETTII",
			""
	};
	
	/**
	 * List of all possible lineage colours.
	 */
	private static String[] lineagecolourlist = {
			"#ED00C3", "#0000FF", "#500079", "#FF0000", "#4E2C00",
			"#69CA00", "#FF7E00", "#00FF9C", "#00FF9C", "#00FFFF",
			"0x000000ff"
	};
	
	public static String getLineageColour(String lineage) {
		String lineagecolour = "";
		for (int i = 0; i < lineagelist.length; i++) {
			if (lineage.equals(lineagelist[i])) {
				lineagecolour = lineagecolourlist[i];
			}
		}
		return lineagecolour;
	}

}
