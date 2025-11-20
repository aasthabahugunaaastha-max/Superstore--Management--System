package store;

import java.util.Scanner;

public class LoginModule {
    // Simple role-based login against users stored in TransactionModule.StoreDB
    public static class AuthResult {
        public final boolean success;
        public final String username;
        public final String role;

        public AuthResult(boolean success, String username, String role) {
            this.success = success;
            this.username = username;
            this.role = role;
        }
    }

    public static AuthResult loginPrompt(Scanner sc) {
        System.out.println("=== Login ===");
        System.out.print("Username: ");
        String user = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        TransactionModule.User u = TransactionModule.StoreDB.users.get(user);
        if (u != null && u.password.equals(pass)) {
            System.out.println("Login successful. Role: " + u.role);
            return new AuthResult(true, u.username, u.role);
        }
        System.out.println("Invalid credentials.");
        return new AuthResult(false, null, null);
    }

    // Optional: programmatic login (useful for tests)
    public static AuthResult login(String username, String password) {
        TransactionModule.User u = TransactionModule.StoreDB.users.get(username);
        if (u != null && u.password.equals(password)) {
            return new AuthResult(true, u.username, u.role);
        }
        return new AuthResult(false, null, null);
    }

    // Example main to demo login flow then jump to dashboard
    public static void main(String[] args) {
        // Seed demo data
        TransactionModule.StoreDB.seedDemoData();

        Scanner sc = new Scanner(System.in);
        AuthResult auth = loginPrompt(sc);
        if (auth.success) {
            TransactionModule.dashboard(sc, auth.username, auth.role);
        }
        sc.close();
    }
}