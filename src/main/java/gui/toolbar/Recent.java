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



public class Recent {
	
	final int max_recent = 3;

	public void buildRecent(String dbPath, String fileName) {
		System.out.println("called");
		File file = new File(System.getProperty("user.dir") + "/recent/recent.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
				}
			 catch (IOException e) {
				e.printStackTrace();
			}
		}
			 writeRecent(file, dbPath, fileName);

	}
	
	
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
		    		if(!line.contains(dbPath)) {
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
	
	public LinkedHashMap<String, String> readRecent() {
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
