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

        tableLabel = new JLabel("#" + tableNumber, SwingConstants.CENTER);
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
            parentDashboard.setVisible(false);
            new OrderMenu(this, parentDashboard).setVisible(true);
        } else {
            String[] options = {"Edit Order", "Complete Order", "Cancel"};
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
                case 0: // Edit Order - Now opens OrderMenu in edit mode
                    parentDashboard.setVisible(false);
                    new OrderMenu(this, parentDashboard, true).setVisible(true); // true indicates edit mode
                    break;
                case 1: // Complete Order
                    parentDashboard.setVisible(false);
                    new EditOrderWPayment(this).setVisible(true);
                    break;
                case 2: // Cancel
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
            statusLabel.setText("Occupied");
            statusLabel.setBackground(new Color(231, 71, 60));
            statusLabel.setForeground(Color.WHITE); // White text
            actionButton.setText("View Order");
            actionButton.setBackground(new Color(255, 193, 7));
            actionButton.setForeground(Color.BLACK);
        } else {
            statusLabel.setText("Available");
            statusLabel.setBackground(new Color(60, 231, 74));
            statusLabel.setForeground(Color.BLACK); // Black text
            actionButton.setText("Take Order");
            actionButton.setBackground(new Color(52, 152, 219));
            actionButton.setForeground(Color.BLACK);
        }

        tableLabel.setForeground(Color.BLACK);
    }

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

        if (tables.isEmpty()) {
            for (int i = 1; i <= 10; i++) {
                TablePanel table = new TablePanel(i, dashboard);
                tables.add(table);
            }
        }

        return tables;
    }
}
