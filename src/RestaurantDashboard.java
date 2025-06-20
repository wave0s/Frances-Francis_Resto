package src;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class RestaurantDashboard extends JFrame implements RestaurantFrame {
    private static final int NUM_TABLES = 10;
    private Map<Integer, TablePanel> tables;
    private JPanel tablePanel;
    private JLabel statusLabel;
    private static RestaurantDashboard instance;

    public static RestaurantDashboard getInstance() {
        if (instance == null) {
            instance = new RestaurantDashboard();
        }
        return instance;
    }

    public RestaurantDashboard() {
        setTitle("Frances & Francis - Restaurant Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH); 

        tables = new HashMap<>();
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        tablePanel = new JPanel(new GridLayout(2, 5, 15, 15));
        tablePanel.setBackground(new Color(215, 201, 174));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(tablePanel, BorderLayout.CENTER); 

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        loadTables();

        setLocationRelativeTo(null);
        setResizable(true);
        // updateStats();
    }

    public void updateTableState(int tableNumber, boolean occupied) {
        if (tables.containsKey(tableNumber)) {
            tables.get(tableNumber).setOccupied(occupied);
            // updateStats();
        }
    }

    @Override
    public JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 45, 45));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Frances & Francis - Table Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);


        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }
   
    @Override
    public JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(166, 135, 99));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton backToHomeButton = new JButton("Back to Home");
        backToHomeButton.setBackground(new Color(52, 152, 219));
        backToHomeButton.setForeground(Color.BLACK);
        backToHomeButton.setFont(new Font("Arial", Font.BOLD, 12));
        backToHomeButton.setFocusPainted(false);
        backToHomeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backToHomeButton.addActionListener(e -> {
            this.setVisible(false);
            RestaurantHomePage.getInstance().setVisible(true);
        });

        JButton menuManagementButton = new JButton("Manage Menu");
        menuManagementButton.setBackground(new Color(155, 89, 182));
        menuManagementButton.setForeground(Color.BLUE);
        menuManagementButton.setFont(new Font("Arial", Font.BOLD, 12));
        menuManagementButton.setFocusPainted(false);
        menuManagementButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuManagementButton.addActionListener(e -> {
            this.setVisible(false);
            new MenuManagement().setVisible(true);
        });

        
        JButton checkSales = new JButton("Total sales");
        checkSales.setBackground(new Color(155, 89, 182));
        checkSales.setForeground(Color.BLUE);
        checkSales.setFont(new Font("Arial", Font.BOLD, 12));
        checkSales.setFocusPainted(false);
        checkSales.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        checkSales.addActionListener(e -> {
        try (Connection conn = Database.getConnection();
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery("SELECT SUM(totalBill) AS TotalSales FROM restosales");
            Statement stmt2 = conn.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT name, totalSales FROM menu_item")) {

            double totalBill = 0;
            if (rs1.next()) {
                totalBill = rs1.getDouble("TotalSales");
            }

            StringBuilder message = new StringBuilder("Item Sales:\n");
            while (rs2.next()) {
                String name = rs2.getString("name");
                double itemSales = rs2.getDouble("totalSales");
                message.append(name).append(" = ₱").append(itemSales).append("\n");
            }

            message.append("Total Sales ₱").append(totalBill);

            JOptionPane.showMessageDialog(null, message.toString());

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(166, 135, 99));
        rightPanel.add(backToHomeButton); //added

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(166, 135, 99));
        leftPanel.add(checkSales); //change to left
        leftPanel.add(menuManagementButton);

        footerPanel.add(rightPanel, BorderLayout.EAST);
        footerPanel.add(leftPanel, BorderLayout.WEST);

        return footerPanel;
    }

    public void loadTables() {
        tablePanel.removeAll();
        tables.clear();

        try {
            Database.testConnection();
            Database.synchronizeTableStates();
        
            List<TablePanel> loadedTables = TablePanel.loadFromDatabase(this);
            for (TablePanel t : loadedTables) {
                tables.put(t.getTableNumber(), t);
                tablePanel.add(t);
            }
        
            System.out.println("Loaded " + loadedTables.size() + " tables from database");
        
        } catch (Exception e) {
            System.err.println("Error loading tables: " + e.getMessage());
            e.printStackTrace();
            for (int i = 1; i <= NUM_TABLES; i++) {
                TablePanel table = new TablePanel(i, this);
                tables.put(i, table);
                tablePanel.add(table);
            }
            System.out.println("Created default tables due to database error");
        }

        tablePanel.revalidate();
        tablePanel.repaint();
    }

    public static void launchDashboard() {
        SwingUtilities.invokeLater(() -> {
            RestaurantDashboard dashboard = new RestaurantDashboard();
            dashboard.setVisible(true);
        });
    }

    /* public void updateStats() {
        // Method kept for interface compatibility
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            RestaurantDashboard dashboard = getInstance();
            dashboard.setVisible(true);
        });
    } */

    public static void showDashboard() {
        SwingUtilities.invokeLater(() -> {
            RestaurantDashboard dashboard = getInstance();
            dashboard.setVisible(true);
        });
    }
}
