import java.util.HashMap;
import java.util.Map;

public class OrderManager {
    private static OrderManager instance;
    private Map<Integer, Order> orders;
    
    private OrderManager() {
        orders = new HashMap<>();
    }
    
    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }
    
    public void createOrder(int tableNumber) {
        orders.put(tableNumber, new Order(tableNumber));
    }
    
    public Order getOrder(int tableNumber) {
        return orders.get(tableNumber);
    }
    
    public void removeOrder(int tableNumber) {
        orders.remove(tableNumber);
    }
    
    public boolean hasOrder(int tableNumber) {
        return orders.containsKey(tableNumber);
    }
}