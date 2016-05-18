package toolbar;

import static org.junit.Assert.*;

import java.io.File;

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
		if(new File(directory).listFiles().length > 0) {
			for(File file: new File(directory).listFiles()) {
				file.delete();
			}
		}
	}
	
	@Test
	public void testTxtCreation() {
		RecentHandler recent = new RecentHandler();
		recent.buildRecent("", "");
		File file = new File(directory + "recent.txt");
		assertEquals(true, file.exists());
		cleanUp();
	}
}
