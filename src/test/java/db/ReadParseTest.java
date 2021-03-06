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
public class ReadParseTest {

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
		dbm.cleanDbDirectory();
		parser.parse(gfaPath);
		dbm.getDbProcessor().calculateLinkCounts();
		dbm.getDbProcessor().updateCoordinates();
	}
	
	/**
	 * Close database connection, or risk system crashes.
	 */
	@AfterClass
	public static void cleanup() {
		dbm.closeDbConnection();
	}
	
	/**
	 * Test for counting all genomes through all segments.
	 */
	@Test
	public void countAllGenomesInSegTest() {
		ArrayList<Integer> genomecount = dbm.getDbReader().countAllGenomesInSeg();
		assertEquals(4, (int) genomecount.get(0));
		assertEquals(2, (int) genomecount.get(2));
		assertEquals(1, (int) genomecount.get(3));
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test for counting the all genomes.
	 */
	@Test public void countGenomesTest() {
		int genomecount = dbm.getDbReader().countGenomes();
		assertEquals(4, genomecount);
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test for counting all segments.
	 */
	@Test public void countSegmentsTest() {
		int segmentcount = dbm.getDbReader().countSegments();
		assertEquals(9, segmentcount);
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test for determining first segment of a genome.
	 */
	@Test
	public void getFirstOfAllGenomesTest() {
		ArrayList<Integer> segmentlist = dbm.getDbReader().getFirstOfAllGenomes();
		assertEquals(1, (int)segmentlist.get(0));
		assertEquals(1, (int)segmentlist.get(1));
		assertEquals(1, (int)segmentlist.get(2));
		assertEquals(1, (int)segmentlist.get(3));
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test for method counting all genomes going through a certain link.
	 */
	@Test
	public void getAllXCoordTest() {
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllXCoord();
		assertEquals(0, (int)xcoords.get(0), 0);
		assertEquals(100, (int)xcoords.get(1));
		assertEquals(100, (int)xcoords.get(2));
		assertEquals(200, (int)xcoords.get(3));
		assertEquals(200, (int)xcoords.get(4));
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test for method counting all genomes going through a certain link.
	 */
	@Test
	public void getAllYCoordTest() {
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllYCoord();
		assertEquals(200, (int)xcoords.get(0));
		assertEquals(100, (int)xcoords.get(1));
		assertEquals(300, (int)xcoords.get(2));
		assertEquals(50, (int)xcoords.get(3));
		assertEquals(150, (int)xcoords.get(4));
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test for method returning a list with all outgoing segments from a certain segment.
	 */
	@Test
	public void getToIDsTest() {
		ArrayList<Integer> toids = dbm.getDbReader().getToIDs(1);
		int[] expectedids = {2, 2};
		for (int i = 0; i < 2; i++) {
			assertEquals(expectedids[i], (int)toids.get(i));
		}
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test for method reading the content of a segment.
	 */
	@Test
	public void getContentTest() {
		String[] expectedcontent = { "empty", "TC", "AGATCAAT", "CTTCGTGA", 
				"AC", "TG", "GT", "T", "A", "AG" };
		for (int i = 1; i <= 9; i++) {
			assertEquals(expectedcontent[i], dbm.getDbReader().getContent(i));
		}
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test if GfaParser correctly read the required content.
	 * @throws GfaException
	 */
	@Test
	public void gfaParsedContentTest() throws GfaException {
		assertEquals(4, parser.getGenomes().size());
		assertEquals("{seq3.fasta=3, seq1.fasta=1, seq4.fasta=4, seq2.fasta=2}",
				parser.getGenomes().toString());
		dbm.cleanDbDirectory();
	}
	
	@AfterClass
	public static void after() throws GfaException {
		dbm.closeDbConnection();
		dbm.cleanDbDirectory();
	}
}