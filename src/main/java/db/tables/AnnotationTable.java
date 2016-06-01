package db.tables;

/**
 * 
 * Table that contains data about annotations parsed from the gff file.
 *
 */
public class AnnotationTable extends Table {
	
	public AnnotationTable() {
		this.name = "ANNOTATION";
		this.names = new String[]{"SEQID", "SOURCE", "TYPE", "START", "END", "SCORE", "STRAND", "PHASE", "ATTRIBUTES"};
		this.types = new String[]{"STRING", "STRING", "STRING", "STRING", "STRING", "STRING", "STRING", "STRING"};
	}
}
