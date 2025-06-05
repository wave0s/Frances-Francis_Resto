import java.util.ArrayList;
import java.util.List;

public class Order {
    private int tableNumber;
    private List<OrderItem> items;
    private double subtotal;
    private double tax;
    private double total;
    private String status;
    private String orderTime;
    
    public Order(int tableNumber) {
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
        this.status = "In Progress";
        this.orderTime = java.time.LocalTime.now().toString();
    }
    
    public void addItem(String name, double price, int quantity) {
        for (OrderItem item : items) {
            if (item.getName().equals(name)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new OrderItem(name, price, quantity));
    }
    
    public void calculateTotals() {
        subtotal = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        tax = subtotal * 0.12;
        total = subtotal + tax;
    }
    

    public int getTableNumber() { return tableNumber; }
    public List<OrderItem> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getOrderTime() { return orderTime; }
    public void setStatus(String status) { this.status = status; }
    
    public static class OrderItem {
        private String name;
        private double price;
        private int quantity;
        
        public OrderItem(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
        

        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}