package db.tables;

/**
 * 
 * Table that contains the genome id's and names.
 *
 */
public class GenomeTable extends Table {
	
	public GenomeTable() {
		this.name = "GENOMES";
		this.names = new String[]{"ID","NAME"};
		this.types = new String[]{"INT","VARCHAR"};
		this.primaryKeyIdx = 0;
	}
}
