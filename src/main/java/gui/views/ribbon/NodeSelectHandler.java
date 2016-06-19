package gui.views.ribbon;

import db.DatabaseManager;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class NodeSelectHandler implements EventHandler<MouseEvent> {
	
	private int segmentId;
	private StringProperty selectedContent;
	private DatabaseManager dbm;
	private boolean selected = false;
	
	public NodeSelectHandler(int segmentId, StringProperty selectedContent, DatabaseManager dbm) {
		this.segmentId = segmentId;
		this.selectedContent = selectedContent;
		this.dbm = dbm;
	}
	
	@Override
	public void handle(MouseEvent event) {
		if (!selected) {
			selectedContent.set(dbm.getDbReader().getContent(segmentId));
			selected = true;
		} else {
			selectedContent.set("");
			selected = false;
		}
	}

}
