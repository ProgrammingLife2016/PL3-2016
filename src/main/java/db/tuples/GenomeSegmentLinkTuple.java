package db.tuples;

import db.tables.GenomeSegmentLinkTable;

/**
 * 
 * Tuple to insert into the GenomeSegmentLink table.
 *
 */
public class GenomeSegmentLinkTuple extends Tuple {

	private int segmentId;
	private int genomeId;
	
	public GenomeSegmentLinkTuple(int segmentId, int genomeId) {
		this.segmentId = segmentId;
		this.genomeId = genomeId;
		this.table = new GenomeSegmentLinkTable();
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (" + segmentId + "," + genomeId + ")";
	}

}
