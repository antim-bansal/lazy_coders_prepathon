package prep_A.thon.utils;

import prep_A.thon.model.company;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException; // Import for CSV validation exception
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CSVReaderUtil {

    // Method to parse a single line from the CSV into a 'company' object
    private company parseCSVLine(String[] csvLine) {
        company company = new company();

        // Assuming the columns are in the order matching the model
        company.setSlNo(Integer.parseInt(csvLine[0]));
        company.setCompanyName(csvLine[1]);
        company.setCountry(csvLine[2]);
        company.setCountryCode(csvLine[3]);
        company.setMarketCap(csvLine[4]);
        company.setDiversity(Double.parseDouble(csvLine[5]));

        // Create Maps for stockPrices, expenses, revenues, and marketShares
        Map<String, String> stockPrices = new HashMap<>();
        Map<String, String> expenses = new HashMap<>();
        Map<String, String> revenues = new HashMap<>();
        Map<String, Double> marketShares = new HashMap<>();

        // Start indexes for stock prices, expenses, revenues, and market shares
        int stockPricesStartIndex = 6;  // Stock Prices from 2015 to 2024
        int expensesStartIndex = stockPricesStartIndex + 10;    // Expenses 2015 to 2024
        int revenuesStartIndex = expensesStartIndex + 10;       // Revenues 2015 to 2024
        int marketSharesStartIndex = revenuesStartIndex + 10;   // Market Shares 2015 to 2024

        // Populate the Maps for each year from 2015 to 2024
        for (int year = 2015; year <= 2024; year++) {
            String yearKey = String.valueOf(year);

            // Stock Prices
            stockPrices.put(yearKey, csvLine[stockPricesStartIndex++]);

            // Expenses
            expenses.put(yearKey, csvLine[expensesStartIndex++]);

            // Revenues
            revenues.put(yearKey, csvLine[revenuesStartIndex++]);

            // Market Shares
            marketShares.put(yearKey, Double.parseDouble(csvLine[marketSharesStartIndex++]));
        }

        // Set the Maps to the company object
        company.setStockPrices(stockPrices);
        company.setExpenses(expenses);
        company.setRevenues(revenues);
        company.setMarketShares(marketShares);

        return company;
    }

    // Method to read the entire CSV and return a list of 'company' objects from a file path
    public List<company> readCSV(String filePath) throws IOException, CsvValidationException { 
        List<company> companies = new ArrayList<>();

        // Try reading the CSV file from a file path
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            reader.readNext(); // Skip the header line
            while ((nextLine = reader.readNext()) != null) {
                companies.add(parseCSVLine(nextLine));
            }
        }

        return companies;
    }

    // Overloaded method to read the CSV from an InputStream (useful for reading files from resources)
    public List<company> readCSV(InputStream inputStream) throws IOException, CsvValidationException {
        List<company> companies = new ArrayList<>();

        // Try reading the CSV file from an InputStream
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] nextLine;
            reader.readNext(); // Skip the header line
            while ((nextLine = reader.readNext()) != null) {
                companies.add(parseCSVLine(nextLine));
            }
        }

        return companies;
    }
}
