package store;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ReportingModule {

    public static void showReportsMenu() {
        System.out.println("\n=== Reporting ===");
        System.out.println("1. Daily revenue");
        System.out.println("2. Top-selling products");
        System.out.println("3. Sales by seller");
        System.out.println("4. Inventory below threshold");
        System.out.println("5. Back");
    }

    public static void handleReports(Scanner sc) {
        while (true) {
            showReportsMenu();
            System.out.print("Choose: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": dailyRevenue(); break;
                case "2": topSellingProducts(5); break;
                case "3": salesBySeller(); break;
                case "4":
                    System.out.print("Threshold: ");
                    int t = parseInt(sc.nextLine());
                    inventoryBelowThreshold(t <= 0 ? 5 : t);
                    break;
                case "5": return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    // ==== Reports ====

    // 1) Daily revenue: sum of total per day
    public static void dailyRevenue() {
        Map<LocalDate, Double> revenueByDay = new TreeMap<>();
        for (TransactionModule.Sale s : TransactionModule.StoreDB.sales.values()) {
            LocalDate d = s.timestamp.toLocalDate();
            double total = s.unitPrice * s.quantity;
            revenueByDay.merge(d, total, Double::sum);
        }
        System.out.println("=== Daily Revenue ===");
        if (revenueByDay.isEmpty()) {
            System.out.println("No sales.");
            return;
        }
        revenueByDay.forEach((d, sum) -> System.out.printf("%s | %.2f%n", d, sum));
    }

    // 2) Top-selling products by quantity
    public static void topSellingProducts(int topN) {
        Map<String, Integer> qtyByProduct = new HashMap<>();
        for (TransactionModule.Sale s : TransactionModule.StoreDB.sales.values()) {
            qtyByProduct.merge(s.productId, s.quantity, Integer::sum);
        }
        List<Map.Entry<String, Integer>> sorted = qtyByProduct.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .collect(Collectors.toList());

        System.out.println("=== Top Selling Products ===");
        if (sorted.isEmpty()) {
            System.out.println("No sales.");
            return;
        }
        for (Map.Entry<String, Integer> e : sorted) {
            TransactionModule.Product p = TransactionModule.StoreDB.products.get(e.getKey());
            System.out.printf("%s | %s | Qty: %d%n",
                    e.getKey(), p != null ? p.name : "(unknown)", e.getValue());
        }
    }

    // 3) Sales by seller
    public static void salesBySeller() {
        Map<String, Double> revenueBySeller = new HashMap<>();
        for (TransactionModule.Sale s : TransactionModule.StoreDB.sales.values()) {
            double total = s.unitPrice * s.quantity;
            revenueBySeller.merge(s.sellerId, total, Double::sum);
        }
        System.out.println("=== Sales By Seller ===");
        if (revenueBySeller.isEmpty()) {
            System.out.println("No sales.");
            return;
        }
        for (Map.Entry<String, Double> e : revenueBySeller.entrySet()) {
            TransactionModule.Seller sel = TransactionModule.StoreDB.sellers.get(e.getKey());
            System.out.printf("%s | %s | Revenue: %.2f%n",
                    e.getKey(), sel != null ? sel.name : "(unknown)", e.getValue());
        }
    }

    // 4) Inventory below threshold
    public static void inventoryBelowThreshold(int threshold) {
        System.out.println("=== Low Inventory (<= " + threshold + ") ===");
        List<TransactionModule.Product> low = TransactionModule.StoreDB.products.values().stream()
                .filter(p -> p.stock <= threshold)
                .collect(Collectors.toList());
        if (low.isEmpty()) {
            System.out.println("All good.");
            return;
        }
        for (TransactionModule.Product p : low) {
            System.out.printf("%s | %s | Stock: %d%n", p.id, p.name, p.stock);
        }
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return -1; }
    }

    // Standalone runner for quick testing (assumes StoreDB seeded elsewhere)
    public static void main(String[] args) {
        TransactionModule.StoreDB.seedDemoData();
        // Simulate a sale to have some data
        TransactionModule.Product p = TransactionModule.StoreDB.products.get("P100");
        TransactionModule.Customer c = TransactionModule.StoreDB.customers.get("C001");
        TransactionModule.Seller sel = TransactionModule.StoreDB.sellers.get("S001");
        String saleId = "T" + (TransactionModule.StoreDB.sales.size() + 1);
        TransactionModule.StoreDB.sales.put(saleId, new TransactionModule.Sale(
                saleId, p.id, c.id, sel.id, 2, p.price, java.time.LocalDateTime.now()
        ));

        handleReports(new java.util.Scanner(System.in));
    }
}
