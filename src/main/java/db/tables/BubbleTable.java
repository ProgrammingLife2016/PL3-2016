package db.tables;

/**
 * 
 * Table that contains the start and end segment id's of all innermost bubbles in the graph.
 *
 */
public class BubbleTable extends Table {
	
	public BubbleTable() {
		this.name = "BUBBLES";
		this.names = new String[]{"FROMID","TOID","GENOMEID"};
		this.types = new String[]{"INT","INT","INT"};
	}
}
