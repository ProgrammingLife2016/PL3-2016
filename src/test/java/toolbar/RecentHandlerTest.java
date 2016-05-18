package toolbar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to check if RecentHandler is working as intended
 * @author Bjorn
 */
public class RecentHandlerTest {
	static String directory= System.getProperty("user.dir") 
			+ File.separator + "recent" + File.separator;
	
	@BeforeClass
	public static void cleanUp() {
		for(File file: new File(directory).listFiles()) {
			if(!file.getName().equals(".gitignore"))
			file.delete();
		}
	}
	
	@Test
	public void testTxtCreation() throws IOException {
		RecentHandler recent = new RecentHandler();
		File file = new File(directory + "recent.txt");
		recent.buildRecent("test", "test");
		assertEquals(true, file.exists());
		cleanUp();
	}
}
