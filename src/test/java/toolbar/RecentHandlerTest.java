package toolbar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to check if RecentHandler is working as intended
 * @author Bjorn
 */
public class RecentHandlerTest {
	static String directory= System.getProperty("user.dir") 
			+ File.separator + "recent" + File.separator;
	
	static String dbDir= System.getProperty("user.dir") 
			+ File.separator + "db" + File.separator;
	
	
	@BeforeClass
	public static void cleanUp() {
		for(File file: new File(directory).listFiles()) {
			if(!file.getName().equals(".gitignore"))
			file.delete();
		}
		
		for(File file: new File(dbDir).listFiles()) {
			file.delete();
		}
	}
	
	@Test
	public void testTxtCreation() throws IOException {
		RecentHandler recent = new RecentHandler();
		File file = new File(directory + "recent.txt");
		recent.buildRecent("test", "test");
		assertTrue(file.exists());
		cleanUp();
	}
	
	@Test
	public void testFileNotExist() {
		RecentHandler recent = new RecentHandler();
		assertFalse(recent.fileExists("TB500" + " " + directory + "TB500.mv.db"));
	}
	
	@Test
	public void testFileExist() throws IOException {
		RecentHandler recent = new RecentHandler();
		File file = new File(directory + "TB500.mv.db");
		file.createNewFile();
		assertTrue(recent.fileExists("TB500" + " " + directory + "TB500"));
		cleanUp();
	}
	
	@Test
	public void testWriteRecent() throws IOException {
		File file = new File(directory + "recent.txt");
		file.createNewFile();
		File file2 = new File(dbDir + "TB100.mv.db");
		String temp = FilenameUtils.removeExtension(file2.getName());
		String name = FilenameUtils.removeExtension(temp);
		file2.createNewFile();
		RecentHandler recent = new RecentHandler();
		recent.writeRecent(file, dbDir + name, name);
		assertEquals(dbDir + name, recent.getRecent().get(name));
		cleanUp();
	}
}
