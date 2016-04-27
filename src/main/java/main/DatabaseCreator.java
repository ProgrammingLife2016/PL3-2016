package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

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
//        System.out.println(line);
        String[] split = line.split("\\s")[1].split(":");
        if(split[0].equals("ORI")) {
        	String[] genomeNames = split[2].split(";");
        	for(int i = 0; i < genomeNames.length; i++) {
        		genomes.put(genomeNames[i],i+1);
        		insertGenome(i+1 + "",genomeNames[i].toString());
        	}
        }
	}
	
	private void parseSegment(String line) throws SQLException {
//		System.out.println(line);
        String[] split = line.split("\\s");
		insertSegment(split[SEGMENT_ID_IDX],split[SEGMENT_CONTENT_IDX]);
		String[] genomesInSegment = split[SEGMENT_GENOMES_IDX].split(":")[2].split(";");
		for(String gen: genomesInSegment) {
			insertGenomeSegmentLink(split[SEGMENT_ID_IDX],genomes.get(gen).toString());
		}
	}
	
	private void parseLink(String line) throws SQLException {
        String[] split = line.split("\\s");
		insertLink(split[LINK_FROM_IDX],split[LINK_TO_IDX]);
		System.out.println(split[LINK_FROM_IDX] + ", " + split[LINK_TO_IDX]);
        
	}
	
	private void createTables() throws SQLException {
		this.db.executeUpdate("CREATE TABLE SEGMENTS (ID INT PRIMARY KEY,CONTENT CLOB)");
		this.db.executeUpdate("CREATE TABLE LINKS (FROMID INT,TOID INT)");
		this.db.executeUpdate("CREATE TABLE GENOMES (ID INT PRIMARY KEY,NAME VARCHAR)");
		this.db.executeUpdate("CREATE TABLE GENOMESEGMENTLINK (SEGMENTID INT,GENOMEID INT)");
	}
	
	private void insertSegment(String id, String content) throws SQLException {
		this.db.executeUpdate("INSERT INTO SEGMENTS VALUES (" + id + ",\'" + content + "\')");
	}
	
	private void insertGenome(String id, String name) throws SQLException {
		System.out.println(id + ", " + name);
		this.db.executeUpdate("INSERT INTO GENOMES VALUES (" + id + ",\'" + name + "\')");
	}
	
	private void insertLink(String fromId, String toId) throws SQLException {
		this.db.executeUpdate("INSERT INTO LINKS VALUES (" + fromId + "," + toId + ")");
	}
	
	private void insertGenomeSegmentLink(String segmentId, String genomeId) throws SQLException {
		this.db.executeUpdate("INSERT INTO GENOMESEGMENTLINK VALUES (" + segmentId + "," + genomeId + ")");
	}
	
	
}
