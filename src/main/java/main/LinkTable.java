package main;

/**
 * 
 * Table that contains the links between segments.
 *
 */
public class LinkTable extends Table {
	
	LinkTable() {
		this.name = "LINKS";
		this.names = new String[]{"FROMID","TOID"};
		this.types = new String[]{"INT","INT"};
	}
}
