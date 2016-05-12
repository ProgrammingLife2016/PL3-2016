package gui.phylogeny;

import javafx.scene.control.Label;

public class ExternalNewickNode extends NewickNode {
	
	public ExternalNewickNode(String name) {
		this(0,0,name);
	}

	public ExternalNewickNode(int x, int y, String name) {
		super(x, y);
		Label label = new Label(name);
		label.setTranslateX(10);
		label.setTranslateY(-8);
		this.getChildren().add(label);
	}

}
