import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class TablePanel extends JPanel {
    private int tableNumber;
    private boolean occupied;
    private JLabel tableLabel;
    private JLabel statusLabel;
    private JButton toggleButton;
    private JButton cancelButton;
    private RestaurantFrame parentFrame; 
    
    private RestaurantDashboard restoDashboard;

    public TablePanel(int tableNumber, RestaurantFrame parent) { 
        
        this.tableNumber = tableNumber;
        this.occupied = false;
        this.parentFrame = parent;
        this.restoDashboard = null; 

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(140, 120));
        setOpaque(false);

        createComponents();
        updateAppearance();
    }


    public TablePanel(int tableNumber, RestaurantDashboard parent) {
        this(tableNumber, (RestaurantFrame) parent);
    }

    public static List<TablePanel> loadFromDatabase(RestaurantFrame frame) { 
        List<TablePanel> tables = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT table_number, occupied FROM tables ORDER BY table_number")) {
            
            while (rs.next()) {
                int tableNumber = rs.getInt("table_number");
                boolean occupied = rs.getBoolean("occupied");
            
                TablePanel table = new TablePanel(tableNumber, frame);
            
                // Double-check if there's an active order for this table
                if (OrderManager.getInstance().hasOrder(tableNumber)) {
                    occupied = true;
                    // Update database to match
                    try {
                        Database.updateTableStatus(tableNumber, true);
                    } catch (SQLException e) {
                        System.err.println("Failed to update table status: " + e.getMessage());
                    }
                }
            
                table.setOccupied(occupied);
                tables.add(table);
            
                System.out.println("Loaded table " + tableNumber + " - " + (occupied ? "OCCUPIED" : "AVAILABLE"));
            }
        
        } catch (SQLException e) {
            System.err.println("Error loading tables from database: " + e.getMessage());
            e.printStackTrace();
            // Create def tables if database fails
            for (int i = 1; i <= 10; i++) {
                tables.add(new TablePanel(i, frame));
            }
        }
        return tables;
    }

    public static List<TablePanel> loadFromDatabase(RestaurantDashboard dashboard) {
        return loadFromDatabase((RestaurantFrame) dashboard);
    }

    private void createComponents() {
        tableLabel = new JLabel("Table " + tableNumber, SwingConstants.CENTER);
        tableLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tableLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        toggleButton = new JButton();
        toggleButton.setFont(new Font("Arial", Font.BOLD, 10));
        toggleButton.setFocusPainted(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        toggleButton.addActionListener(e -> {
            if (occupied) {
                showEditOrder();
            } else {
                showOrderMenu();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 10));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        cancelButton.addActionListener(e -> cancelOrder());
        cancelButton.setVisible(false); 

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(statusLabel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(toggleButton);

        centerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(tableLabel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void cancelOrder() {
        int confirm = JOptionPane.showConfirmDialog((Component) parentFrame,
                "Wanna cancel the order for Table " + tableNumber + "?",
                "Confirm Cancel Order",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            occupied = false;
            OrderManager.getInstance().removeOrder(tableNumber);
            updateAppearance();
            parentFrame.updateStats();
        }
    }

    private void updateAppearance() {
        if (occupied) {
            setBackground(new Color(234, 224, 210));
            tableLabel.setForeground(Color.BLACK);
            statusLabel.setText("OCCUPIED");
            statusLabel.setForeground(Color.BLACK);
            toggleButton.setText("Bill Out");
            toggleButton.setBackground(new Color(231, 71, 60));
            toggleButton.setForeground(Color.BLACK);
            cancelButton.setVisible(true);
        } else {
            setBackground(new Color(234, 224, 210));
            tableLabel.setForeground(Color.BLACK);
            statusLabel.setText("AVAILABLE");
            statusLabel.setForeground(Color.BLACK);
            toggleButton.setText("Place Order");
            toggleButton.setBackground(new Color(60, 231, 74));
            toggleButton.setForeground(Color.BLACK);
            cancelButton.setVisible(false);
        }

        tableLabel.setOpaque(false);
        statusLabel.setOpaque(false);
        setOpaque(true);
        repaint();
    }

    // Modify the showOrderMenu methd
    private void showOrderMenu() {
        ((JFrame) parentFrame).setVisible(false);
        
        // Create a temp TablePanel that references the dashboard
        RestaurantDashboard dashboard = RestaurantDashboard.getInstance();
        TablePanel tempTablePanel = new TablePanel(this.tableNumber, dashboard);
        tempTablePanel.setOccupied(this.occupied);
        
        OrderMenu orderMenu = new OrderMenu(tempTablePanel, dashboard);
        orderMenu.setVisible(true);
    }

    // Mmodify the showEditOrder method
    private void showEditOrder() {
        ((JFrame) parentFrame).setVisible(false);
        
        RestaurantDashboard dashboard = RestaurantDashboard.getInstance();
        TablePanel tempTablePanel = new TablePanel(this.tableNumber, dashboard);
        tempTablePanel.setOccupied(this.occupied);
        
        EditOrderWPayment editOrder = new EditOrderWPayment(tempTablePanel);
        editOrder.setVisible(true);
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getTableNumber() {
        return tableNumber;
    }
    
    public RestaurantFrame getParentFrame() {
        return parentFrame;
    }

    public RestaurantDashboard getParentDashboard() {
        if (parentFrame instanceof RestaurantDashboard) {
            return (RestaurantDashboard) parentFrame;
        }
        return null;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
        updateAppearance();
    }
}
