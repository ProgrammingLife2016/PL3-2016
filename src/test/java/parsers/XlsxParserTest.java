package parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * @author hugokooijman
 * 
 * Test if XlsxxxParser correctly reads the content from a file.
 */
public class XlsxParserTest {

	/**
	 * Location of xlsx test file.
	 */
	private static String xlsxpath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TB10" + File.separator + "metadata" + ".xlsx";

	/**
	 * Correct size of the Lineages hashmap.
	 */
	private static final int LINEAGES_SIZE = 337;
	/**
	 * Tests if parse function works as intended.
	 * 
	 * @throws IOException
	 */
	@Test
	public void parseTest() {
		XlsxParser xlsxparser = new XlsxParser();
		xlsxparser.parse(xlsxpath);
		assertEquals(xlsxparser.getLineages().get("TKK-01-0001"), "LIN 4");
		assertEquals(xlsxparser.getLineages().get("TKK_03_0040"), "LIN 2");
		assertEquals(xlsxparser.getLineages().get("TKK_04_0103"), "LIN 4");
		assertEquals(xlsxparser.getLineages().get("TKK_05SA_0018"), "LIN 4");
	}
	
	@Test
	public void lineagesSizeTest() {
		XlsxParser xlsxparser = new XlsxParser();
		xlsxparser.parse(xlsxpath);
		assertTrue(xlsxparser.getLineages().size() == LINEAGES_SIZE);
	}
}
