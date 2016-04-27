package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseCreator {
	
	private final int SEGMENT_ID_IDX = 1;
	private final int SEGMENT_CONTENT_IDX = 2;
	private final int SEGMENT_GENOMES_IDX = 4;
//	private final int SEGMENT_REF_GENOME_IDX = 5;
//	private final int SEGMENT_REF_COORD_IDX = 6;
	
	private final int LINK_FROM_IDX = 1;
	private final int LINK_TO_IDX = 3;
	
	private HashMap<String,Integer> genomes = new HashMap<>();
	
	private Statement db;
	private Connection dbConnection;
	
	private List<Table> tables = new ArrayList<Table>();
	
	public DatabaseCreator() {
		tables.add(new SegmentTable());
		tables.add(new GenomeTable());
		tables.add(new LinkTable());
		tables.add(new GenomeSegmentLinkTable());
	}
	
	public void parse(String filename, String gfaDir, String dbDir) throws GfaException {
		
		String line;
		
		String fullDbPath = dbDir + filename;
		String fullGfaPath = gfaDir + filename + "/" + filename + ".gfa";
		
		try {
		    
		    dbConnection = DriverManager.getConnection("jdbc:h2:" + fullDbPath);
			db = dbConnection.createStatement();
			this.createTables();
			
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fullGfaPath));
		    while ((line = bufferedReader.readLine()) != null) {
		        char type = line.charAt(0);
		        
		        switch(type) {
			        case 'H': parseHeader(line); break;
			        case 'S': parseSegment(line); break;
			        case 'L': parseLink(line); break;
			        default: throw new GfaException();
		        }
		        
		    }
		    bufferedReader.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + fullGfaPath);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading file: " + fullGfaPath);
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void parseHeader(String line) throws SQLException {
        String[] split = line.split("\\s")[1].split(":");
        if(split[0].equals("ORI")) {
        	String[] genomeNames = split[2].split(";");
        	for(int i = 0; i < genomeNames.length; i++) {
        		String genomeName = genomeNames[i];
        		genomes.put(genomeName,i+1);
        		insert(new GenomeTuple(i+1,genomeName.substring(0,genomeName.length()-6)));
        	}
        }
	}
	
	private void parseSegment(String line) throws SQLException {
        String[] split = line.split("\\s");
        insert(new SegmentTuple(Integer.parseInt(split[SEGMENT_ID_IDX]),split[SEGMENT_CONTENT_IDX]));
		String[] genomesInSegment = split[SEGMENT_GENOMES_IDX].split(":")[2].split(";");
		for(String gen: genomesInSegment) {
			insert(new GenomeSegmentLinkTuple(Integer.parseInt(split[SEGMENT_ID_IDX]),genomes.get(gen)));
		}
	}
	
	private void parseLink(String line) throws SQLException {
        String[] split = line.split("\\s");
        insert(new LinkTuple(Integer.parseInt(split[LINK_FROM_IDX]),Integer.parseInt(split[LINK_TO_IDX])));
	}
	
	private void createTables() throws SQLException {
		for(Table table : tables) {
			this.db.executeUpdate(table.getCreateQuery());
		}
	}
	
	private void insert(Tuple tuple) throws SQLException {
		this.db.executeUpdate(tuple.getInsertQuery());
	}
	
}
