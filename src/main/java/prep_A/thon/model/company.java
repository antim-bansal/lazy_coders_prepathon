package prep_A.thon.model;

import java.util.Map;

public class company {

    private int slNo;
    private String companyName;
    private String country;
    private String countryCode;
    private String marketCap;
    private double diversity;
    private Map<String, String> stockPrices;
    private Map<String, Double> marketShares;
    private Map<String, String> revenues;
    private Map<String, String> expenses;

    // Getters and Setters

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public double getDiversity() {
        return diversity;
    }

    public void setDiversity(double diversity) {
        this.diversity = diversity;
    }

    public Map<String, String> getStockPrices() {
        return stockPrices;
    }

    public void setStockPrices(Map<String, String> stockPrices) {
        this.stockPrices = stockPrices;
    }

    public Map<String, Double> getMarketShares() {
        return marketShares;
    }

    public void setMarketShares(Map<String, Double> marketShares) {
        this.marketShares = marketShares;
    }

    public Map<String, String> getRevenues() {
        return revenues;
    }

    public void setRevenues(Map<String, String> revenues) {
        this.revenues = revenues;
    }

    public Map<String, String> getExpenses() {
        return expenses;
    }

    public void setExpenses(Map<String, String> expenses) {
        this.expenses = expenses;
    }
}


