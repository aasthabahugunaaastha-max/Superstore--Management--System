public class SuperstoreManagementSystem {
    public static void main(String[] args) {
        // Create users
        User superUser = new SuperUser("admin", "admin123");
        User warehouseAdmin = new WarehouseAdmin("whAdmin", "whAdmin123");

        // Create warehouse and items
        Warehouse warehouse = new Warehouse("WH001", "Main Warehouse");
        Item item1 = new Item("Laptop", "E001", 100, 2000, 50);
        warehouse.addItem(item1);

        // Create store
        Store store = new Store("ST001", "City Store");
        store.addItem(item1);

        // Simulate login
        superUser.login();
        warehouseAdmin.login();

        // Warehouse admin calculates EOQ
        warehouse.calculateAndDisplayEOQ();

        // Store places an order
        store.placeOrder(warehouse, item1, 10);

        // Trigger reorder alert
        Order order = new Order("ORD001", warehouse, store);
        order.triggerReorderAlert();
    }
}
