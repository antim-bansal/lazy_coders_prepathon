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
            model.addAttribute("company", selectedCompany);
            logger.info("Displaying details for company: {}", selectedCompany.getCompanyName());
        } else {
            logger.warn("No companies found for the name: {}", companyName);
        }
        return "companyDetails"; // Maps to src/main/resources/templates/companyDetails.html
    }
    //  @GetMapping("/compute")
    // public String performComputation(@RequestParam("companyCode") String companyCode, Model model) {
    //     logger.info("Performing computation for company code: {}", companyCode);
    //     CompanyComputationResult result = companyService.performComputationWithDelay(companyCode);
    //     model.addAttribute("result", result);
    //     logger.info("Computation completed for company code: {}", companyCode);
    //     return "computationResult"; // Maps to src/main/resources/templates/computationResult.html
    // }
}
