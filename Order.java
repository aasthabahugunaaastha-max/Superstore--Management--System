// Order class
class Order {
    private String orderId;
    private Warehouse warehouse;
    private Store store;
    private List<Item> items;
    private Date orderDate;
    private Date expectedArrivalDate;

    public Order(String orderId, Warehouse warehouse, Store store) {
        this.orderId = orderId;
        this.warehouse = warehouse;
        this.store = store;
        this.items = new ArrayList<>();
        this.orderDate = new Date();
    }

    // Add item to order
    public void addItem(Item item, int quantity) {
        System.out.println("Added item " + item.getItemName() + " to order.");
        items.add(item);
    }

    // Method to check and trigger reorder alert
    public void triggerReorderAlert() {
        for (Item item : warehouse.getItems()) {
            double reorderPoint = warehouse.calculateReorderPoint(10, 20, 30);  // Example values
            if (item.calculateEOQ() <= reorderPoint) {
                System.out.println("Reorder alert: " + item.getItemName() + " needs to be ordered!");
            }
        }
    }
}
