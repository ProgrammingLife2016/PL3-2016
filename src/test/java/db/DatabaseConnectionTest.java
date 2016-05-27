package db;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * @author hugokooijman
 *
 * Test database connection.
 */
public class DatabaseConnectionTest {

	private static DatabaseManager dbm;
	
	/**
	 * Location of test file.
	 */
	private String dbPath = System.getProperty("user.dir") + File.separator + "db"
			+ File.separator + "testreaderror";
	
	/**
	 * Set up required objects.
	 */
	@Before
	public void before() {
		dbm = new DatabaseManager(dbPath);
	}
	
	/**
	 * Test if database connection closes properly.
	 */
	@Test
	public void closeConnectionTest() {
		assertTrue(dbm.closeDbConnection());
	}
}
