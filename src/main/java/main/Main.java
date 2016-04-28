package main;

import gui.Graphdrawer;
import gui.Gui;
import gui.TutorialSceneswitch;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.List;

import db.*;
import db.tables.*;

@SuppressWarnings("restriction")
public abstract class Main extends Application {
	
	private static Class c0 = Gui.class;
	private static Class c1 = TutorialSceneswitch.class;
	private static Class c2 = Graphdrawer.class;
	static Class Tutorials[] = {c0, c1, c2};
	
	/**
	 * Launch application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String filename = "TB10";
		String gfaPath = System.getProperty("user.dir") + "/Data/" + filename + "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		
		List<Table> tables = new ArrayList<>();
		tables.add(new SegmentTable());
		tables.add(new GenomeTable());
		tables.add(new LinkTable());
		tables.add(new GenomeSegmentLinkTable());
		
		DatabaseManager dbManager = new DatabaseManager(dbPath, tables);
		GfaParser parser = new GfaParser(dbManager);
		
		try {
			parser.parse(gfaPath);
		} catch (GfaException e) {
			e.printStackTrace();
		}
	}
}
