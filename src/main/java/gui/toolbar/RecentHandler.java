package gui.toolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * Class used to handle the recently opened files
 * in the toolbar.
 * @author Bjorn
 */
public class RecentHandler {
	
	/**
	 * The maximum amount of recently opened files
	 * shown when accessing the submenu.
	 */
	final int max_recent = 3;
	
	/**
	 * It builds up the most recently opened files used for the toolbar
	 * and saves it as recent.txt. If it does not exist yet, it 
	 * will make a new file.
	 * @param dbPath		path to the database.
	 * @param fileName		name of the file.
	 */
	public void buildRecent(String dbPath, String fileName) {
		File file = new File(System.getProperty("user.dir") + "/recent/recent.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			  }
		}
		writeRecent(file, dbPath, fileName);
	}
	
	/**
	 * Used to write recently used files to a .txt file. During the progress
	 * a tmp.txt is used so that duplicates or non existing files will not occur 
	 * and that the limit of recently used files holds. The recently opened file 
	 * will be on the first line of recent.txt.
	 * @param file		the file which is the recent.txt
	 * @param dbPath	the path to the recently opened database.
	 * @param fileName	the file name of the database.
	 */
	public void writeRecent(File file, String dbPath, String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getParent() + File.separator + "tmp.txt"));
		    bw.write(fileName);
		    bw.append(" ");
		    bw.append(dbPath);
		    bw.newLine();
		    String line;
		    int lineCount = 0;
		    while((line = br.readLine()) != null && lineCount < max_recent - 1) {
		    	if(!line.contains(dbPath) && fileExists(line)) {
		    	bw.append(line);
		    	bw.newLine();
		    	lineCount++;
		    	}
		    }
		    br.close();
		    bw.close();
		    File newFile = new File(file.getParent() + File.separator + "tmp.txt");
		    file.delete();
		    newFile.renameTo(file);
		} catch (IOException e) {
				e.printStackTrace();
		  }
	}
	
	/**
	 * Used to check whether a database file exists in the
	 * database directory.
	 * @param line	The database directory to the file.
	 * @return		true if it exists and else false.
	 */
	public boolean fileExists(String line) {
		String[] lineSplit= line.split(" ");
		File file = new File(lineSplit[1] + ".mv.db");
		return file.exists();
	}
	
	/**
	 * This is used to setup a linked hash map to store a name
	 * and a directory of a recently opened file. This reads from recent.txt
	 * and stores it in the hash map. A linked hashmap is used to preserve
	 * the order. (Most recently opened is first).
	 * @return	returns the linked hash map.
	 */
	public LinkedHashMap<String, String> getRecent() {
		LinkedHashMap<String, String> recentMap = new LinkedHashMap<String, String>();
		File file = new File(System.getProperty("user.dir") + "/recent/recent.txt");
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNext()) {
				recentMap.put(sc.next(), sc.next());
			}
			sc.close();
			return recentMap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		  }
		return recentMap;
	}
}