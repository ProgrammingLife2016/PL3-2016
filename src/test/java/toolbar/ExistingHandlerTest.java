package toolbar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to check if ExistingHandler is working as intended
 * @author Bjorn
 */
public class ExistingHandlerTest {
	static String directory= System.getProperty("user.dir") 
			+ File.separator + "db" + File.separator;
	
	@BeforeClass
	public static void cleanUp() {
		for(File file: new File(directory).listFiles()) {
			file.delete();
		}
	}
	
	@Test
	public void testMakeFileArray() throws IOException {
		File file= new File(directory + "test.mv.db");
		file.createNewFile();
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals(directory + "test.mv.db", exHandler.makeFileArray()[0].toString());
		file.delete();
	}
	
	@Test
	public void testEmptyFileArray() throws IOException {
		File file = new File(directory + "invalidType.txt");
		file.createNewFile();
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals(0, exHandler.makeFileArray().length);
		file.delete();
	}
	
	@Test
	public void testCorrectValue() throws IOException {
		File file1 = new File(directory + "TB500.mv.db");
		file1.createNewFile();
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals(directory + "TB500", 
				exHandler.buildExistingMap().get("TB500"));
		file1.delete();
	}
	
	@Test
	public void testCorrectKey() throws IOException {
		File file1 = new File(directory + "TB999.mv.db");
		file1.createNewFile();
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals("TB999",
				exHandler.buildExistingMap().keySet().iterator().next());
		file1.delete();
	}
	
	@Test
	public void testEmpty() {
		ExistingHandler exHandler = new ExistingHandler();
		assertEquals(0, exHandler.buildExistingMap().size());
	}
}
