package main;

public class Main {
	

	
	public static void main(String[] args) {
		
		String filename = "example";
		String gfaDir = System.getProperty("user.dir") + "/Data/";
		String dbDir = System.getProperty("user.dir") + "/db/";
		
		
		DatabaseCreator dbCreator = new DatabaseCreator();
		try {
			dbCreator.parse(filename,gfaDir,dbDir);
		} catch (GfaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
