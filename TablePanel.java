import java.awt.*;
import javax.swing.*;

public class TablePanel extends JPanel {
    private int tableNumber;
    private boolean occupied;
    private JLabel tableLabel;
    private JLabel statusLabel;
    private JButton toggleButton;
    private JButton cancelButton;
    private RestaurantDashboard parentDashboard;


    public TablePanel(int tableNumber, RestaurantDashboard parent) {
        this.tableNumber = tableNumber;
        this.occupied = false;
        this.parentDashboard = parent;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(140, 120));
        setOpaque(false);

        createComponents();
        updateAppearance();
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
        int confirm = JOptionPane.showConfirmDialog(parentDashboard,
                "Wanna cancel the order for Table " + tableNumber + "?",
                "Confirm Cancel Order",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            occupied = false;
            updateAppearance();
            parentDashboard.updateStats();
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

    private void showOrderMenu() {
        OrderMenu orderMenu = new OrderMenu(this, parentDashboard);
        orderMenu.isVisible();
        
        orderMenu.setVisible(true);
    }

    private void showEditOrder() {
        EditOrder editOrder = new EditOrder(this);
        editOrder.setVisible(true);
    }

    public boolean isOccupied() {
        return occupied;
    }

    public int getTableNumber() {
        return tableNumber;
    }
    public RestaurantDashboard getParentDashboard() {
    return parentDashboard;
}

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
        updateAppearance();
    }

    
}
