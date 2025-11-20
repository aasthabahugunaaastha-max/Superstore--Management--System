package store;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionModule {

    // ===== Domain Models =====
    public static class Product {
        public final String id;
        public String name;
        public String category;
        public double price;
        public int stock;

        public Product(String id, String name, String category, double price, int stock) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }
    }

    public static class Customer {
        public final String id;
        public String name;
        public String phone;

        public Customer(String id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }
    }

    public static class Seller {
        public final String id;
        public String name;

        public Seller(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class Sale {
        public final String saleId;
        public final String productId;
        public final String customerId;
        public final String sellerId;
        public final int quantity;
        public final double unitPrice;
        public final LocalDateTime timestamp;

        public Sale(String saleId, String productId, String customerId, String sellerId,
                    int quantity, double unitPrice, LocalDateTime timestamp) {
            this.saleId = saleId;
            this.productId = productId;
            this.customerId = customerId;
            this.sellerId = sellerId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.timestamp = timestamp;
        }
    }

    public static class User {
        public final String username;
        public final String password;
        public final String role; // "admin" or "seller"

        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }

    // ===== In-memory Store =====
    public static class StoreDB {
        public static final Map<String, Product> products = new LinkedHashMap<>();
        public static final Map<String, Customer> customers = new LinkedHashMap<>();
        public static final Map<String, Seller> sellers = new LinkedHashMap<>();
        public static final Map<String, Sale> sales = new LinkedHashMap<>();
        public static final Map<String, User> users = new LinkedHashMap<>();

        public static void seedDemoData() {
            if (!users.isEmpty()) return;
            users.put("admin", new User("admin", "admin123", "admin"));
            users.put("rohan", new User("rohan", "seller123", "seller"));

            products.put("P100", new Product("P100", "Basmati Rice 5kg", "Grocery", 549.0, 20));
            products.put("P101", new Product("P101", "Toothpaste", "Personal Care", 89.0, 50));

            customers.put("C001", new Customer("C001", "Neha Sharma", "9876543210"));
            customers.put("C002", new Customer("C002", "Arjun Mehta", "9990011223"));

            sellers.put("S001", new Seller("S001", "Rohan"));
            sellers.put("S002", new Seller("S002", "Priya"));
        }
    }

    // ===== Dashboard (CLI) =====
    public static void dashboard(Scanner sc, String username, String role) {
        while (true) {
            System.out.println("\n=== Super Store Dashboard ===");
            System.out.println("Logged in: " + username + " (" + role + ")");
            System.out.println("1. Add new product");
            System.out.println("2. Update product");
            System.out.println("3. Sell product");
            System.out.println("4. List products");
            System.out.println("5. Customer info");
            System.out.println("6. Seller info");
            System.out.println("7. View sales");
            System.out.println("8. Search product by name/category");
            System.out.println("9. Logout");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": addProduct(sc, role); break;
                case "2": updateProduct(sc, role); break;
                case "3": sellProduct(sc, username, role); break;
                case "4": listProducts(); break;
                case "5": customerInfo(sc, role); break;
                case "6": sellerInfo(sc, role); break;
                case "7": viewSales(); break;
                case "8": searchProducts(sc); break;
                case "9": System.out.println("Goodbye!"); return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    // ===== Actions =====

    // Add new product (admin-only)
    private static void addProduct(Scanner sc, String role) {
        if (!"admin".equals(role)) {
            System.out.println("Permission denied: only admin can add products.");
            return;
        }
        System.out.println("=== Add New Product ===");
        System.out.print("Product ID: ");
        String id = sc.nextLine().trim();
        if (StoreDB.products.containsKey(id)) {
            System.out.println("Product ID already exists.");
            return;
        }
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Category: ");
        String cat = sc.nextLine().trim();
        System.out.print("Price: ");
        double price = parseDouble(sc.nextLine());
        System.out.print("Stock: ");
        int stock = parseInt(sc.nextLine());

        StoreDB.products.put(id, new Product(id, name, cat, price, stock));
        System.out.println("Product added: " + id);
    }

    // Update product fields (admin-only)
    private static void updateProduct(Scanner sc, String role) {
        if (!"admin".equals(role)) {
            System.out.println("Permission denied: only admin can update products.");
            return;
        }
        System.out.println("=== Update Product ===");
        System.out.print("Product ID: ");
        String id = sc.nextLine().trim();
        Product p = StoreDB.products.get(id);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }
        System.out.println("Leave field blank to keep current.");
        System.out.print("New name (" + p.name + "): ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) p.name = name;

        System.out.print("New category (" + p.category + "): ");
        String cat = sc.nextLine().trim();
        if (!cat.isEmpty()) p.category = cat;

        System.out.print("New price (" + p.price + "): ");
        String priceStr = sc.nextLine().trim();
        if (!priceStr.isEmpty()) p.price = parseDouble(priceStr);

        System.out.print("New stock (" + p.stock + "): ");
        String stockStr = sc.nextLine().trim();
        if (!stockStr.isEmpty()) p.stock = parseInt(stockStr);

        System.out.println("Updated: " + id);
    }

    // Sell product: create a sale, decrement stock
    private static void sellProduct(Scanner sc, String username, String role) {
        System.out.println("=== Sell Product ===");
        System.out.print("Product ID: ");
        String pid = sc.nextLine().trim();
        Product p = StoreDB.products.get(pid);
        if (p == null) {
            System.out.println("Product not found.");
            return;
        }
        System.out.print("Quantity: ");
        int qty = parseInt(sc.nextLine());
        if (qty <= 0 || qty > p.stock) {
            System.out.println("Invalid quantity. Available stock: " + p.stock);
            return;
        }
        System.out.print("Customer ID (existing or new): ");
        String cid = sc.nextLine().trim();
        Customer c = StoreDB.customers.get(cid);
        if (c == null) {
            System.out.println("New customer details:");
            System.out.print("Name: ");
            String cname = sc.nextLine().trim();
            System.out.print("Phone: ");
            String phone = sc.nextLine().trim();
            c = new Customer(cid, cname, phone);
            StoreDB.customers.put(cid, c);
        }

        // Determine seller id from username role mapping
        String sellerId = sellerIdForUser(username);
        if (sellerId == null) {
            System.out.println("Seller mapping not found. Using S001.");
            sellerId = "S001";
        }

        String saleId = "T" + (StoreDB.sales.size() + 1);
        Sale sale = new Sale(saleId, pid, cid, sellerId, qty, p.price, LocalDateTime.now());
        StoreDB.sales.put(saleId, sale);
        p.stock -= qty;

        double total = qty * p.price;
        System.out.printf("Sale recorded: %s | Total: %.2f%n", saleId, total);
    }

    // Customer info: add/update/list
    private static void customerInfo(Scanner sc, String role) {
        System.out.println("=== Customer Info ===");
        System.out.println("1. List customers");
        System.out.println("2. Add customer");
        System.out.println("3. Update customer");
        System.out.print("Choose: ");
        String ch = sc.nextLine().trim();
        switch (ch) {
            case "1":
                if (StoreDB.customers.isEmpty()) {
                    System.out.println("No customers.");
                } else {
                    StoreDB.customers.values().forEach(c ->
                        System.out.println(c.id + " | " + c.name + " | " + c.phone));
                }
                break;
            case "2":
                System.out.print("Customer ID: ");
                String id = sc.nextLine().trim();
                if (StoreDB.customers.containsKey(id)) {
                    System.out.println("ID exists.");
                    return;
                }
                System.out.print("Name: ");
                String name = sc.nextLine().trim();
                System.out.print("Phone: ");
                String phone = sc.nextLine().trim();
                StoreDB.customers.put(id, new Customer(id, name, phone));
                System.out.println("Added: " + id);
                break;
            case "3":
                System.out.print("Customer ID: ");
                String uid = sc.nextLine().trim();
                Customer c = StoreDB.customers.get(uid);
                if (c == null) {
                    System.out.println("Not found.");
                    return;
                }
                System.out.print("New name (" + c.name + "): ");
                String newName = sc.nextLine().trim();
                if (!newName.isEmpty()) c.name = newName;
                System.out.print("New phone (" + c.phone + "): ");
                String newPhone = sc.nextLine().trim();
                if (!newPhone.isEmpty()) c.phone = newPhone;
                System.out.println("Updated: " + uid);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    // Seller info: list/add/update (admin-only for add/update)
    private static void sellerInfo(Scanner sc, String role) {
        System.out.println("=== Seller Info ===");
        System.out.println("1. List sellers");
        System.out.println("2. Add seller (admin)");
        System.out.println("3. Update seller (admin)");
        System.out.print("Choose: ");
        String ch = sc.nextLine().trim();
        switch (ch) {
            case "1":
                if (StoreDB.sellers.isEmpty()) {
                    System.out.println("No sellers.");
                } else {
                    StoreDB.sellers.values().forEach(s ->
                        System.out.println(s.id + " | " + s.name));
                }
                break;
            case "2":
                if (!"admin".equals(role)) {
                    System.out.println("Permission denied.");
                    return;
                }
                System.out.print("Seller ID: ");
                String id = sc.nextLine().trim();
                if (StoreDB.sellers.containsKey(id)) {
                    System.out.println("ID exists.");
                    return;
                }
                System.out.print("Name: ");
                String name = sc.nextLine().trim();
                StoreDB.sellers.put(id, new Seller(id, name));
                System.out.println("Added: " + id);
                break;
            case "3":
                if (!"admin".equals(role)) {
                    System.out.println("Permission denied.");
                    return;
                }
                System.out.print("Seller ID: ");
                String sid = sc.nextLine().trim();
                Seller s = StoreDB.sellers.get(sid);
                if (s == null) {
                    System.out.println("Not found.");
                    return;
                }
                System.out.print("New name (" + s.name + "): ");
                String newName = sc.nextLine().trim();
                if (!newName.isEmpty()) s.name = newName;
                System.out.println("Updated: " + sid);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void listProducts() {
        System.out.println("=== Product List ===");
        if (StoreDB.products.isEmpty()) {
            System.out.println("No products.");
            return;
        }
        StoreDB.products.values().forEach(p -> System.out.printf(
                "%s | %s | %s | Price: %.2f | Stock: %d%n",
                p.id, p.name, p.category, p.price, p.stock
        ));
    }

    private static void searchProducts(Scanner sc) {
        System.out.println("=== Search Products ===");
        System.out.print("Query (name/category): ");
        String q = sc.nextLine().trim().toLowerCase();
        List<Product> results = StoreDB.products.values().stream()
                .filter(p -> p.name.toLowerCase().contains(q) || p.category.toLowerCase().contains(q))
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            System.out.println("No matches.");
        } else {
            results.forEach(p -> System.out.printf(
                    "%s | %s | %s | Price: %.2f | Stock: %d%n",
                    p.id, p.name, p.category, p.price, p.stock
            ));
        }
    }

    private static void viewSales() {
        System.out.println("=== Sales ===");
        if (StoreDB.sales.isEmpty()) {
            System.out.println("No sales yet.");
            return;
        }
        StoreDB.sales.values().forEach(sale -> {
            TransactionModule.Product p = StoreDB.products.get(sale.productId);
            TransactionModule.Customer c = StoreDB.customers.get(sale.customerId);
            TransactionModule.Seller s = StoreDB.sellers.get(sale.sellerId);
            double total = sale.quantity * sale.unitPrice;
            System.out.printf("%s | %s -> %s | Seller: %s | Qty: %d | Unit: %.2f | Total: %.2f | %s%n",
                    sale.saleId,
                    p != null ? p.name : sale.productId,
                    c != null ? c.name : sale.customerId,
                    s != null ? s.name : sale.sellerId,
                    sale.quantity, sale.unitPrice, total, sale.timestamp);
        });
    }

    // ===== Helpers =====
    private static double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return -1; }
    }
    private static int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }
    private static String sellerIdForUser(String username) {
        // Simple mapping: username to seller by name match, else first seller
        for (Seller s : StoreDB.sellers.values()) {
            if (s.name.equalsIgnoreCase(username)) return s.id;
        }
        return StoreDB.sellers.keySet().stream().findFirst().orElse(null);
    }

    // Standalone runner for quick testing (bypasses LoginModule)
    public static void main(String[] args) {
        StoreDB.seedDemoData();
        Scanner sc = new Scanner(System.in);
        dashboard(sc, "admin", "admin");
        sc.close();
    }
}