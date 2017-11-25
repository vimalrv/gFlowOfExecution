package com.orchestrator.handlers;

import java.io.FileReader;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CsvHandler {

		public static Iterable<CSVRecord> readFromCSV(String fileNameWithPath) throws Exception {

			Reader in = new FileReader(fileNameWithPath);

			Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);

			return records;
		}
				
		
		//public boolean writeOnOffLevelSheet(TreeMap<String, ProjectMetricsVO> treeMapMetrics, String fileName) throws Exception {
			// CSVRecord row = new CSVRecord(null, null, null, 0, 0);
			//FileWriter writer = new FileWriter(fileName);
			//CSVPrinter printer = CSVFormat.EXCEL.withHeader(headerArr).print(writer);

//			int rowCount = 0;
//			// printer.printRecords(treeMapMetrics.values());
//			for (String key : treeMapMetrics.keySet()) {
//				rowCount++;
//				ProjectMetricsVO metricsVO = treeMapMetrics.get(key);
//				writeEachRow(printer, metricsVO, rowCount);
//				printer.println();
//			}
//			printer.flush();
//			printer.close();

//			return true;
//		}

	}
