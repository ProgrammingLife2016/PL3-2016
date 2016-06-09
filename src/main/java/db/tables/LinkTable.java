package db.tables;

/**
 * 
 * Table that contains the links between segments.
 *
 */
public class LinkTable extends Table {
	
	public LinkTable() {
		this.name = "LINKS";
		this.names = new String[]{"FROMID","TOID", "GENOMEID"};
		this.types = new String[]{"INT","INT", "INT"};
	}
}
