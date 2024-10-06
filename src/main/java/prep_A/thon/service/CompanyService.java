package prep_A.thon.service;

import org.springframework.stereotype.Service;
import prep_A.thon.model.company;
import prep_A.thon.utils.CSVReaderUtil;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private final CSVReaderUtil csvReaderUtil;
    private List<company> companies;

    public CompanyService(CSVReaderUtil csvReaderUtil) {
        this.csvReaderUtil = csvReaderUtil;
        try {
            // Load CSV data from resources folder
            InputStream csvInputStream = getClass().getClassLoader().getResourceAsStream("Mock Data Prepathon.csv");
            if (csvInputStream == null) {
                throw new FileNotFoundException("File not found in classpath: Mock Data Prepathon.csv");
            }

            // Read and store company data from CSV
            this.companies = csvReaderUtil.readCSV(csvInputStream);
            logger.info("CSV file loaded successfully with {} companies.", companies.size());
        } catch (IOException | CsvValidationException e) {
            logger.error("Error reading CSV file: {}", e.getMessage(), e);
            this.companies = new ArrayList<>();
        }
    }

    // Search companies by name (case-insensitive)
    public List<company> searchCompanies(String companyName) {
        logger.info("Searching for companies with name: {}", companyName);
        return companies.stream()
                .filter(company -> company.getCompanyName().toLowerCase().contains(companyName.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Count companies from the same country
    public long countCompaniesInSameCountry(company selectedCompany) {
        return companies.stream()
                .filter(company -> company.getCountry().equalsIgnoreCase(selectedCompany.getCountry()))
                .count();
    }

    // Count companies with greater diversity in the same country
    public long countCompaniesWithGreaterDiversity(company selectedCompany) {
        return companies.stream()
                .filter(company -> company.getCountry().equalsIgnoreCase(selectedCompany.getCountry()) &&
                        company.getDiversity() > selectedCompany.getDiversity())
                .count();
    }

    // Calculate year-by-year increase or decrease for a given metric
    public Map<String, String> calculateYearlyChange(Map<String, String> data) {
        return data.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String year = entry.getKey();
                            double currentValue = Double.parseDouble(entry.getValue());
                            String previousYear = String.valueOf(Integer.parseInt(year) - 1);
                            double previousValue = data.containsKey(previousYear) ? Double.parseDouble(data.get(previousYear)) : 0.0;
                            return String.format("%.2f", currentValue - previousValue);
                        }
                ));
    }

    // Count companies with greater stock price, market share, revenue, and expenses
    public Map<String, Long> countCompaniesWithGreaterMetrics(company selectedCompany) {
        return Map.of(
                "greaterStockPrice", companies.stream()
                        .filter(company -> companyHasGreaterValue(company.getStockPrices(), selectedCompany.getStockPrices()))
                        .count(),
                "greaterMarketShare", companies.stream()
                        .filter(company -> companyHasGreaterValue(company.getMarketShares(), selectedCompany.getMarketShares()))
                        .count(),
                "greaterRevenue", companies.stream()
                        .filter(company -> companyHasGreaterValue(company.getRevenues(), selectedCompany.getRevenues()))
                        .count(),
                "greaterExpense", companies.stream()
                        .filter(company -> companyHasGreaterValue(company.getExpenses(), selectedCompany.getExpenses()))
                        .count()
        );
    }

    // Helper method to check if one company has greater values than another for metrics (stock price, revenue, etc.)
    private boolean companyHasGreaterValue(Map<String, ?> company1Data, Map<String, ?> company2Data) {
        return company1Data.entrySet().stream()
            .allMatch(entry -> {
                String key = entry.getKey();
                Object value1 = entry.getValue();
                Object value2 = company2Data.get(key);
    
                if (value1 instanceof String && value2 instanceof String) {
                    return Double.parseDouble((String) value1) > Double.parseDouble((String) value2);
                } else if (value1 instanceof Double && value2 instanceof Double) {
                    return (Double) value1 > (Double) value2;
                } else {
                    return false; // Handle type mismatch or unknown types
                }
            });
    }
    

    // Comment on company's growth based on revenue and expense trends
    public String commentOnCompanyGrowth(company selectedCompany) {
        Map<String, String> revenueGrowth = calculateYearlyChange(selectedCompany.getRevenues());
        Map<String, String> expenseGrowth = calculateYearlyChange(selectedCompany.getExpenses());

        // Simplified comment based on growth (extend logic as needed)
        String growthComment = "The company has shown steady revenue growth and fluctuating expenses.";
        return growthComment;
    }

    // Predict next year's stock price, market share, revenue, and expenses
    public Map<String, Double> predictNextYearMetrics(company selectedCompany) {
        return Map.of(
                "predictedStockPrice", predictNextYearValue(selectedCompany.getStockPrices()),
                "predictedRevenue", predictNextYearValue(selectedCompany.getRevenues()),
                "predictedExpense", predictNextYearValue(selectedCompany.getExpenses()),
                "predictedMarketShare", predictNextYearValue(selectedCompany.getMarketShares())
        );
    }

    // Helper method to predict next year's value using average growth trend
    private double predictNextYearValue(Map<String, ?> data) {
        double totalChange = 0.0;
        int years = 0;
        String previousYear = null;

        for (String year : data.keySet()) {
            if (previousYear != null) {
                double previousValue = Double.parseDouble(data.get(previousYear).toString());
                double currentValue = Double.parseDouble(data.get(year).toString());
                totalChange += (currentValue - previousValue);
                years++;
            }
            previousYear = year;
        }

        return years > 0 ? (totalChange / years) + Double.parseDouble(data.get("2024").toString()) : 0.0;
    }
}
