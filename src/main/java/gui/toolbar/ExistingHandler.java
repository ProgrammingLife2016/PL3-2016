package gui.toolbar;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

public class ExistingHandler {
	private final String dbPath = System.getProperty("user.dir") + File.separator + "db" + File.separator;
	
	public HashMap<String, String> buildExistingMap() {
		File[] files = makeFileArray();
		HashMap<String, String> existingMap = new HashMap<String, String>();
		for (File file : files) {
			if (file.isFile()) {
	    	String temp = FilenameUtils.removeExtension(file.getName());
	    	existingMap.put(FilenameUtils.removeExtension(temp), dbPath + FilenameUtils.removeExtension(temp));
			}
		}
		return existingMap;
	}
	
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
