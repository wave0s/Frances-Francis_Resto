
import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/restaurantdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to MySQL successfully!");

            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "menu_item", null);
            if (!tables.next()) {
                System.out.println("Warning: menu_item table not found. Please create database schema.");
            }

        } catch (SQLException e) {
            System.err.println("Database connection test failed:");
            e.printStackTrace();
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

                
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tables (
                    table_number INT PRIMARY KEY,
                    occupied BOOLEAN DEFAULT FALSE
                )
            """);

            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS menu_item (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    category VARCHAR(50) NOT NULL
                )
            """);

            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    order_id INT AUTO_INCREMENT PRIMARY KEY,
                    table_number INT NOT NULL,
                    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status VARCHAR(50) DEFAULT 'In Progress',
                    subtotal DECIMAL(10,2) DEFAULT 0.00,
                    tax DECIMAL(10,2) DEFAULT 0.00,
                    total DECIMAL(10,2) DEFAULT 0.00,
                    FOREIGN KEY (table_number) REFERENCES tables(table_number)
                )
            """);

            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    item_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT NOT NULL,
                    menu_item_id INT NOT NULL,
                    item_name VARCHAR(100) NOT NULL,
                    item_price DECIMAL(10,2) NOT NULL,
                    quantity INT NOT NULL,
                    item_total DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                    FOREIGN KEY (menu_item_id) REFERENCES menu_item(id)
                )
            """);

            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS restosales (
                    sale_id INT AUTO_INCREMENT PRIMARY KEY,
                    tableNum INT NOT NULL,
                    customerOrder TEXT NOT NULL,
                    totalBill DECIMAL(10,2) NOT NULL,
                    paidAmount DECIMAL(10,2) NOT NULL,
                    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            
            try {
                stmt.execute("ALTER TABLE menu_item ADD COLUMN totalSales DECIMAL(10,2) DEFAULT 0.00");
                System.out.println("Added totalSales column to menu_item table.");
            } catch (SQLException e) {
                
                if (!e.getMessage().contains("Duplicate column name")) {
                    System.err.println("Error adding totalSales column: " + e.getMessage());
                }
            }

            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tables");
            rs.next();
            if (rs.getInt(1) == 0) {
                for (int i = 1; i <= 10; i++) {
                    stmt.execute("INSERT INTO tables (table_number, occupied) VALUES (" + i + ", FALSE)");
                }
                System.out.println("Default tables created");
            }

            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM menu_item");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO menu_item (name, price, category) VALUES " +
                    "('Adobo', 150.00, 'Main Course'), " +
                    "('Sinigang', 180.00, 'Main Course'), " +
                    "('Lechon Kawali', 220.00, 'Main Course'), " +
                    "('Pancit Canton', 120.00, 'Noodles'), " +
                    "('Lumpia', 80.00, 'Appetizer'), " +
                    "('Rice', 25.00, 'Sides'), " +
                    "('Halo-Halo', 95.00, 'Dessert'), " +
                    "('Buko Juice', 45.00, 'Drinks'), " +
                    "('Softdrinks', 35.00, 'Drinks')");
                System.out.println("Sample menu items created");
            }

            System.out.println("Database initialized successfully");

        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public static int createOrder(int tableNumber) throws SQLException {
        String sql = "INSERT INTO orders (table_number, status, subtotal, tax, total) VALUES (?, 'In Progress', 0.00, 0.00, 0.00)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, tableNumber);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int orderId = rs.getInt(1);
                System.out.println("Order created with ID: " + orderId + " for table " + tableNumber);
                return orderId;
            }
            throw new SQLException("Failed to get generated order ID");
        }
    }

    public static void addOrderItem(int orderId, String itemName, double itemPrice, int quantity) throws SQLException {
        // First, get the menu item ID
        int menuItemId = getMenuItemId(itemName);
        double itemTotal = itemPrice * quantity;

        String sql = "INSERT INTO order_items (order_id, menu_item_id, item_name, item_price, quantity, item_total) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, menuItemId);
            pstmt.setString(3, itemName);
            pstmt.setDouble(4, itemPrice);
            pstmt.setInt(5, quantity);
            pstmt.setDouble(6, itemTotal);

            pstmt.executeUpdate();
            System.out.println("Added item: " + itemName + " x" + quantity + " to order " + orderId);
        }
    }

    public static void updateOrderTotals(int orderId, double subtotal, double tax, double total) throws SQLException {
        String sql = "UPDATE orders SET subtotal = ?, tax = ?, total = ? WHERE order_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, subtotal);
            pstmt.setDouble(2, tax);
            pstmt.setDouble(3, total);
            pstmt.setInt(4, orderId);

            pstmt.executeUpdate();
            System.out.println("Updated totals for order " + orderId);
        }
    }

    public static Order loadOrderFromDatabase(int tableNumber) throws SQLException {
        String orderSql = "SELECT order_id, table_number, order_time, status, subtotal, tax, total FROM orders WHERE table_number = ? AND status = 'In Progress'";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(orderSql)) {

            pstmt.setInt(1, tableNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int orderId = rs.getInt("order_id");
                String orderTime = rs.getString("order_time");
                String status = rs.getString("status");
                double subtotal = rs.getDouble("subtotal");
                double tax = rs.getDouble("tax");
                double total = rs.getDouble("total");

                Order order = new Order(tableNumber);
                order.setOrderId(orderId);
                order.setOrderTime(orderTime);
                order.setStatus(status);

                String itemsSql = "SELECT item_name, item_price, quantity FROM order_items WHERE order_id = ?";
                try (PreparedStatement itemsPstmt = conn.prepareStatement(itemsSql)) {
                    itemsPstmt.setInt(1, orderId);
                    ResultSet itemsRs = itemsPstmt.executeQuery();

                    while (itemsRs.next()) {
                        String itemName = itemsRs.getString("item_name");
                        double itemPrice = itemsRs.getDouble("item_price");
                        int quantity = itemsRs.getInt("quantity");

                        order.addItemWithoutCalculation(itemName, itemPrice, quantity);
                    }
                }

                order.setTotals(subtotal, tax, total);
                return order;
            }
            return null;
        }
    }

    public static void completeOrder(int orderId, int tableNumber, double paidAmount) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); 

            String getOrderSql = "SELECT subtotal, tax, total FROM orders WHERE order_id = ?";
            double subtotal = 0, tax = 0, total = 0;

            try (PreparedStatement pstmt = conn.prepareStatement(getOrderSql)) {
                pstmt.setInt(1, orderId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    subtotal = rs.getDouble("subtotal");
                    tax = rs.getDouble("tax");
                    total = rs.getDouble("total");
                }
            }

            StringBuilder orderItems = new StringBuilder();
            String getItemsSql = "SELECT item_name, quantity, item_price FROM order_items WHERE order_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(getItemsSql)) {
                pstmt.setInt(1, orderId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    int quantity = rs.getInt("quantity");
                    double itemPrice = rs.getDouble("item_price");
                    double itemTotal = itemPrice * quantity;

                    if (orderItems.length() > 0) orderItems.append(", ");
                    orderItems.append(itemName)
                             .append(" x").append(quantity)
                             .append(" (₱").append(itemPrice).append(")");


                    updateMenuItemSalesInTransaction(conn, itemName, itemTotal);
                }
            }

            String insertSalesSql = "INSERT INTO restosales (tableNum, customerOrder, totalBill, paidAmount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSalesSql)) {
                pstmt.setInt(1, tableNumber);
                pstmt.setString(2, orderItems.toString());
                pstmt.setDouble(3, total);
                pstmt.setDouble(4, paidAmount);
                pstmt.executeUpdate();
            }

        
            String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteItemsSql)) {
                pstmt.setInt(1, orderId);
                pstmt.executeUpdate();
            }


            String deleteOrderSql = "DELETE FROM orders WHERE order_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteOrderSql)) {
                pstmt.setInt(1, orderId);
                pstmt.executeUpdate();
            }


            updateTableStatusInTransaction(conn, tableNumber, false);

            conn.commit(); 
            System.out.println("Order completed and moved to sales records");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); 
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void updateMenuItemSalesInTransaction(Connection conn, String itemName, double salesAmount) throws SQLException {
        String sql = "UPDATE menu_item SET totalSales = totalSales + ? WHERE name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, salesAmount);
            pstmt.setString(2, itemName);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Updated sales for " + itemName + ": +₱" + String.format("%.2f", salesAmount));
            }
        }
    }

    private static void updateTableStatusInTransaction(Connection conn, int tableNumber, boolean occupied) throws SQLException {
        String sql = "UPDATE tables SET occupied = ? WHERE table_number = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, occupied);
            pstmt.setInt(2, tableNumber);
            pstmt.executeUpdate();

            System.out.println("Table " + tableNumber + " status updated to: " + (occupied ? "occupied" : "available"));
        }
    }

    public static void deleteOrder(int tableNumber) throws SQLException {
        String getOrderIdSql = "SELECT order_id FROM orders WHERE table_number = ? AND status = 'In Progress'";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getOrderIdSql)) {

            pstmt.setInt(1, tableNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int orderId = rs.getInt("order_id");

                String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
                try (PreparedStatement deleteItemsPstmt = conn.prepareStatement(deleteItemsSql)) {
                    deleteItemsPstmt.setInt(1, orderId);
                    deleteItemsPstmt.executeUpdate();
                }

                String deleteOrderSql = "DELETE FROM orders WHERE order_id = ?";
                try (PreparedStatement deleteOrderPstmt = conn.prepareStatement(deleteOrderSql)) {
                    deleteOrderPstmt.setInt(1, orderId);
                    deleteOrderPstmt.executeUpdate();
                }

                System.out.println("Order deleted for table " + tableNumber);
            }
        }
    }

    public static void updateTableStatus(int tableNumber, boolean occupied) throws SQLException {
        String sql = "UPDATE tables SET occupied = ? WHERE table_number = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, occupied);
            pstmt.setInt(2, tableNumber);
            pstmt.executeUpdate();

            System.out.println("Table " + tableNumber + " status updated to: " + (occupied ? "occupied" : "available"));
        }
    }

    private static int getMenuItemId(String itemName) throws SQLException {
        String sql = "SELECT id FROM menu_item WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, itemName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Menu item not found: " + itemName);
        }
    }

    public static void synchronizeTableStates() throws SQLException {
        try (Connection conn = getConnection()) {
            // First, set all tables to available
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE tables SET occupied = FALSE");
            }

            String sql = "UPDATE tables SET occupied = TRUE WHERE table_number IN (SELECT DISTINCT table_number FROM orders WHERE status = 'In Progress')";
            try (Statement stmt = conn.createStatement()) {
                int updated = stmt.executeUpdate(sql);
                System.out.println("Synchronized " + updated + " table states with active orders");
            }
        }
    }
}
