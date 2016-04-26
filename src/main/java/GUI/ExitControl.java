package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

@SuppressWarnings("restriction")
public class ExitControl implements EventHandler<ActionEvent> {

	@Override
	public void handle(ActionEvent event) {
		System.out.println("Exiting DNAnalyzer");
		System.exit(0);
	}
}
