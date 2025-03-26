import java.io.*;
import java.util.*;

public class InventorySystem {
    private StockNode root;        // BST root
    private HashIndexes indexes;   // Hash-based indexes
    private String csvFilePath;    // Path to CSV file for persistence
    
    // Constructor with file path parameter
    public InventorySystem(String csvFilePath) {
        this.root = null;
        this.indexes = new HashIndexes();
        this.csvFilePath = csvFilePath;
    }
    
    // Default constructor
    public InventorySystem() {
        this.root = null;
        this.indexes = new HashIndexes();
        this.csvFilePath = null;
    }
    
    /**
     * Set the CSV file path
     */
    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }
    
    /**
     * Load inventory data from CSV file
     */
    public boolean loadInventoryFromCSV(String filePath) {
        // Update the file path if provided
        if (filePath != null) {
            this.csvFilePath = filePath;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(this.csvFilePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                // Skip header row
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // Split CSV line
                String[] data = line.split(",");
                if (data.length >= 5) {
                    // Create new stock
                    Stock stock = new Stock(
                        data[0].trim(),  // date
                        data[1].trim(),  // status (Old/New)
                        data[2].trim(),  // brand
                        data[3].trim(),  // engineNumber
                        data[4].trim()   // purchaseStatus (On-hand/Sold)
                    );
                    
                    // Add to both data structures without saving back to CSV
                    addStockWithoutSaving(stock);
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error loading inventory data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save inventory data to CSV file
     */
    public boolean saveInventoryToCSV() {
        if (csvFilePath == null) {
            System.err.println("Error: CSV file path not set");
            return false;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            // Write header row
            writer.write("Date,Status,Brand,Engine Number,Purchase Status");
            writer.newLine();
            
            // Get all inventory items
            ArrayList<Stock> items = getAllItems();
            
            // Write each item to the CSV
            for (Stock stock : items) {
                writer.write(String.format("%s,%s,%s,%s,%s",
                    stock.getDate(),
                    stock.getStatus(),
                    stock.getBrand(),
                    stock.getEngineNumber(),
                    stock.getPurchaseStatus()));
                writer.newLine();
            }
            
            System.out.println("Inventory saved to CSV file successfully!");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving inventory data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add stock without saving to CSV (used for initial loading)
     */
    private String addStockWithoutSaving(Stock newStock) {
        // Check if engine number already exists using hash table (O(1))
        if (indexes.containsEngineNumber(newStock.getEngineNumber())) {
            return "Error: Engine number already exists in inventory";
        }
        
        // Add to BST
        root = insertNode(root, newStock);
        
        // Add to hash indexes
        indexes.addStock(newStock);
        
        return "Success: New stock added to inventory";
    }
    
    /**
     * Add new stock with validation and save to CSV
     */
    public String addStock(Stock newStock) {
        // Check if engine number already exists using hash table (O(1))
        if (indexes.containsEngineNumber(newStock.getEngineNumber())) {
            return "Error: Engine number already exists in inventory";
        }
        
        // Add to BST
        root = insertNode(root, newStock);
        
        // Add to hash indexes
        indexes.addStock(newStock);
        
        // Save changes to CSV file
        if (csvFilePath != null) {
            saveInventoryToCSV();
        }
        
        return "Success: New stock added to inventory";
    }
    
    /**
     * Insert a node into the BST (recursive)
     */
    private StockNode insertNode(StockNode node, Stock stock) {
        // If tree is empty, create new node
        if (node == null) {
            return new StockNode(stock);
        }
        
        // Compare engine numbers to determine insertion path
        int compareResult = stock.getEngineNumber().compareTo(node.stock.getEngineNumber());
        
        if (compareResult < 0) {
            // Insert in left subtree
            node.left = insertNode(node.left, stock);
        } else if (compareResult > 0) {
            // Insert in right subtree
            node.right = insertNode(node.right, stock);
        }
        // If equal, node already exists (handled in addStock method)
        
        return node;
    }
    
    /**
     * Delete stock with validation and save to CSV
     */
    public String deleteStock(String engineNumber) {
        // Find stock using hash table (O(1))
        Stock stock = indexes.getByEngineNumber(engineNumber);
        
        // Check if stock exists
        if (stock == null) {
            return "Error: Engine number not found in inventory";
        }
        
        // Check status criteria (Old or Sold)
        String status = stock.getStatus();
        String purchaseStatus = stock.getPurchaseStatus();
        
        if (status.equals("Old") || purchaseStatus.equals("Sold")) {
            // Remove from hash indexes first (to preserve the object)
            indexes.removeStock(stock);
            
            // Remove from BST
            root = deleteNode(root, engineNumber);
            
            // Save changes to CSV file
            if (csvFilePath != null) {
                saveInventoryToCSV();
            }
            
            return "Success: Stock with engine number " + engineNumber + " has been deleted";
        } else {
            return "Error: Only Old or Sold stocks can be deleted";
        }
    }
    
    /**
     * Delete a node from the BST (recursive)
     */
    private StockNode deleteNode(StockNode node, String engineNumber) {
        if (node == null) {
            return null;
        }
        
        int compareResult = engineNumber.compareTo(node.stock.getEngineNumber());
        
        if (compareResult < 0) {
            // Delete from left subtree
            node.left = deleteNode(node.left, engineNumber);
        } else if (compareResult > 0) {
            // Delete from right subtree
            node.right = deleteNode(node.right, engineNumber);
        } else {
            // This is the node to delete
            
            // Case 1: No children
            if (node.left == null && node.right == null) {
                return null;
            }
            
            // Case 2: One child
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            
            // Case 3: Two children
            // Find the smallest node in the right subtree (successor)
            Stock smallestValue = findMin(node.right).stock;
            node.stock = smallestValue;
            
            // Delete the successor
            node.right = deleteNode(node.right, smallestValue.getEngineNumber());
        }
        
        return node;
    }
    
    /**
     * Find the minimum value node (leftmost node)
     */
    private StockNode findMin(StockNode node) {
        if (node == null) {
            return null;
        }
        
        if (node.left == null) {
            return node;
        }
        
        return findMin(node.left);
    }
    
    /**
     * Sort inventory by brand
     * This can be implemented in two ways:
     * 1. Using in-order traversal of BST and then sorting by brand
     * 2. Using the brand hash map (which is already grouped by brand)
     */
    public ArrayList<Stock> sortByBrand() {
        // Method 1: In-order traversal + sorting
        ArrayList<Stock> allStocks = new ArrayList<>();
        inOrderTraversal(root, allStocks);
        
        Collections.sort(allStocks, new Comparator<Stock>() {
            @Override
            public int compare(Stock s1, Stock s2) {
                return s1.getBrand().compareToIgnoreCase(s2.getBrand());
            }
        });
        
        // Alternatively, we could use the hash-based approach:
        // return indexes.getAllStocksSortedByBrand();
        
        return allStocks;
    }
    
    /**
     * In-order traversal to get all items
     */
    private void inOrderTraversal(StockNode node, ArrayList<Stock> result) {
        if (node != null) {
            inOrderTraversal(node.left, result);
            result.add(node.stock);
            inOrderTraversal(node.right, result);
        }
    }
    
    /**
     * Search inventory by various criteria using optimized approach
     */
    public ArrayList<Stock> searchStock(String criteria, String value) {
        ArrayList<Stock> results = new ArrayList<>();
        
        switch (criteria.toLowerCase()) {
            case "enginenumber":
                // Direct hash lookup (O(1))
                Stock stock = indexes.getByEngineNumber(value);
                if (stock != null) {
                    results.add(stock);
                }
                break;
                
            case "brand":
                // Try direct hash lookup first for exact matches
                ArrayList<Stock> exactMatches = indexes.getByBrand(value);
                if (!exactMatches.isEmpty()) {
                    results.addAll(exactMatches);
                } else {
                    // Use recursive search for partial matches
                    searchByPartialBrand(root, value.toLowerCase(), results);
                }
                break;
                
            case "status":
                // Direct hash lookup
                results.addAll(indexes.getByStatus(value));
                break;
                
            case "purchasestatus":
                // Direct hash lookup
                results.addAll(indexes.getByPurchaseStatus(value));
                break;
                
            case "date":
                // Use recursive search for date
                searchByDate(root, value, results);
                break;
                
            default:
                // Fallback to general criteria search
                searchByCriteria(root, criteria, value, results);
        }
        
        return results;
    }
    
    /**
     * Search for partial brand matches (recursive)
     */
    private void searchByPartialBrand(StockNode node, String partialBrand, ArrayList<Stock> results) {
        if (node == null) {
            return;
        }
        
        // Check current node
        if (node.stock.getBrand().toLowerCase().contains(partialBrand)) {
            results.add(node.stock);
        }
        
        // Check both subtrees (can't use BST property for partial matches)
        searchByPartialBrand(node.left, partialBrand, results);
        searchByPartialBrand(node.right, partialBrand, results);
    }
    
    /**
     * Search by date (recursive)
     */
    private void searchByDate(StockNode node, String date, ArrayList<Stock> results) {
        if (node == null) {
            return;
        }
        
        // Check current node
        if (node.stock.getDate().contains(date)) {
            results.add(node.stock);
        }
        
        // Check both subtrees
        searchByDate(node.left, date, results);
        searchByDate(node.right, date, results);
    }
    
    /**
     * General criteria search (recursive)
     */
    private void searchByCriteria(StockNode node, String criteria, String value, ArrayList<Stock> results) {
        if (node == null) {
            return;
        }
        
        // Check current node
        boolean matches = false;
        
        switch (criteria.toLowerCase()) {
            case "date":
                if (node.stock.getDate().contains(value)) {
                    matches = true;
                }
                break;
            case "status":
                if (node.stock.getStatus().equalsIgnoreCase(value)) {
                    matches = true;
                }
                break;
            case "brand":
                if (node.stock.getBrand().toLowerCase().contains(value.toLowerCase())) {
                    matches = true;
                }
                break;
            case "enginenumber":
                if (node.stock.getEngineNumber().contains(value)) {
                    matches = true;
                }
                break;
            case "purchasestatus":
                if (node.stock.getPurchaseStatus().equalsIgnoreCase(value)) {
                    matches = true;
                }
                break;
        }
        
        if (matches) {
            results.add(node.stock);
        }
        
        // Search both subtrees
        searchByCriteria(node.left, criteria, value, results);
        searchByCriteria(node.right, criteria, value, results);
    }
    
    /**
     * Get all inventory items
     */
    public ArrayList<Stock> getAllItems() {
        ArrayList<Stock> allItems = new ArrayList<>();
        inOrderTraversal(root, allItems);
        return allItems;
    }
    
    /**
     * Check inventory size
     */
    public int getInventorySize() {
        return indexes.getTotalStockCount();
    }
    
    /**
     * Display all inventory items
     */
    public void displayInventory() {
        System.out.println("\n=== Current Inventory ===");
        ArrayList<Stock> items = getAllItems();
        for (Stock stock : items) {
            System.out.println(stock);
        }
        System.out.println("Total items: " + items.size());
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        // Update this path to match your CSV file location
        String csvFilePath = "/workspaces/Milestone-2-Advanced-Data-Structures-Implementation/MotorPHInventorySystem/src/data/Copy of MotorPH Inventory Data - March 2023 Inventory Data.csv";
        
        // Create system with file path
        InventorySystem system = new InventorySystem(csvFilePath);
        
        // Load inventory data
        System.out.println("Loading inventory data...");
        boolean loaded = system.loadInventoryFromCSV(csvFilePath);
        
        if (!loaded) {
            System.out.println("Failed to load inventory data. Exiting...");
            return;
        }
        
        System.out.println("Successfully loaded " + system.getInventorySize() + " inventory items.");
        
        // Test functionality
        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        
        while (choice != 5) {
            System.out.println("\n=== MotorPH Inventory Management (BST+Hash Implementation) ===");
            System.out.println("1. Add new stock");
            System.out.println("2. Delete stock");
            System.out.println("3. Sort by brand");
            System.out.println("4. Search inventory");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                switch (choice) {
                    case 1:
                        // Add new stock
                        System.out.println("\n=== Add New Stock ===");
                        System.out.print("Enter date (MM/DD/YYYY): ");
                        String date = scanner.nextLine();
                        System.out.print("Enter status (Old/New): ");
                        String status = scanner.nextLine();
                        System.out.print("Enter brand: ");
                        String brand = scanner.nextLine();
                        System.out.print("Enter engine number: ");
                        String engineNumber = scanner.nextLine();
                        System.out.print("Enter purchase status (On-hand/Sold): ");
                        String purchaseStatus = scanner.nextLine();
                        
                        Stock newStock = new Stock(date, status, brand, engineNumber, purchaseStatus);
                        String result = system.addStock(newStock);
                        System.out.println(result);
                        break;
                        
                    case 2:
                        // Delete stock
                        System.out.println("\n=== Delete Stock ===");
                        System.out.print("Enter engine number to delete: ");
                        String engineToDelete = scanner.nextLine();
                        System.out.println(system.deleteStock(engineToDelete));
                        break;
                        
                    case 3:
                        // Sort by brand
                        System.out.println("\n=== Inventory Sorted by Brand ===");
                        ArrayList<Stock> sorted = system.sortByBrand();
                        for (Stock stock : sorted) {
                            System.out.println(stock);
                        }
                        System.out.println("Total items: " + sorted.size());
                        break;
                        
                    case 4:
                        // Search inventory
                        System.out.println("\n=== Search Inventory ===");
                        System.out.println("1. Search by date");
                        System.out.println("2. Search by status");
                        System.out.println("3. Search by brand");
                        System.out.println("4. Search by engine number");
                        System.out.println("5. Search by purchase status");
                        System.out.print("Enter search type: ");
                        int searchType = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        
                        String criteria = "";
                        switch (searchType) {
                            case 1: criteria = "date"; break;
                            case 2: criteria = "status"; break;
                            case 3: criteria = "brand"; break;
                            case 4: criteria = "enginenumber"; break;
                            case 5: criteria = "purchasestatus"; break;
                            default:
                                System.out.println("Invalid search type");
                                continue;
                        }
                        
                        System.out.print("Enter search value: ");
                        String searchValue = scanner.nextLine();
                        
                        ArrayList<Stock> results = system.searchStock(criteria, searchValue);
                        
                        System.out.println("\nSearch Results:");
                        if (results.isEmpty()) {
                            System.out.println("No matching items found");
                        } else {
                            for (Stock stock : results) {
                                System.out.println(stock);
                            }
                            System.out.println("Found " + results.size() + " items");
                        }
                        break;
                        
                    case 5:
                        System.out.println("Exiting...");
                        break;
                        
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
                choice = 0;
            }
        }
        
        scanner.close();
    }
}
