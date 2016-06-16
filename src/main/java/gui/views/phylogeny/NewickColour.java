package gui.views.phylogeny;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.paint.Paint;

/**
 * A map cointaining all colours for lineages
 */
public class NewickColour {
	static final Map<String, Paint> colourMap;
	
    static {
        colourMap = new HashMap<String, Paint>();
        colourMap.put("LIN1", Paint.valueOf("#ED00C3"));
        colourMap.put("LIN2", Paint.valueOf("#0000FF"));
        colourMap.put("LIN3", Paint.valueOf("#500079"));
        colourMap.put("LIN4", Paint.valueOf("#FF0000"));
        colourMap.put("LIN5", Paint.valueOf("#4E2C00"));
        colourMap.put("LIN6", Paint.valueOf("#69CA00"));
        colourMap.put("LIN7", Paint.valueOf("#FF7E00"));
        colourMap.put("LINanimal", Paint.valueOf("#00FF9C"));
        colourMap.put("LINB", Paint.valueOf("#00FF9C"));
        colourMap.put("LINCALETTII", Paint.valueOf("#00FFFF"));
        colourMap.put("Selected", Paint.valueOf("#778899"));
        colourMap.put("", Paint.valueOf("0x000000ff"));
    }
    
    /**
     * Retrieves the colour when selecting a node
     * @return		the grey colour
     */
    public static Paint selected() {
    	return colourMap.get("Selected");
    }
}