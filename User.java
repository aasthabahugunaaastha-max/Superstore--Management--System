// Base class User
abstract class User {
    protected String username;
    protected String password;
    protected String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public abstract void login();
    public abstract void viewData();
}

// SuperUser class
class SuperUser extends User {
    public SuperUser(String username, String password) {
        super(username, password, "SuperUser");
    }

    @Override
    public void login() {
        System.out.println("SuperUser logged in.");
    }

    @Override
    public void viewData() {
        System.out.println("Viewing all data (Warehouses and Stores).");
    }
}

// WarehouseAdmin class
class WarehouseAdmin extends User {
    public WarehouseAdmin(String username, String password) {
        super(username, password, "WarehouseAdmin");
    }

    @Override
    public void login() {
        System.out.println("WarehouseAdmin logged in.");
    }

    @Override
    public void viewData() {
        System.out.println("Viewing warehouse data.");
    }

    // Method for warehouse specific actions like adding new items, updating inventory, etc.
    public void manageInventory() {
        System.out.println("Managing warehouse inventory...");
    }
}

// StoreAdmin class
class StoreAdmin extends User {
    public StoreAdmin(String username, String password) {
        super(username, password, "StoreAdmin");
    }

    @Override
    public void login() {
        System.out.println("StoreAdmin logged in.");
    }

    @Override
    public void viewData() {
        System.out.println("Viewing store data.");
    }

    // Method for placing orders to warehouse
    public void placeOrder() {
        System.out.println("Placing order to warehouse...");
    }
}
