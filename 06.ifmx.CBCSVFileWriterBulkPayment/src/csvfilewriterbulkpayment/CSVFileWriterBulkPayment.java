/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csvfilewriterbulkpayment;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.xml.sax.InputSource;
/**
 *
 * @author Haikal
 */
public class CSVFileWriterBulkPayment {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if (args.length < 2) {
            System.err.println("Usage: java CSVFileWriter <csv_content> <output_file>");
            return;
        }

        String csvContent = args[0];
        String outputFile = args[1];
        saveToFile(csvContent, outputFile);
    }
    
    public static void saveToFile(String xmlContent, String filePath) {
        try {
            // Parse XML from string
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlContent));
            Document doc = builder.parse(inputSource);

            doc.getDocumentElement().normalize();

            Element bulkPayment = (Element) doc.getElementsByTagName("BulkPayment").item(0);
            String filenameAttr = bulkPayment.getAttribute("filename");

            // Convert filename to .csv
            String csvFilename = filenameAttr.replace(".xml", ".csv");
            Path csvOutputPath = Paths.get(filePath, csvFilename);

            // Extract attributes
            String[] headers = {
                "detectionStatus", "filename", "transactionKey", "batchId",
                "logicalInputFileCreationDateTime", "logicalFileSequenceId",
                "validEntriesCount", "invalidEntriesCount",
                "numberOfSuccessfullyDetectedEntries", "numberOfFailedDetectedEntries"
            };

            List<String> values = new ArrayList<>();
            for (String header : headers) {
                values.add(bulkPayment.getAttribute(header));
            }

            // Write CSV
            try (FileWriter writer = new FileWriter(csvOutputPath.toFile())) {
                writer.write(String.join(",", headers) + "\n");
                writer.write(String.join(",", values) + "\n");
            }

            System.out.println("CSV created: " + csvOutputPath);

        } catch (Exception e) {
            System.err.println("Error converting XML to CSV: " + e.getMessage());
        }
    }
    
}
