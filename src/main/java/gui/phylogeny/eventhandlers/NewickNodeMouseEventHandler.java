package gui.phylogeny.eventhandlers;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import gui.phylogeny.model.NewickNode;

/**
 * EventHandler for MouseEvents associated with the NewickNode.
 *
 */
public class NewickNodeMouseEventHandler implements EventHandler<MouseEvent> {

	/**
	 * NewickNode upon which this handler will take effect.
	 */
	private NewickNode newickNode;
	
	/**
	 * The MouseEventHandler for a designated NewickNode.
	 */
	public NewickNodeMouseEventHandler(NewickNode node) {
		newickNode = node;
	}

	/**
	 * Handles the MouseEvent.
	 */
	@Override
	public void handle(MouseEvent event) {
		toggleActive();
		newickNode.setChanged(true);
	}
	
	/**
	 * Makes a node appear active or non-active.
	 */
	public void toggleActive() {
		newickNode.toggleSelected();
		toggleColour();
	}
	
	/**
	 * Adjusts the colour to an active or non-active state.
	 */
	private void toggleColour() {
		if (newickNode.isSelected()) {
			newickNode.setColoured();
		} else {
			newickNode.unsetColoured();
		}
	}
}
