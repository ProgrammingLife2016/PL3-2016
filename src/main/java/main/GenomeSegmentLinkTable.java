package main;

/**
 * 
 * Table that contains which genomes occur in each segment (and the other way
 * around).
 *
 */
public class GenomeSegmentLinkTable extends Table {
	
	public GenomeSegmentLinkTable() {
		this.name = "GENOMESEGMENTLINK";
		this.names = new String[]{"SEGMENTID","GENOMEID"};
		this.types = new String[]{"INT","INT"};
	}
}
