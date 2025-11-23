// Warehouse class
class Warehouse {
    private String warehouseId;
    private String warehouseName;
    private List<Item> items;
    private double safetyStock;

    public Warehouse(String warehouseId, String warehouseName) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.items = new ArrayList<>();
    }

    // Add item to warehouse inventory
    public void addItem(Item item) {
        items.add(item);
    }

    // Calculate Safety Stock
    public double calculateSafetyStock(double maxDailyUsage, double maxLeadTime, double avgDailyUsage, double avgLeadTime) {
        return (maxDailyUsage * maxLeadTime) - (avgDailyUsage * avgLeadTime);
    }

    // Calculate reorder point
    public double calculateReorderPoint(double leadTime, double avgDailyUsage, double safetyStock) {
        return (leadTime * avgDailyUsage) + safetyStock;
    }

    // Getters and Setters
    public List<Item> getItems() {
        return items;
    }
}

// Store class
class Store {
    private String storeId;
    private String storeName;
    private List<Item> inventory;

    public Store(String storeId, String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
        inventory = new ArrayList<>();
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void placeOrder(Warehouse warehouse, Item item, int quantity) {
        System.out.println("Placing order for " + quantity + " units of " + item.getItemName() + " from warehouse.");
        // Implement order placement logic
    }
}
