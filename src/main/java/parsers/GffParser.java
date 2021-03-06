package parsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import db.DatabaseManager;
import db.tables.AnnotationTable;
import db.tables.Table;
import db.tuples.AnnotationTuple;

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
		if (line.charAt(0) == '#') {
			return;
		}
		line = line.replace('.', '_');
		line = line.replace(' ', '_');
		line = line.replace('-', '_');
		line = line.replace('\'', ' ');
		String[] tuples = line.split("\t|;|=");
		storeTuples(tuples);
	}
	
	/**
	 * Method that adds the relevant data of the parsed data to the database.
	 * @param tuples contains the data of a line in an array
	 */
	
	private void storeTuples(String[] tuples) {
		
		if (tuples.length != 16) {
			return;
		}
		
		for (int i = 0; i < 16; i++) {
			if (tuples[i] == null) {
				tuples[i] = "null";
			} else if (tuples[i].equals("")) {
				tuples[i] = "null";
			}
		}
		
		dbManager.insert(new AnnotationTuple(tuples[0], 
				tuples[1], tuples[2], Integer.parseInt(tuples[3]), 
				Integer.parseInt(tuples[4]), tuples[5], tuples[6], 
				tuples[7], tuples[9], tuples[11], tuples[13],
				tuples[15]));
	}
}