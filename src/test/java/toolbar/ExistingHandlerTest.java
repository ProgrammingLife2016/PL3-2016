package toolbar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to check if ExistingHandler is working as intended
 * @author Björn Ho
 */
public class ExistingHandlerTest {
	static String directory = System.getProperty("user.dir") 
			+ File.separator + "db" + File.separator;
	
	@BeforeClass
	public static void cleanUp() {
		File[] fileList = new File(directory).listFiles();
		if(fileList != null) {
			for (File file: fileList) {
				if (file != null) {
					if (!file.delete())
						System.err.println("File was not deleted!");
				}
			}
		}
	}
	
	@Test
	public void testMakeFileArray() throws IOException {
		File file = new File(directory + "test.mv.db");
		if (!file.createNewFile())
			System.err.println("File was not created!");
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals(directory + "test.mv.db", exHandler.makeFileArray()[0].toString());
		if (!file.delete())
			System.err.println("File was not deleted!");
	}
	
	@Test
	public void testEmptyFileArray() throws IOException {
		File file = new File(directory + "invalidType.txt");
		if (!file.createNewFile())
			System.err.println("File was not created!");
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals(0, exHandler.makeFileArray().length);
		if (!file.delete())
			System.err.println("File was not deleted!");
	}
	
	@Test
	public void testCorrectValue() throws IOException {
		File file1 = new File(directory + "TB500.mv.db");
		if (!file1.createNewFile())
			System.err.println("File was not created!");
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals(directory + "TB500", 
				exHandler.buildExistingMap().get("TB500"));
		if (!file1.delete())
			System.err.println("File was not deleted!");
	}
	
	@Test
	public void testCorrectKey() throws IOException {
		File file1 = new File(directory + "TB999.mv.db");
		if (!file1.createNewFile())
			System.err.println("File was not created!");
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals("TB999",
				exHandler.buildExistingMap().keySet().iterator().next());
		if (!file1.delete())
			System.err.println("File was not deleted!");
	}
	
	@Test
	public void testEmpty() {
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals(0, exHandler.buildExistingMap().size());
	}
}
