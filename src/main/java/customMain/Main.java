package customMain;

import parsers.GfaException;
import parsers.GfaParser;

import db.*;

public abstract class Main {
	
//	private static Class c0 = Gui.class;
//	private static Class c1 = TutorialSceneswitch.class;
//	private static Class c2 = Graphdrawer.class;
//	static Class Tutorials[] = {c0, c1, c2};
	
	/**
	 * Launch application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "example";
		String gfaPath = System.getProperty("user.dir") + "/Data/" + filename + "/" + filename + ".gfa";
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
