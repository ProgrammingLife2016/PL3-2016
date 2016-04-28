package main;

import java.util.ArrayList;
import java.util.List;

import db.*;
import db.tables.*;

public class Main {

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
