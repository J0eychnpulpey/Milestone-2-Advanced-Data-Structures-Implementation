public class Stock {
    private String date;
    private String status;
    private String brand;
    private String engineNumber;
    private String purchaseStatus;
    
    // Constructor
    public Stock(String date, String status, String brand, String engineNumber, String purchaseStatus) {
        this.date = date;
        this.status = status;
        this.brand = brand;
        this.engineNumber = engineNumber;
        this.purchaseStatus = purchaseStatus;
    }
    
    // Getters and setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getEngineNumber() { return engineNumber; }
    public void setEngineNumber(String engineNumber) { this.engineNumber = engineNumber; }
    
    public String getPurchaseStatus() { return purchaseStatus; }
    public void setPurchaseStatus(String purchaseStatus) { this.purchaseStatus = purchaseStatus; }
    
    @Override
    public String toString() {
        return "Date: " + date + 
               ", Status: " + status + 
               ", Brand: " + brand + 
               ", Engine Number: " + engineNumber + 
               ", Purchase Status: " + purchaseStatus;
    }
}