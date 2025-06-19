package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TablePanel extends JPanel {
    private int tableNumber;
    private JLabel tableLabel;
    private JLabel statusLabel;
    private JButton actionButton;
    private boolean occupied;
    private RestaurantDashboard parentDashboard;

    public TablePanel(int tableNumber, RestaurantDashboard parentDashboard) {
        this.tableNumber = tableNumber;
        this.parentDashboard = parentDashboard;
        this.occupied = false;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(150, 120));
        setBorder(BorderFactory.createRaisedBevelBorder());

        // Set neutral background for the entire panel
        setBackground(new Color(234, 224, 210));

        tableLabel = new JLabel("" + tableNumber, SwingConstants.CENTER);
        tableLabel.setFont(new Font("Arial", Font.BOLD, 16));
        tableLabel.setForeground(Color.BLACK);

        statusLabel = new JLabel("Available", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setOpaque(true); // Make label background visible
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        actionButton = new JButton("Take Order");
        actionButton.setFont(new Font("Arial", Font.BOLD, 10));
        actionButton.setFocusPainted(false);
        actionButton.setBackground(new Color(52, 152, 219));
        actionButton.setForeground(Color.BLACK);
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleButtonClick();
            }
        });

        add(tableLabel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.CENTER);
        add(actionButton, BorderLayout.SOUTH);

        updateAppearance();
    }

    public RestaurantDashboard getParentDashboard() {
        return parentDashboard;
    }

    private void handleButtonClick() {
        if (!occupied) {
            // Table is available - open order menu
            parentDashboard.setVisible(false);
            new OrderMenu(this, parentDashboard).setVisible(true);
        } else {
            // Table is occupied - show options
            String[] options = {"Complete Order", "Cancel"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Table " + tableNumber + " is currently occupied.\nWhat would you like to do?",
                    "Table Options",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            switch (choice) {
                case 0: // Complete Order
                    parentDashboard.setVisible(true);
                    new EditOrderWPayment(this).setVisible(true);
                    break;
                case 1: // Cancel
                default:
                    // Do nothing
                    break;
            }
        }
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
        updateAppearance();

        // Update database
        try {
            Database.updateTableStatus(tableNumber, occupied);
        } catch (SQLException e) {
            System.err.println("Failed to update table status in database: " + e.getMessage());
        }
    }

    private void updateAppearance() {
        if (occupied) {
            // Only highlight the status label in red
            statusLabel.setText("Occupied");
            statusLabel.setBackground(new Color(231, 71, 60)); // Red background
            statusLabel.setForeground(Color.WHITE); // White text
            actionButton.setText("View Order");
            actionButton.setBackground(new Color(255, 193, 7)); // Yellow for occupied table button
            actionButton.setForeground(Color.BLACK);
        } else {
            // Only highlight the status label in green
            statusLabel.setText("Available");
            statusLabel.setBackground(new Color(60, 231, 74)); // Green background
            statusLabel.setForeground(Color.BLACK); // Black text
            actionButton.setText("Take Order");
            actionButton.setBackground(new Color(52, 152, 219)); // Blue for available table button
            actionButton.setForeground(Color.BLACK);
        }

        // Keep table label and panel background neutral
        tableLabel.setForeground(Color.BLACK);
    }

    // Static method to load tables from database
    public static List<TablePanel> loadFromDatabase(RestaurantDashboard dashboard) throws SQLException {
        List<TablePanel> tables = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT table_number, occupied FROM tables ORDER BY table_number")) {

            while (rs.next()) {
                int tableNumber = rs.getInt("table_number");
                boolean occupied = rs.getBoolean("occupied");

                TablePanel table = new TablePanel(tableNumber, dashboard);
                table.setOccupied(occupied);
                tables.add(table);
            }
        }

        // If no tables in database, create default tables
        if (tables.isEmpty()) {
            for (int i = 1; i <= 10; i++) {
                TablePanel table = new TablePanel(i, dashboard);
                tables.add(table);
            }
        }

        return tables;
    }
}