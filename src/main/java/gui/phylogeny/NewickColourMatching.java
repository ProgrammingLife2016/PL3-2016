package gui.phylogeny;

import javafx.scene.paint.Paint;

/**
 * Matches phylogenetic lineages to their corresponding colours,
 * as required for the phylogenetic tree.
 */
public class NewickColourMatching {

	/**
	 * List of all possible lineages.
	 */
	private static final String[] LINEAGELIST = {
			"LIN1", "LIN2", "LIN3", "LIN4", "LIN5",
			"LIN6", "LIN7", "LINanimal", "LINB", "LINCALETTII",
			""
	};
	
	/**
	 * List of all possible lineage colours.
	 */
	private static final String[] LINEAGECOLOURLIST = {
			"#ED00C3", "#0000FF", "#500079", "#FF0000", "#4E2C00",
			"#69CA00", "#FF7E00", "#00FF9C", "#00FF9C", "#00FFFF",
			"0x000000ff"
	};
	
	private static final String GRAY_COLOUR = "#778899";
	
	/**
	 * Returns colour associated with a certain lineage.
	 * 
	 * @param lineage
	 * 			Lineage of a specimen.
	 * @return lineagecolour
	 * 			The colour corresponding to the lineage.
	 */
	public static Paint getLineageColour(String lineage) {
		String lineagecolour = "";
		for (int i = 0; i < LINEAGELIST.length; i++) {
			if (lineage.equals(LINEAGELIST[i])) {
				lineagecolour = LINEAGECOLOURLIST[i];
			}
		}
		return Paint.valueOf(lineagecolour);
	}
	
	/**
	 * Returns Paint object containing a gray colour.
	 * 
	 * @return Paint
	 */
	public static Paint getDeactivatedColour() {
		return Paint.valueOf(GRAY_COLOUR);
	}
}
