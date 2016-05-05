package db;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author hugokooijman
 *
 * Tests for DatabaseReader methods, to see if given queries provide the correct information.
 */
public class DatabaseReaderTest {

	private static DatabaseManager dbm;
	private static GfaParser parser;
	
	/**
	 * Locations of test files.
	 */
	private static String filename = "testread";
	private static String gfaPath = System.getProperty("user.dir") + "/Data/TestData/" + filename
			+ ".gfa";
	private static String dbPath = System.getProperty("user.dir") + "/db/" + filename;
	
	
	/**
	 * Run necessary functions once before running all tests.
	 * @throws GfaException
	 */
	@BeforeClass
	public static void before() throws GfaException {
		dbm = new DatabaseManager(dbPath);
		parser = new GfaParser(dbm);
		parser.parse(gfaPath);
	}
	
	/**
	 * Test for method returning a list with each starting segment of a certain link.
	 */
	@Test
	public void fromAllIdsTest() {
		ArrayList<Integer> fromallids = dbm.getDbReader().getAllFromId();
		int[] expectedids = {1, 1, 2, 2, 3, 3, 4, 5, 6, 7, 8};
		for (int i = 0; i < 11; i++) {
			assertEquals((int)fromallids.get(i), expectedids[i]);
		}
	}
	
	/**
	 * Test for method returning a list with each ending segment of a certain link.
	 */
	@Test public void toAllIdsTest() {
		ArrayList<Integer> toallids = dbm.getDbReader().getAllToId();
		int[] expectedids = {2, 3, 4, 5, 6, 8, 7, 7, 8, 9, 9};
		for (int i = 0; i < 2; i++) {
			assertEquals((int)toallids.get(i), expectedids[i]);
		}
	}
	
	/**
	 * Test for method returning a list with all incoming segments for a certain segment.
	 */
	@Test
	public void fromIdsTest() {
		ArrayList<Integer> fromids = dbm.getDbReader().getFromIDs(7);
		int[] expectedids = {4, 5};
		for (int i = 0; i < 2; i++) {
			assertEquals((int)fromids.get(i), expectedids[i]);
		}
	}
	
	/**
	 * Test for method returning a list with all outgoing segments for a certain segment.
	 */
	@Test
	public void toIdsTest() {
		ArrayList<Integer> toids = dbm.getDbReader().getToIDs(1);
		int[] expectedids = {2, 3};
		for (int i = 0; i < 2; i++) {
			assertEquals((int)toids.get(i), expectedids[i]);
		}
	}
	
	/**
	 * Test for method counting all genomes going through a certain link.
	 */
	@Test
	public void countGenomesInLinkTest() {
		assertEquals(dbm.getDbReader().countGenomesInLink(1,2), 2);
		assertEquals(dbm.getDbReader().countGenomesInLink(2,4), 1);
		assertEquals(dbm.getDbReader().countGenomesInLink(8,9), 2);
	}
	
	/**
	 * Test for method counting all genomes going through a certain segment.
	 */
	@Test
	public void countGenomesInSegTest() {
		assertEquals(dbm.getDbReader().countGenomesInSeg(1), 4);
		assertEquals(dbm.getDbReader().countGenomesInSeg(2), 2);
		assertEquals(dbm.getDbReader().countGenomesInSeg(8), 2);
	}
	
	/**
	 * Test for method reading the content of a segment.
	 */
	@Test
	public void getContentTest() {
		String[] expectedcontent = {"empty", "TC", "AGATCAAT", "CTTCGTGA", "AC", "TG", "GT", "T", "A", "AG"};
		for (int i = 1; i <= 9; i++) {
			assertEquals(dbm.getDbReader().getContent(i), expectedcontent[i]);
		}
	}
}
