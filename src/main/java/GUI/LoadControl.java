package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

@SuppressWarnings("restriction")
public class LoadControl implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent event) {
		System.out.println("loading DNA");
	}
}
