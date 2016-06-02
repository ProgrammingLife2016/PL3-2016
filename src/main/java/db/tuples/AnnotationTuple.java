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
	private int start;
	private int end;
	private String score;
	private String strand;
	private String phase;
	private String calhounclass;
	private String name;
	private String id;
	private String displayName;
	
	public AnnotationTuple(String seqid, String source, String type, int start, 
			int end, String score, String strand, String phase, String calhounclass,
			String name, String id, String displayName) {
		this.seqID = seqid;
		this.source = source;
		this.type = type;
		this.start = start;
		this.end = end;
		this.score = score;
		this.strand = strand;
		this.phase = phase;
		this.calhounclass = calhounclass;
		this.name = name;
		this.id = id;
		this.displayName = displayName;
		this.table = new AnnotationTable();
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (\'" +  seqID + "\',\'" + source + "\',\'" + type + "\'," 
				+ start + "," + end + ",\'" + score + "\',\'" + strand + "\',\'" 
				+ phase + "\',\'" + calhounclass + "\',\'" + name + 
				"\'," + id + ",\'" + displayName + "\')";
	}

}