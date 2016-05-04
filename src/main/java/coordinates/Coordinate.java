package coordinates;

/** 
 * @author Rob Kapel
 * 
 * Simple coordinate system for storing a segment's coordinates.
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
