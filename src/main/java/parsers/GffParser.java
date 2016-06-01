package parsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import db.DatabaseManager;
import db.tables.AnnotationTable;
import db.tables.BubbleTable;
import db.tables.GenomeSegmentLinkTable;
import db.tables.GenomeTable;
import db.tables.LinkTable;
import db.tables.SegmentTable;
import db.tables.Table;
import db.tuples.GenomeSegmentLinkTuple;
import db.tuples.GenomeTuple;
import db.tuples.LinkTuple;
import db.tuples.SegmentTuple;

public class GffParser {
	
	/**
	 * Required DatabaseManager for loading parsed lines into the database.
	 */
	private DatabaseManager dbManager;

	private ArrayList<Table> tables = new ArrayList<Table>();
	
	public GffParser(DatabaseManager dbManager) {
		this.dbManager = dbManager;
	}
	
	/**
	 * Reads through the gff file, parsing all the infomation into the database
	 * 
	 * @param file
	 * 				Path to the gff file.
	 * @throws GffException
	 * 				Exception thrown when execution fails.
	 */
	public void parse(String file) throws GffException {
		tables.add(new AnnotationTable());
		dbManager.createTables(tables);
		
		String line;
		try {
			BufferedReader bufferedReader = 
					new BufferedReader(new InputStreamReader(new FileInputStream(file), 
							Charset.defaultCharset()));
		    while ((line = bufferedReader.readLine()) != null) {
		    	parseLine(line);
		    }
		    
		    bufferedReader.close();
		    
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + file);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading file: " + file);
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that parses the line into a row that can be inserted into the table
	 * 
	 * @param line
	 * 			Link line to parse.
	 */
	private void parseLine(String line) {
		
	}	
}