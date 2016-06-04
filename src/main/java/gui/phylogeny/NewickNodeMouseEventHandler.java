package gui.phylogeny;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;

public class NewickNodeMouseEventHandler implements EventHandler<MouseEvent> {

	/**
	 * NewickNode upon which this handler will take effect.
	 */
	private NewickNode newicknode;
	
	public NewickNodeMouseEventHandler(NewickNode node) {
		newicknode = node;
	}

	@Override
	public void handle(MouseEvent event) {
		toggleActive();
	}
	
	/**
	 * Makes a node appear active or non-active.
	 */
	public void toggleActive() {
		newicknode.toggleSelected();
		toggleColour();
	}
	
	/**
	 * Adjusts the colour according to the "selected" state of a node.
	 */
	private void toggleColour() {
		if (newicknode.isSelected()) {
			newicknode.setColoured();
		} else {
			newicknode.unsetColoured();
		}
	}
}
	
//	/**
//	 * Colors all relevant children in the Group light grey.
//	 */
//	public void turnChildrenGrey(NewickNode node) {
//		for (Object child : newicknode.getChildren()) {
//			if (child instanceof NewickNode) {
//				((NewickNode) child).unsetColoured();
//				if (!((NewickNode) child).isLeaf()) {
//					((NewickNode) child).turnChildrenGrey();
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Colors all relevant children in the Group black.
//	 */
//	public void turnChildrenColoured() {
//		for (Object child : this.getChildren()) {
//			if (child instanceof NewickNode) {
//				((NewickNode) child).getRectangle().setFill(Paint
//						.valueOf(NewickColourMatching
//						.getLineageColour((((NewickNode) child).getLineage()))));
//				if (((NewickNode) child).getLabel() != null) {
//					((NewickNode) child).getLabel().setTextFill(Paint
//							.valueOf(NewickColourMatching
//							.getLineageColour((((NewickNode) child).getLineage()))));
//				}
//				if (((NewickNode) child).isLeaf() == false) {
//					((NewickNode) child).turnChildrenColoured();
//				}
//			}
//		}
//	}
//}
