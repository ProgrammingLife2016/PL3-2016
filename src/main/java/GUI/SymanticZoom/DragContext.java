package gui.SymanticZoom;

/**
 * Mouse drag context used for scene and nodes.
 */
public class DragContext {

    private double mouseAnchorX;
    private double mouseAnchorY;

    private double translateAnchorX;
    private double translateAnchorY;
	public double getMouseAnchorX() {
		return mouseAnchorX;
	}
	public void setMouseAnchorX(double mouseAnchorX) {
		this.mouseAnchorX = mouseAnchorX;
	}
	public double getMouseAnchorY() {
		return mouseAnchorY;
	}
	public void setMouseAnchorY(double mouseAnchorY) {
		this.mouseAnchorY = mouseAnchorY;
	}
	public double getTranslateAnchorX() {
		return translateAnchorX;
	}
	public void setTranslateAnchorX(double translateAnchorX) {
		this.translateAnchorX = translateAnchorX;
	}
	public double getTranslateAnchorY() {
		return translateAnchorY;
	}
	public void setTranslateAnchorY(double translateAnchorY) {
		this.translateAnchorY = translateAnchorY;
	}

}