package db.tables;

/**
 * 
 * Table that contains data about annotations parsed from the gff file.
 *
 */
public class AnnotationTable extends Table {
	
	public AnnotationTable() {
		this.name = "ANNOTATION";
		this.names = new String[]{"SEQID", "SOURCE", "TYPE", "START", "END", "SCORE", "STRAND", "PHASE", "CALHOUNCLASS", "NAME", "ID", "DISPLAYNAME"};
		this.types = new String[]{"VARCHAR", "VARCHAR", "VARCHAR", "INT", "INT", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "BIGINT", "VARCHAR"};
	}
}
