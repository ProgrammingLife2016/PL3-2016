package gui.toolbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;



public class OpenRecent {
	
	public void writeRecent(String gfaPath, String fileName) {
		File file = new File(System.getProperty("user.dir") + "/recentgfa/recent.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
				}
			 catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		File gfaFile = new File(gfaPath);
		if(!containsRecent(file, gfaFile.getAbsolutePath())) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
			writer.append(fileName);
			writer.append(" ");
			writer.append(gfaFile.getAbsolutePath());
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		}
	}
	
	public boolean containsRecent(File file, String path) {
			try {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
			        String line = sc.nextLine();
			        if(line.contains(path)) { 
			            sc.close();
			            return true;
			        }
			  }
			  sc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			  return false;		  
	}
	
	public LinkedHashMap<String, String> readRecent() {
		LinkedHashMap<String, String> recentMap = new LinkedHashMap<String, String>();
		File file = new File(System.getProperty("user.dir") + "/recentgfa/recent.txt");
		try {
			Scanner sc = new Scanner(file);
			while(sc.hasNext()) {
				recentMap.put(sc.next(), sc.next());
			}
			sc.close();
			return recentMap;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return recentMap;
	}
}
