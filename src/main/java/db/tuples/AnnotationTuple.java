package db.tuples;

import db.tables.AnnotationTable;

/**
 * 
 * Tuple to insert into the Link table.
 *
 */
public class AnnotationTuple extends Tuple {

	private String seqID;
	private String source;
	private String type;
	private String start;
	private String end;
	private String score;
	private String strand;
	private String phase;
	private String attribute;
	
	public AnnotationTuple(String seqid, String source, String type, String start, 
			String end, String score, String strand, String phase, String attribute) {
		this.seqID = seqid;
		this.source = source;
		this.type = type;
		this.start = start;
		this.end = end;
		this.score = score;
		this.strand = strand;
		this.phase = phase;
		this.attribute = attribute;
		this.table = new AnnotationTable();
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (" + "," + seqID + "," + source + "," + type + "," 
				+ start + "," + end + "," + score + "," + strand + "," 
				+ phase + "," + attribute + ")";
	}

}