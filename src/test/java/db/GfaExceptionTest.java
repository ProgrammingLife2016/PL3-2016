package db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import parsers.GfaException;
import parsers.GfaParser;

/**
 * @author hugokooijman
 *
 * Test if GfaException is triggered correctly.
 */
public class GfaExceptionTest {
	
	private DatabaseManager dbm;
	private GfaParser parser;
	
	/**
	 * Locations of test files.
	 */
	private String filename = "testreaderror";
	private String gfaPath = System.getProperty("user.dir") + "/Data/TestData/"
			+ "testreaderror" + ".gfa";
	private String dbPath = System.getProperty("user.dir") + "/db/" + filename;
	
	/**
	 * Set up required objects.
	 */
	@Before
	public void before() {
		dbm = new DatabaseManager(dbPath);
		parser = new GfaParser(dbm);
	}
	
	/**
	 * GfaException is thrown when the .gfa file does not have the correct format.
	 * @throws GfaException
	 */
	@Test(expected=GfaException.class)
	public void gfaExceptionTest() throws GfaException {
		parser.parse(gfaPath);
	}
	
	/**
	 * Close database connection, or risk system crashes.
	 */
	@After
	public void after() {
		dbm.closeDbConnection();
	}
}
