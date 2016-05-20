package db;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import parsers.GfaException;
import parsers.GfaParser;

/**
 * @author hugokooijman
 *
 * Tests for DatabaseReader methods, to see if given queries provide the correct information.
 */
public class ParserAndReaderTest {

	private static DatabaseManager dbm;
	private static GfaParser parser;
	
	/**
	 * Locations of test files.
	 */
	private static String filename = "testread";
	private static String gfaPath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TestData" + File.separator + filename + ".gfa";
	private static String dbPath = System.getProperty("user.dir") + File.separator + "db"
			+ File.separator + filename;
	
	
	/**
	 * Run necessary functions once before running all tests.
	 * @throws GfaException
	 */
	@BeforeClass
	public static void before() throws GfaException {
		dbm = new DatabaseManager(dbPath);
		parser = new GfaParser(dbm);
		parser.parse(gfaPath);
		dbm.getDbProcessor().calculateLinkCounts();

	}
	
	@AfterClass
	public static void after() {
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test for method returning a list with each starting segment of a certain link.
	 */
	@Test
	public void fromAllIdsTest() {
		ArrayList<Integer> fromallids = dbm.getDbReader().getAllFromId();
		int[] expectedids = {1, 1, 2, 2, 3, 3, 4, 5, 6, 7, 8};
		for (int i = 0; i < 11; i++) {
			assertEquals(expectedids[i], (int)fromallids.get(i));
		}
	}
	
	/**
	 * Test for method returning a list with each ending segment of a certain link.
	 */
	@Test public void toAllIdsTest() {
		ArrayList<Integer> toallids = dbm.getDbReader().getAllToId();
		int[] expectedids = {2, 3, 4, 5, 6, 8, 7, 7, 8, 9, 9};
		for (int i = 0; i < 2; i++) {
			assertEquals(expectedids[i], (int)toallids.get(i));
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
			assertEquals(expectedids[i], (int)fromids.get(i));
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
			assertEquals(expectedids[i], (int)toids.get(i));
		}
	}
	
	/**
	 * Test for method counting all genomes going through a certain link.
	 */
	@Test
	public void countGenomesInLinkTest() {
		assertEquals(2, dbm.getDbReader().countGenomesInLink(1,2));
		assertEquals(1, dbm.getDbReader().countGenomesInLink(2,4));
		assertEquals(2, dbm.getDbReader().countGenomesInLink(8,9));
	}
	
	/**
	 * Test for method counting all genomes going through a certain segment.
	 */
	@Test
	public void countGenomesInSegTest() {
		assertEquals(4, dbm.getDbReader().countGenomesInSeg(1));
		assertEquals(2, dbm.getDbReader().countGenomesInSeg(2));
		assertEquals(2, dbm.getDbReader().countGenomesInSeg(8));
	}
	
	/**
	 * Test for method reading the content of a segment.
	 */
	@Test
	public void getContentTest() {
		String[] expectedcontent = {"empty", "TC", "AGATCAAT", "CTTCGTGA", "AC", "TG", "GT", "T", "A", "AG"};
		for (int i = 1; i <= 9; i++) {
			assertEquals(expectedcontent[i], dbm.getDbReader().getContent(i));
		}
	}
	
	@Test
	public void GfaParsedContentTest() throws GfaException {
		assertEquals(parser.getGenomes().size(), 4);
		assertEquals(parser.getGenomes().toString(),
				"{seq3.fasta=3, seq1.fasta=1, seq4.fasta=4, seq2.fasta=2}");
	}
}