package db;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import parsers.GfaException;
import parsers.GffException;
import parsers.GffParser;



public class GffParseTest {
	
	private static DatabaseManager dbm;
	private static GffParser parser;
	
	/**
	 * Locations of test files.
	 */
	private static String filename = "testgff";
	private static String gffPath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TestData" + File.separator + filename + ".gff";
	private static String dbPath = System.getProperty("user.dir") + File.separator + "db"
			+ File.separator + filename;

	/**
	 * Run necessary functions once before running all tests.
	 * @throws GfaException
	 * @throws GffException 
	 */
	@BeforeClass
	public static void before() throws GffException {
		dbm = new DatabaseManager(dbPath);
		parser = new GffParser(dbm);
		dbm.cleanDbDirectory();
		parser.parse(gffPath);
	}
	
	/**
	 * Close database connection, or risk system crashes.
	 */
	@AfterClass
	public static void cleanup() {
		dbm.closeDbConnection();
		dbm.cleanDbDirectory();
	}
	
	@Test
	public void getAnnotationStartLocations() {
		ArrayList<Integer> startLocations = dbm.getDbReader().getAllAnnotationStartLocations();
		for (int i = 0; i <= 2; i++) {
			assertEquals(10, (int)startLocations.get(0));
			assertEquals(20, (int)startLocations.get(1));
			assertEquals(50, (int)startLocations.get(2));
		}
	}
	
	@Test
	public void getAnnotationEndLocations() {
		ArrayList<Integer> endLocations = dbm.getDbReader().getAllAnnotationEndLocations();
		assertEquals(30, (int)endLocations.get(0));
		assertEquals(40, (int)endLocations.get(1));
		assertEquals(70, (int)endLocations.get(2));
	}
	
	@Test
	public void getAnnotationNames() {
		ArrayList<String> names = dbm.getDbReader().getAllAnnotationNames();
		assertEquals("name1", names.get(0));
		assertEquals("name2", names.get(1));
		assertEquals("name3", names.get(2));
	}
}
