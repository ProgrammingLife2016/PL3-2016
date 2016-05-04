package parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import db.DatabaseManager;
import db.tables.GenomeSegmentLinkTable;
import db.tables.GenomeTable;
import db.tables.LinkTable;
import db.tables.SegmentTable;
import db.tables.Table;
import db.tuples.*;

public class GfaParser {
	
	private final int SEGMENT_ID_IDX = 1;
	private final int SEGMENT_CONTENT_IDX = 2;
	private final int SEGMENT_GENOMES_IDX = 4;
	
	private final int LINK_FROM_IDX = 1;
	private final int LINK_TO_IDX = 3;
	
	private List<Table> tables = new ArrayList<>();
	
	private HashMap<String,Integer> genomes = new HashMap<>();
	private DatabaseManager dbManager;

	
	public GfaParser(DatabaseManager dbManager) {
		this.dbManager = dbManager;
	}
	
	/**
	 * Reads the gfa file with the given path and stores the results in the
	 * table using it's DatabaseManager.
	 * 
	 * @param gfaPath
	 *            Path to the gfa file.
	 * @throws GfaException
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
			        case 'H': parseHeader(line); break;
			        case 'S': parseSegment(line); break;
			        case 'L': parseLink(line); break;
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
	 * @param line
	 */
	private void parseHeader(String line) {
        String[] split = line.split("\\s")[1].split(":");
        if (split[0].equals("ORI")) {
        	String[] genomeNames = split[2].split(";");
        	for (int i = 0; i < genomeNames.length; i++) {
        		String genomeName = genomeNames[i];
        		genomes.put(genomeName, i + 1);
        		dbManager.insert(new GenomeTuple(i + 1, genomeName.substring(0,genomeName.length() - 6)));
        	}
        }
	}
	
	/**
	 * Helper method to parse a Segment line
	 * @param line
	 */
	private void parseSegment(String line) {
		String[] split = line.split("\\s");
		dbManager.insert(new SegmentTuple(Integer.parseInt(split[SEGMENT_ID_IDX]),
				split[SEGMENT_CONTENT_IDX]));
		String[] genomesInSegment = split[SEGMENT_GENOMES_IDX].split(":")[2].split(";");
		for (String gen : genomesInSegment) {
			dbManager.insert(new GenomeSegmentLinkTuple(Integer.parseInt(split[SEGMENT_ID_IDX]),
					genomes.get(gen)));
		}
	}
	
	/**
	 * Helper method to parse a Link line
	 * @param line
	 */
	private void parseLink(String line) {
		String[] split = line.split("\\s");
		dbManager.insert(new LinkTuple(Integer.parseInt(split[LINK_FROM_IDX]), 
				Integer.parseInt(split[LINK_TO_IDX])));
	}
		
}
