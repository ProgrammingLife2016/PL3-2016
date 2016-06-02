package parsers;

import gui.GraphSegment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class XlsxParser {
	
	/**
	 * Iterations required to reach the lineage cell.
	 */
	private static final int LINEAGE = 22;
	
	/**
	 * Hashmap to store the specimen ID's with their corresponding lineage.
	 */
	private HashMap<String, String> lineages = new HashMap<String, String>();

	/**
	 * Parser the input xlsx file.
	 * @param xlsxpath
	 */
	public void parse(String xlsxpath) {
		try {
			FileInputStream file = new FileInputStream(new File(xlsxpath));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0); //Get first/desired sheet from the workbook
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) { //For each row

				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				Cell cell = cellIterator.next();
				
				if (isValidRow(cell)) {
					String specimenid = cell.getStringCellValue();
					for (int i = 0; i < LINEAGE; i++) {
						cellIterator.next();
					}
					cell = cellIterator.next();
					String specimenlineage = cell.getStringCellValue();
					lineages.put(specimenid, specimenlineage);
				}
			}
			file.close();
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + xlsxpath);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading file: " + xlsxpath);
			e.printStackTrace();
		}
	}
	
	private boolean isValidRow(Cell cell) {
		boolean valid = false;
		
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			String specimenid = cell.getStringCellValue();
			String specimentype = specimenid.substring(0, 3);
			if (specimentype.equals("TKK")) {
				valid = true;
			}
			break;
		default:
			break;
		}
		return valid;
	}
	
	public HashMap<String, String> getLineages() { 
		return this.lineages;
	}
}


//public static void parse() {
//	try {
//		FileInputStream file = new FileInputStream(new File(xlsxpath));
//		XSSFWorkbook workbook = new XSSFWorkbook(file);
//		XSSFSheet sheet = workbook.getSheetAt(0); //Get first/desired sheet from the workbook
//		Iterator<Row> rowIterator = sheet.iterator();
//		while (rowIterator.hasNext()) { //For each row
//			Row row = rowIterator.next();
//			Iterator<Cell> cellIterator = row.cellIterator();
//
//			while (cellIterator.hasNext()) { //For each element (column) in a row.
//				Cell cell = cellIterator.next();
//				switch (cell.getCellType()) {
//				case Cell.CELL_TYPE_NUMERIC:
//					System.out.print(cell.getNumericCellValue() + " --- ");
//					break;
//				case Cell.CELL_TYPE_STRING:
//					System.out.print(cell.getStringCellValue() + " --- ");
//					break;
//				default:
//				break;
//				}
//			}
//			System.out.println("");
//		}
//		file.close();
//	} catch (FileNotFoundException e) {
//		System.err.println("File not found: " + xlsxpath);
//		e.printStackTrace();
//	} catch (IOException e) {
//		System.err.println("Error reading file: " + xlsxpath);
//		e.printStackTrace();
//	}
//}
