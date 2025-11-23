// Alert System (simplified)
class AlertSystem {
    public void displayReorderAlert(Item item) {
        System.out.println("ALERT: Item " + item.getItemName() + " (Code: " + item.getItemCode() + ") needs to be reordered.");
    }
}
