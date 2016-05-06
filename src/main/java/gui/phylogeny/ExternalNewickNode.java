package gui.phylogeny;

import javafx.scene.control.Label;

public class ExternalNewickNode extends NewickNode {

	public ExternalNewickNode(int x, int y, String name) {
		super(x, y);
		Label label = new Label(name);
		label.setTranslateX(x + 10);
		label.setTranslateY(y - 5);
		this.getChildren().add(label);
	}

}
