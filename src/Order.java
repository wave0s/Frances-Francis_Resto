package src;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private static final double TAX_RATE = 0.12;
    private int tableNumber;
    private List<OrderItem> items;
    private double subtotal;
    private double tax;
    private double total;
    private String status;
    private String orderTime;
    private int orderId = -1;
    
    // Constructor for tableNum
    public Order(int tableNumber) {
        if (tableNumber <= 0) {
            throw new IllegalArgumentException("Table number must be positive");
        }
        this.tableNumber = tableNumber;
        this.items = new ArrayList<>();
        this.status = "In Progress";
        this.orderTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public void addItem(String name, double price, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        for (OrderItem item : items) {
            if (item.getName().equals(name)) {
                item.setQuantity(item.getQuantity() + quantity);
                calculateTotals(); 
                
                // Save to database if order has an ID
                if (orderId != -1) {
                    try {
                        Database.updateOrderTotals(orderId, subtotal, tax, total);
                    } catch (SQLException e) {
                        System.err.println("Failed to update order totals in database: " + e.getMessage());
                    }
                }
                return;
            }
        }
        items.add(new OrderItem(name, price, quantity));
        calculateTotals();
        
        // Save to database if order has an id
        if (orderId != -1) {
            try {
                Database.addOrderItem(orderId, name, price, quantity);
                Database.updateOrderTotals(orderId, subtotal, tax, total);
            } catch (SQLException e) {
                System.err.println("Failed to save order item to database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public void calculateTotals() {
        BigDecimal subtotalBD = BigDecimal.ZERO;
        
        for (OrderItem item : items) {
            BigDecimal itemTotal = BigDecimal.valueOf(item.getPrice())
                .multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotalBD = subtotalBD.add(itemTotal);
        }
        
        BigDecimal taxBD = subtotalBD.multiply(BigDecimal.valueOf(TAX_RATE));
        BigDecimal totalBD = subtotalBD.add(taxBD);
        
        this.subtotal = subtotalBD.setScale(2, RoundingMode.HALF_UP).doubleValue();
        this.tax = taxBD.setScale(2, RoundingMode.HALF_UP).doubleValue();
        this.total = totalBD.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    
    // Method to add items 
    public void addItemWithoutCalculation(String name, double price, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        // Check if item already exists
        for (OrderItem item : items) {
            if (item.getName().equals(name)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new OrderItem(name, price, quantity));
    }
    
    // Getters
    public int getTableNumber() { return tableNumber; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public String getOrderTime() { return orderTime; }
    public double getTaxRate() { return TAX_RATE; }
    public int getOrderId() { return orderId; }
    
    // Setters
    public void setStatus(String status) { 
        if (status != null && !status.trim().isEmpty()) {
            this.status = status; 
        }
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
    
    public void setTotals(double subtotal, double tax, double total) {
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clearItems() {
    }

    public static class OrderItem {
        private String name;
        private double price;
        private int quantity;
        
        public OrderItem(String name, double price, int quantity) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Item name cannot be empty");
            }
            if (price < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
        
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        
        public void setQuantity(int quantity) { 
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            this.quantity = quantity; 
        }
        
        public double getItemTotal() {
            return price * quantity;
        }
    }
}
