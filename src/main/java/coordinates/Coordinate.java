package coordinates;

/**
 * Simple coordinate object for positioning of segments.
 * 
 * @author Rob Kapel
 */
public class Coordinate {
	private int xc;
	private int yc;
	
	public Coordinate(int xcoord, int ycoord) {
		xc = xcoord;
		yc = ycoord;
	}
	
	public int getX() {
		return xc;
	}
	
	public int getY() {
		return yc;
	}
}
