package db;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import parsers.NewickTreeParser;

/**
 * @author hugokooijman
 * 
 * Test if NewickTreeParser correctly reads content from file.
 */
public class NewickTreeParserTest {
	
	/**
	 * Location of test file.
	 */
	private static String nwkPath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TestData" + File.separator + "testtree" + ".rooted.TKK.nwk";
	
	@Test
	public void parseTest() throws IOException {
		String content = NewickTreeParser.parse(new File(nwkPath)).toString();
		assertEquals("(A:10.0,(B:3.0,C:9.0))", content);
	}
}
