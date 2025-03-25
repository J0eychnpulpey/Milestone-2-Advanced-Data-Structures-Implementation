import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

public class HashIndexes {
    // Primary index by engine number (unique)
    private Map<String, Stock> engineNumberMap;
    
    // Secondary indexes (non-unique)
    private Map<String, ArrayList<Stock>> brandMap;
    private Map<String, ArrayList<Stock>> statusMap;
    private Map<String, ArrayList<Stock>> purchaseStatusMap;
    
    // Constructor
    public HashIndexes() {
        engineNumberMap = new HashMap<>();
        brandMap = new HashMap<>();
        statusMap = new HashMap<>();
        purchaseStatusMap = new HashMap<>();
    }
    
    /**
     * Add a stock to all indexes
     */
    public void addStock(Stock stock) {
        // Add to primary index
        engineNumberMap.put(stock.getEngineNumber(), stock);
        
        // Add to brand index
        String brand = stock.getBrand().toLowerCase();
        if (!brandMap.containsKey(brand)) {
            brandMap.put(brand, new ArrayList<>());
        }
        brandMap.get(brand).add(stock);
        
        // Add to status index
        String status = stock.getStatus().toLowerCase();
        if (!statusMap.containsKey(status)) {
            statusMap.put(status, new ArrayList<>());
        }
        statusMap.get(status).add(stock);
        
        // Add to purchase status index
        String purchaseStatus = stock.getPurchaseStatus().toLowerCase();
        if (!purchaseStatusMap.containsKey(purchaseStatus)) {
            purchaseStatusMap.put(purchaseStatus, new ArrayList<>());
        }
        purchaseStatusMap.get(purchaseStatus).add(stock);
    }
    
    /**
     * Remove a stock from all indexes
     */
    public void removeStock(Stock stock) {
        // Remove from primary index
        engineNumberMap.remove(stock.getEngineNumber());
        
        // Remove from brand index
        String brand = stock.getBrand().toLowerCase();
        if (brandMap.containsKey(brand)) {
            brandMap.get(brand).remove(stock);
            
            // Clean up empty lists
            if (brandMap.get(brand).isEmpty()) {
                brandMap.remove(brand);
            }
        }
        
        // Remove from status index
        String status = stock.getStatus().toLowerCase();
        if (statusMap.containsKey(status)) {
            statusMap.get(status).remove(stock);
            
            // Clean up empty lists
            if (statusMap.get(status).isEmpty()) {
                statusMap.remove(status);
            }
        }
        
        // Remove from purchase status index
        String purchaseStatus = stock.getPurchaseStatus().toLowerCase();
        if (purchaseStatusMap.containsKey(purchaseStatus)) {
            purchaseStatusMap.get(purchaseStatus).remove(stock);
            
            // Clean up empty lists
            if (purchaseStatusMap.get(purchaseStatus).isEmpty()) {
                purchaseStatusMap.remove(purchaseStatus);
            }
        }
    }
    
    /**
     * Check if engine number exists
     */
    public boolean containsEngineNumber(String engineNumber) {
        return engineNumberMap.containsKey(engineNumber);
    }
    
    /**
     * Get stock by engine number
     */
    public Stock getByEngineNumber(String engineNumber) {
        return engineNumberMap.get(engineNumber);
    }
    
    /**
     * Get stocks by brand (exact match)
     */
    public ArrayList<Stock> getByBrand(String brand) {
        String key = brand.toLowerCase();
        return brandMap.containsKey(key) ? new ArrayList<>(brandMap.get(key)) : new ArrayList<>();
    }
    
    /**
     * Get stocks by status
     */
    public ArrayList<Stock> getByStatus(String status) {
        String key = status.toLowerCase();
        return statusMap.containsKey(key) ? new ArrayList<>(statusMap.get(key)) : new ArrayList<>();
    }
    
    /**
     * Get stocks by purchase status
     */
    public ArrayList<Stock> getByPurchaseStatus(String purchaseStatus) {
        String key = purchaseStatus.toLowerCase();
        return purchaseStatusMap.containsKey(key) ? new ArrayList<>(purchaseStatusMap.get(key)) : new ArrayList<>();
    }
    
    /**
     * Get all stocks sorted by brand
     */
    public ArrayList<Stock> getAllStocksSortedByBrand() {
        // Use TreeMap for automatic sorting of keys
        TreeMap<String, ArrayList<Stock>> sortedBrands = new TreeMap<>(brandMap);
        
        ArrayList<Stock> result = new ArrayList<>();
        for (ArrayList<Stock> stockList : sortedBrands.values()) {
            result.addAll(stockList);
        }
        
        return result;
    }
    
    /**
     * Get total count of stocks in inventory
     */
    public int getTotalStockCount() {
        return engineNumberMap.size();
    }
    
    /**
     * Get all stocks
     */
    public List<Stock> getAllStocks() {
        return new ArrayList<>(engineNumberMap.values());
    }
}