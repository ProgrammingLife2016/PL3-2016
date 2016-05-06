package db;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hugokooijman
 *
 * Test if parser reads correct content and deals with errors correctly.
 */
public class GfaParserTest {
	
	private static DatabaseManager dbm;
	private static DatabaseManager dbmerror;
	private static GfaParser parser;
	private static GfaParser parsererror;
	
	/**
	 * Locations of test files.
	 */
	private static String dbPath = System.getProperty("user.dir") + "/db/" + "testread";
//	private static String dbErrorPath = System.getProperty("user.dir") + "/db/dberror/" + "testreaderror";
	private static String gfaPath = System.getProperty("user.dir") + "/Data/TestData/"
			+ "testread" + ".gfa";
//	private static String gfaErrorPath = System.getProperty("user.dir") + "/Data/TestData/"
//			+ "testreaderror" + ".gfa";

	/**
	 * Run necessary functions before running each test.
	 * @throws GfaException
	 */
	@BeforeClass
	public static void before() {
		dbm = new DatabaseManager(dbPath);
//		dbmerror = new DatabaseManager(dbErrorPath);
		parser = new GfaParser(dbm);
//		parsererror = new GfaParser(dbmerror);
	}
	
	/**
	 * Cleans up all Database files in the "db" directory.
	 */
	@AfterClass
	public static void after() {
//		dbmerror.clearDatabaseFiles("dberror");
		dbm.clearDatabaseFiles();
	}
	
//	/**
//	 * GfaException is thrown when the .gfa file does not have the correct format.
//	 * @throws GfaException
//	 */
//	@Test(expected=Exception.class)
//	public void gfaExceptionTest() throws GfaException {
//		parsererror.parse(gfaErrorPath);
//	}
	
	@Test
	public void parsedContentTest() throws GfaException {
		parser.parse(gfaPath);
		assertEquals(parser.getGenomes().size(), 4);
		assertEquals(parser.getGenomes().toString(), "{seq3.fasta=3, seq1.fasta=1, seq4.fasta=4, seq2.fasta=2}");
	}
}
