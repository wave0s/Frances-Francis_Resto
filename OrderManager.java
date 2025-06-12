import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class OrderManager {
    private static volatile OrderManager instance;
    private Map<Integer, Order> orders;
    
    private OrderManager() {
        orders = new ConcurrentHashMap<>();
        loadActiveOrdersFromDatabase();
    }
    
    public static OrderManager getInstance() {
        if (instance == null) {
            synchronized (OrderManager.class) {
                if (instance == null) {
                    instance = new OrderManager();
                }
            }
        }
        return instance;
    }
    
    private void loadActiveOrdersFromDatabase() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT table_number FROM orders WHERE status = 'In Progress'")) {
            
            while (rs.next()) {
                int tableNumber = rs.getInt("table_number");
                Order order = Database.loadOrderFromDatabase(tableNumber);
                if (order != null) {
                    orders.put(tableNumber, order);
                    System.out.println("Loaded existing order for table " + tableNumber + " - Total: ₱" + String.format("%.2f", order.getTotal()));
                    
                    // Cjeck status
                    Database.updateTableStatus(tableNumber, true);
                }
            }
            
            System.out.println("Loaded " + orders.size() + " active orders from database");
            
        } catch (SQLException e) {
            System.err.println("Failed to load orders from database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public synchronized void createOrder(int tableNumber) {
        try {
            // Create order in database first
            int orderId = Database.createOrder(tableNumber);
            
            // Create order object
            Order order = new Order(tableNumber);
            order.setOrderId(orderId);
            
            // Store in memory
            orders.put(tableNumber, order);
            
            // Update table status in database
            Database.updateTableStatus(tableNumber, true);
            
            System.out.println("Order created for table " + tableNumber + " with ID: " + orderId);
        } catch (SQLException e) {
            System.err.println("Failed to create order in database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Order getOrder(int tableNumber) {
        Order order = orders.get(tableNumber);
        if (order == null) {
            // Try to load from database
            try {
                order = Database.loadOrderFromDatabase(tableNumber);
                if (order != null) {
                    orders.put(tableNumber, order);
                }
            } catch (SQLException e) {
                System.err.println("Failed to load order from database: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return order;
    }
    
    public synchronized void removeOrder(int tableNumber) {
        Order order = orders.remove(tableNumber);
        if (order != null) {
            try {
                // Delete from database
                Database.deleteOrder(tableNumber);
                
                // Update table status
                Database.updateTableStatus(tableNumber, false);
                
                System.out.println("Order removed for table " + tableNumber);
            } catch (SQLException e) {
                System.err.println("Failed to remove order from database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public synchronized void completeOrder(int tableNumber, double paidAmount) {
        Order order = orders.get(tableNumber);
        if (order != null) {
            try {
                // Complete order in database (moves to sales records)
                Database.completeOrder(order.getOrderId(), tableNumber, paidAmount);
                
                // Remove from memory
                orders.remove(tableNumber);
                
                System.out.println("Order completed for table " + tableNumber);
            } catch (SQLException e) {
                System.err.println("Failed to complete order in database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public boolean hasOrder(int tableNumber) {
        return orders.containsKey(tableNumber) || getOrder(tableNumber) != null;
    }
    
    public int getActiveOrderCount() {
        return orders.size();
    }
    
    public void printAllOrders() {
        System.out.println("Active orders: " + orders.keySet());
    }
}
