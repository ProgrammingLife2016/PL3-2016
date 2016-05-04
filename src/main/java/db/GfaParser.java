package db;

import db.tables.GenomeSegmentLinkTable;
import db.tables.GenomeTable;
import db.tables.LinkTable;
import db.tables.SegmentTable;
import db.tables.Table;
import db.tuples.GenomeSegmentLinkTuple;
import db.tuples.GenomeTuple;
import db.tuples.LinkTuple;
import db.tuples.SegmentTuple;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GfaParser {
	
	/**
	 * These 5 constants are used to designate specific parts of a line that was
	 * parsed from the gfa file.
	 */
	private static final int SEGMENTID_IDX = 1;
	private static final int SEGMENTCONTENT_IDX = 2;
	private static final int SEGMENTGENOMES_IDX = 4;
	private static final int LINKFROM_IDX = 1;
	private static final int LINKTO_IDX = 3;
	
	/**
	 * List of tables contained within the database.
	 */
	private List<Table> tables = new ArrayList<>();
	
	/**
	 * Hashmap of genomes for effiently storing specific genomes.
	 */
	private HashMap<String,Integer> genomes = new HashMap<>();
	
	/**
	 * Required DatabaseManager for loading parsed lines into the database.
	 */
	private DatabaseManager dbManager;

	
	public GfaParser(DatabaseManager dbManager) {
		this.dbManager = dbManager;
	}
	
	/**
	 * Reads the gfa file with the given path and stores the results in the
	 * table using it's DatabaseManager.
	 * 
	 * @param gfaPath
	 * 				Path to the gfa file.
	 * @throws GfaException
	 * 				Exception thrown when execution fails.
	 */
	public void parse(String gfaPath) throws GfaException {
		tables.add(new SegmentTable());
		tables.add(new GenomeTable());
		tables.add(new LinkTable());
		tables.add(new GenomeSegmentLinkTable());
		dbManager.createTables(tables);
		
		String line;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(gfaPath));
		    while ((line = bufferedReader.readLine()) != null) {
		        char type = line.charAt(0);
		        
		        switch (type) {
			        case 'H': parseHeader(line);
			        break;
			        case 'S': parseSegment(line);
			        break;
			        case 'L': parseLink(line);
			        break;
			        default: throw new GfaException();
		        }
		    }
		    
		    bufferedReader.close();
		    
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + gfaPath);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading file: " + gfaPath);
			e.printStackTrace();
		}
	}
	
	/**
	 * Helper method to parse a Header line
	 * 
	 * @param line
	 * 			Header line to parse.
	 */
	private void parseHeader(String line) {
        String[] split = line.split("\\s")[1].split(":");
        if (split[0].equals("ORI")) {
        	String[] genomeNames = split[2].split(";");
        	for (int i = 0; i < genomeNames.length; i++) {
        		String genomeName = genomeNames[i];
        		genomes.put(genomeName, i + 1);
        		dbManager.insert(new GenomeTuple(i + 1,
        					genomeName.substring(0,genomeName.length() - 6)));
        	}
        }
	}
	
	/**
	 * Helper method to parse a Segment line
	 * 
	 * @param line
	 * 			Segment line to parse.
	 * 			
	 */
	private void parseSegment(String line) {
		String[] split = line.split("\\s");
		dbManager.insert(new SegmentTuple(Integer.parseInt(split[SEGMENTID_IDX]),
				split[SEGMENTCONTENT_IDX]));
		String[] genomesInSegment = split[SEGMENTGENOMES_IDX].split(":")[2].split(";");
		for (String gen : genomesInSegment) {
			dbManager.insert(new GenomeSegmentLinkTuple(Integer.parseInt(split[SEGMENTID_IDX]),
					genomes.get(gen)));
		}
	}
	
	/**
	 * Helper method to parse a Link line
	 * 
	 * @param line
	 * 			Link line to parse.
	 */
	private void parseLink(String line) {
		String[] split = line.split("\\s");
		dbManager.insert(new LinkTuple(Integer.parseInt(split[LINKFROM_IDX]), 
				Integer.parseInt(split[LINKTO_IDX])));
	}
		
}
