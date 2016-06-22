package parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
	 * Hashmap to store the metadata of each specimen.
	 */
	private HashMap<String, ArrayList<String>> metadata = new HashMap<String, ArrayList<String>>();
	
	private String[] columnnames = {
			"Specimen ID: ", "Age: ", "Sex: ", "HIV Status: ", "Cohort: ", "Date of Collection: ", "Study Geographic District: ",
			"Specimen Type: ", "Microscopy Smear Status: ", "IGNORE", "DNA isolation: ",
			"Phenotypic DST Pattern: ", "Capreomycin (10ug/mL): ", "Ethambutol (7.5ug/mL): ", "Ethionamide (10ug/mL): ", "Isoniazid (0.2ug/mL or 1ug/mL): ",
			"Kanamycin (6ug/mL): ", "IGNORE", "Pyrazinamide (Nicotinamide 500.0ug/mL or PZA-MGIT): ", "Ofloxacin (2ug/mL): ",
			"Rifampin (1ug/mL): ", "Streptomycin (2ug/mL): ", "Digital Spoligotype: ", "Lineage: ", "Genotypic DST pattern: ", "IGNORE",
			"Tugela Ferry vs. non-Tugela Ferry XDR: "
	};

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
					final String specimenid = cell.getStringCellValue();
					for (int i = 0; i < LINEAGE; i++) {
						cellIterator.next();
					}
					cell = cellIterator.next();
					String specimenlineage = cell.getStringCellValue();
					specimenlineage = specimenlineage.replace(" ", "");
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
	
	/**
	 * Parser the input xlsx file.
	 * @param xlsxpath
	 */
	public void parseMetaData(String xlsxpath) {
		try {
			FileInputStream file = new FileInputStream(new File(xlsxpath));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0); //Get first/desired sheet from the workbook
			Iterator<Row> rowIterator = sheet.iterator();
			
			while (rowIterator.hasNext()) { //For each row
				ArrayList<String> metadatainfo = new ArrayList<String>();
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				
				Iterator<Cell> cellIterator2 = row.cellIterator();
				Cell cellid = cellIterator2.next();
				if (isValidRow(cellid)) {
					String id = cellid.getStringCellValue();
					
					int i = 0;
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						String metadata = getCellValue(cell);

						String columnname = columnnames[i];
						if (!columnname.equals("IGNORE")) {
							String infocombo = columnname.concat(metadata);
							metadatainfo.add(infocombo);
						}
						++i;
					}
					System.out.println(metadatainfo.toString() + "\n");
					metadata.put(id, metadatainfo);
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
	
	private String getCellValue(Cell cell) {
		String metadata = "";
		if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
			metadata = cell.getStringCellValue();

		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			int metadataint = (int) cell.getNumericCellValue();
			metadata = Integer.toString(metadataint);
		}
		return metadata;
	}
	
	public HashMap<String, String> getLineages() { 
		return this.lineages;
	}
	
	public HashMap<String, ArrayList<String>> getMetaData() {
		return this.metadata;
	}
}
