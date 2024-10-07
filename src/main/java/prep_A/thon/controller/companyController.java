package prep_A.thon.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import prep_A.thon.model.CompanyComputationResult;
import prep_A.thon.model.company;
import prep_A.thon.service.CompanyService;

import java.util.List;
import java.util.Map;

@Controller
public class companyController {

    private final CompanyService companyService;
    private static final Logger logger = LoggerFactory.getLogger(companyController.class);

    @Autowired
    public companyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // Home page route, maps to /
    @GetMapping("/")
    public String home() {
        logger.info("Accessing home page.");
        return "index"; // Maps to src/main/resources/templates/index.html
    }

    // Search companies by name
    @GetMapping("/search")
    public String searchCompanies(@RequestParam("companyName") String companyName, Model model) {
        logger.info("Received search request for company name: {}", companyName);
        List<company> companies = companyService.searchCompanies(companyName);
        model.addAttribute("companies", companies);
        logger.info("Found {} companies matching the name '{}'.", companies.size(), companyName);
        return "searchResults";
    }

    

    // Display company details by name
    @GetMapping("/companyDetails")
    public String companyDetails(@RequestParam("companyName") String companyName, Model model) {
        logger.info("Displaying details for company: {}", companyName);
        List<company> companies = companyService.searchCompanies(companyName);
        
        if (!companies.isEmpty()) {
            company selectedCompany = companies.get(0);

            // Populate model with company details
            model.addAttribute("company", selectedCompany);
            
            // (a) Count companies in the same country
            long sameCountryCount = companyService.countCompaniesInSameCountry(selectedCompany);
            model.addAttribute("sameCountryCount", sameCountryCount);
            
            // (b) Count companies with greater diversity in the same country
            long greaterDiversityCount = companyService.countCompaniesWithGreaterDiversity(selectedCompany);
            model.addAttribute("greaterDiversityCount", greaterDiversityCount);
            
            // (c) Calculate year-by-year increase/decrease for stock price, market share, revenue, and expense
            Map<String, String> stockPriceChange = companyService.calculateYearlyChange(selectedCompany.getStockPrices());
            // Map<String, String> marketShareChange = companyService.calculateYearlyChange(selectedCompany.getMarketShares());
            Map<String, String> revenueChange = companyService.calculateYearlyChange(selectedCompany.getRevenues());
            Map<String, String> expenseChange = companyService.calculateYearlyChange(selectedCompany.getExpenses());
            
            model.addAttribute("stockPriceChange", stockPriceChange);
            // model.addAttribute("marketShareChange", marketShareChange);
            model.addAttribute("revenueChange", revenueChange);
            model.addAttribute("expenseChange", expenseChange);
            
            // (d) Count companies with greater stock price, market share, revenue, and expense domestically and globally
            Map<String, Long> greaterMetricsCount = companyService.countCompaniesWithGreaterMetrics(selectedCompany);
            model.addAttribute("greaterMetricsCount", greaterMetricsCount);
            
            // (e) Comment on company growth and stability
            String growthComment = companyService.commentOnCompanyGrowth(selectedCompany);
            model.addAttribute("growthComment", growthComment);
            
            // (f) Predict next year's metrics
            Map<String, Double> predictedMetrics = companyService.predictNextYearMetrics(selectedCompany);
            model.addAttribute("predictedMetrics", predictedMetrics);
            
            logger.info("Displaying details for company: {}", selectedCompany.getCompanyName());
        } else {
            logger.warn("No companies found for the name: {}", companyName);
        }

        return "companyDetails"; // Maps to src/main/resources/templates/companyDetails.html
    }

}
