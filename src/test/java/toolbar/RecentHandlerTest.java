package toolbar;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to check if RecentHandler is working as intended
 * @author Björn Ho
 */
public class RecentHandlerTest {
	static String directory = System.getProperty("user.dir") 
			+ File.separator + "recent" + File.separator;
	
	static String dbDir = System.getProperty("user.dir") 
			+ File.separator + "db" + File.separator;
	
	
	@BeforeClass
	public static void cleanUp() {
		for (File file: new File(directory).listFiles()) {
			if (!file.getName().equals(".gitignore")) {
				if (!file.delete())
					System.err.println("No file was deleted");
			}
		}
		
		for (File file: new File(dbDir).listFiles()) {
			if (!file.delete())
				System.err.println("No file was deleted");
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
		if (!file.createNewFile())
			System.err.println("File was not created!");
		assertTrue(recent.fileExists("TB500" + " " + directory + "TB500"));
		cleanUp();
	}
	
	@Test
	public void testGetRecent() throws IOException {
		File file = new File(directory + "recent.txt");
		if (!file.createNewFile())
			System.err.println("File was not created!");
		BufferedWriter bw = new BufferedWriter(
				new FileWriter(directory + "recent.txt"));
		bw.write("TBTest" + " " + "PathToFile");
		bw.close();
		RecentHandler recent = new RecentHandler();
		assertEquals("PathToFile", 
				recent.getRecent().get("TBTest"));
		cleanUp();
	}
	
	@Test
	public void testWriteRecent() throws IOException {
		File file = new File(directory + "recent.txt");
		if (!file.createNewFile())
			System.err.println("File was not created!");
		File file2 = new File(dbDir + "TB100.mv.db");
		String temp = FilenameUtils.removeExtension(file2.getName());
		String name = FilenameUtils.removeExtension(temp);
		if (!file2.createNewFile())
			System.err.println("File was not created!");
		RecentHandler recent = new RecentHandler();
		recent.writeRecent(file, dbDir + name, name);
		assertEquals(dbDir + name, recent.getRecent().get(name));
		cleanUp();
	}
}
