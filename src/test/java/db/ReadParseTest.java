package db;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;

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
	 * Test for counting all genomes in 1 segment.
	 */
	@Test
	public void countGenomesInSegTest() {
		assertEquals(4, dbm.getDbReader().countGenomesInSeg(1));
		assertEquals(2, dbm.getDbReader().countGenomesInSeg(2));
		assertEquals(2, dbm.getDbReader().countGenomesInSeg(8));
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
	 * Test for counting genomes in a link.
	 */
	@Test
	public void countGenomesInLink() {
		int genomelinkcount = dbm.getDbReader().countGenomesInLink(1, 2);
		assertEquals(2, genomelinkcount);
		genomelinkcount = dbm.getDbReader().countGenomesInLink(1, 3);
		assertEquals(2, genomelinkcount);
		genomelinkcount = dbm.getDbReader().countGenomesInLink(5, 7);
		assertEquals(1, genomelinkcount);
		dbm.cleanDbDirectory();
	}
	
	/**
	 * Test for counting links in a segment.
	 */
	@Test
	public void getLinkCountTest() {
		int linkcount = dbm.getDbReader().getLinkcount(1, 2);
		assertEquals(2, linkcount);
		linkcount = dbm.getDbReader().getLinkcount(1, 3);
		assertEquals(2, linkcount);
		linkcount = dbm.getDbReader().getLinkcount(5, 7);
		assertEquals(1, linkcount);
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
	 * Test for method returning a list with each ending segment of a certain link.
	 */
	@Test public void getAllToIdTest() {
		ArrayList<Integer> toallids = dbm.getDbReader().getAllToId();
		int[] expectedids = {2, 3, 4, 5, 6, 8, 7, 7, 8, 9, 9};
		for (int i = 0; i < 2; i++) {
			assertEquals(expectedids[i], (int)toallids.get(i));
		}
	}
	
	/**
	 * Test for getting all counts of links through all genomes.
	 */
	@Test
	public void getAllCounts() {
		ArrayList<Integer> allcounts = dbm.getDbReader().getAllCounts();
		assertEquals(2, (int)allcounts.get(0));
		assertEquals(2, (int)allcounts.get(1));
		assertEquals(1, (int)allcounts.get(2));
		assertEquals(1, (int)allcounts.get(3));
	}
	
	/**
	 * Test for method returning a list with all incoming segments for a certain segment.
	 */
	@Test
	public void getFromIDsTest() {
		ArrayList<Integer> fromids = dbm.getDbReader().getFromIDs(7);
		int[] expectedids = {4, 5};
		for (int i = 0; i < 2; i++) {
			assertEquals(expectedids[i], (int)fromids.get(i));
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
	
	@Test
	public void gfaParsedContentTest() throws GfaException {
		assertEquals(4, parser.getGenomes().size());
		assertEquals("{seq3.fasta=3, seq1.fasta=1, seq4.fasta=4, seq2.fasta=2}",
				parser.getGenomes().toString());
		dbm.cleanDbDirectory();
	}
}