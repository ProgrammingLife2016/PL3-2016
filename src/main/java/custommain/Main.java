package custommain;

import db.DatabaseManager;
import db.GfaException;
import db.GfaParser;

public abstract class Main {
	
	/**
	 * Launch application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "example";
		String gfaPath = System.getProperty("user.dir") + "/Data/" + filename
				+ "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		
		DatabaseManager dbManager = new DatabaseManager(dbPath);
		GfaParser parser = new GfaParser(dbManager);
		
		try {
			parser.parse(gfaPath);
		} catch (GfaException e) {
			e.printStackTrace();
		}
		
		
	}
}
