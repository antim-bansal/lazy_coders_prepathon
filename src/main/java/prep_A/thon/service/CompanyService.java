package prep_A.thon.service;

import org.springframework.stereotype.Service;
import prep_A.thon.model.company;
import prep_A.thon.utils.CSVReaderUtil;
import com.opencsv.exceptions.CsvValidationException; // Import for CSV validation exception

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
public class CompanyService {

    private static final Logger logger= LoggerFactory.getLogger(CompanyService.class);
    private final CSVReaderUtil csvReaderUtil;
    private List<company> companies;

    public CompanyService(CSVReaderUtil csvReaderUtil) {
        this.csvReaderUtil = csvReaderUtil;
        try {
            // Load CSV data once when the service is initialized
            this.companies = csvReaderUtil.readCSV("C:\\Users\\antim\\Downloads\\thon\\thon\\src\\main\\resources\\Mock Data Prepathon.csv");
            logger.info("CSV file loaded successfully with {} companies.", companies.size());
        } catch (IOException | CsvValidationException e) {
            logger.error("Error reading CSV file: {}", e.getMessage(), e);
            this.companies = new ArrayList<>();
        }
    }

    // Method to search companies by name (partial or full match)
    public List<company> searchCompanies(String companyName) {
        logger.info("Searching for companies with name: {}", companyName);
        List<company> foundCompanies = companies.stream()
                .filter(company -> company.getCompanyName().toLowerCase().contains(companyName.toLowerCase()))
                .collect(Collectors.toList());
    
        logger.info("Found {} companies matching the name '{}'.", foundCompanies.size(), companyName);
        return foundCompanies;
    }
    

    // Method to count companies from the same country
    public long countCompaniesInSameCountry(company selectedCompany) {
        return companies.stream()
                .filter(company -> company.getCountry().equalsIgnoreCase(selectedCompany.getCountry()))
                .count();
    }

    // Method to count companies with greater diversity in the same country
    public long countCompaniesWithGreaterDiversity(company selectedCompany) {
        return companies.stream()
                .filter(company -> company.getCountry().equalsIgnoreCase(selectedCompany.getCountry()) &&
                        company.getDiversity() > selectedCompany.getDiversity())
                .count();
    }

    // Method to calculate year-by-year increase or decrease in stock prices, revenue, and expenses
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

    // Method to calculate companies with greater stock price, market share, revenue, and expenses
    public Map<String, Long> countCompaniesWithGreaterMetrics(company selectedCompany) {
        Map<String, Long> metrics = Map.of(
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
        return metrics;
    }

    // Helper method to check if one company has greater values than another for a given map (used for revenue, stock price, etc.)
    private boolean companyHasGreaterValue(Map<String, ?> company1Data, Map<String, ?> company2Data) {
        return company1Data.entrySet().stream()
                .allMatch(entry -> Double.parseDouble(entry.getValue().toString()) > Double.parseDouble(company2Data.get(entry.getKey()).toString()));
    }

    // Method to comment on the company's growth and stability
    public String commentOnCompanyGrowth(company selectedCompany) {
        Map<String, String> revenueGrowth = calculateYearlyChange(selectedCompany.getRevenues());
        Map<String, String> expenseGrowth = calculateYearlyChange(selectedCompany.getExpenses());

        // Analyze the growth pattern and return a comment
        String growthComment = "The company has shown consistent growth in revenue but fluctuating expenses.";
        // You can extend this logic based on your requirements
        return growthComment;
    }

    // Method to predict next year's stock price, market share, revenue, and expenses
    public Map<String, Double> predictNextYearMetrics(company selectedCompany) {
        Map<String, Double> predictions = Map.of(
                "predictedStockPrice", predictNextYearValue(selectedCompany.getStockPrices()),
                "predictedRevenue", predictNextYearValue(selectedCompany.getRevenues()),
                "predictedExpense", predictNextYearValue(selectedCompany.getExpenses()),
                "predictedMarketShare", predictNextYearValue(selectedCompany.getMarketShares())
        );
        return predictions;
    }

    // Helper method to predict the next year's value based on linear progression of previous years
    private double predictNextYearValue(Map<String, ?> data) {
        // Simple prediction: take the average of year-over-year changes and project it to the next year
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

    // Method to perform computation with a delay
    // public CompanyComputationResult performComputationWithDelay(String companyCode) {
    //     CompanyComputationResult result = performComputation(companyCode); // Call your computation method

    //     long computationTime = result.getActualComputationTime(); // Assume method to get actual time
    //     long delay = TimeUnit.MINUTES.toMillis(2) - computationTime; // Set delay to 2 minutes

    //     if (delay > 0) {
    //         try {
    //             Thread.sleep(delay); // Add artificial delay
    //         } catch (InterruptedException e) {
    //             e.printStackTrace(); // Handle interruption appropriately
    //         }
    //     }

    //     return result;
    // }

    // Placeholder for actual computation logic
    // private CompanyComputationResult performComputation(String companyCode) {
    //     // Your logic for computation here
    //     return new CompanyComputationResult(); // Return a new instance
    // }
}
