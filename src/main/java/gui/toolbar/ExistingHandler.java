package gui.toolbar;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

/**
 * Class to handle the existing database files
 * @author Björn Ho
 */
public class ExistingHandler {
	private final String dbPath = System.getProperty("user.dir") 
			+ File.separator + "db" + File.separator;
	
	/**
	 * This is used to build an existing database map.
	 * Remove extension is used to for compatibility reasons with
	 * the database. The key is the file name and the value is the directory.
	 * @return		returns the hashmap
	 */
	public HashMap<String, String> buildExistingMap() {
		File[] files = makeFileArray();
		HashMap<String, String> existingMap = new HashMap<String, String>();
		for (File file : files) {
			if (file.isFile()) {
	    	String temp = FilenameUtils.removeExtension(file.getName());
	    	existingMap.put(FilenameUtils.removeExtension(temp), 
	    			dbPath + FilenameUtils.removeExtension(temp));
			}
		}
		return existingMap;
	}
	
	/**
	 * It makes an array of files in the database directory
	 * which end with .mv.db as file extension.
	 * @return		Array of file type.
	 */
	public File[] makeFileArray() {
		File[] files = new File(dbPath).listFiles(new FilenameFilter() { 
				@Override 
				public boolean accept(File directory, String name) { 
					return name.endsWith(".mv.db"); 
				} 
		});
		return files;
	}
}
