import java.awt.*;
import javax.swing.*;

public class EditOrder extends JFrame {
    private TablePanel tablePanel;
    private RestaurantDashboard parentDashboard;

    public EditOrder(TablePanel tablePanel) {
        this.tablePanel = tablePanel;
        this.parentDashboard = tablePanel.getParentDashboard();
        
        setTitle("Edit Order - Table " + tablePanel.getTableNumber());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(215, 201, 174));

        createHeaderPanel();
        createOrderDetailsPanel();
        createFooterPanel();
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 45, 45));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Edit Order - Table " + tablePanel.getTableNumber(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel statusLabel = new JLabel("Frances & Francis");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(46, 204, 113));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(45, 45, 45));
        rightPanel.add(statusLabel);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createOrderDetailsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(215, 201, 174));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel sectionTitle = new JLabel("Order Details");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(new Color(52, 73, 94));
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JTextArea orderDetails = new JTextArea();
        orderDetails.setLineWrap(true);
        orderDetails.setWrapStyleWord(true);
        orderDetails.setFont(new Font("Arial", Font.PLAIN, 14));
        orderDetails.setBackground(new Color(234, 224, 210));
        orderDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get actual order data from OrderManager
        Order order = OrderManager.getInstance().getOrder(tablePanel.getTableNumber());
        StringBuilder orderText = new StringBuilder();
        
        if (order != null) {
            orderText.append("Current Order for Table ").append(tablePanel.getTableNumber()).append(":\n\n");
            
            // Display each item in the order
            for (Order.OrderItem item : order.getItems()) {
                orderText.append("• ").append(item.getName())
                         .append(" x").append(item.getQuantity())
                         .append(" - Php ").append(String.format("%.2f", item.getPrice() * item.getQuantity()))
                         .append("\n");
            }
            
            orderText.append("\nSubtotal: Php ").append(String.format("%.2f", order.getSubtotal()));
            orderText.append("\nTax (8%): Php ").append(String.format("%.2f", order.getTax()));
            orderText.append("\nTotal: Php ").append(String.format("%.2f", order.getTotal()));
            orderText.append("\n\nOrder Status: ").append(order.getStatus());
            orderText.append("\nOrder Time: ").append(order.getOrderTime());
        } else {
            orderText.append("No order found for Table ").append(tablePanel.getTableNumber()).append(".\n\n");
            orderText.append("This table appears to be occupied but no order data is available.");
        }

        orderDetails.setText(orderText.toString());
        orderDetails.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(orderDetails);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        scrollPane.setBackground(new Color(215, 201, 174));

        mainPanel.add(sectionTitle, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(166, 135, 99));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(166, 135, 99));

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(231, 71, 60));
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        cancelBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                RestaurantDashboard newDashboard = new RestaurantDashboard();
                newDashboard.setVisible(true);
            });
        });

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setBackground(new Color(52, 152, 219));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 12));
        saveBtn.setFocusPainted(false);
        saveBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Order changes saved successfully!", 
                "Changes Saved", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> {
                RestaurantDashboard newDashboard = new RestaurantDashboard();
                newDashboard.setVisible(true);
            });
        });

        JButton confirmBtn = new JButton("Confirm & Complete Order");
        confirmBtn.setBackground(new Color(60, 231, 74));
        confirmBtn.setForeground(Color.BLACK);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        confirmBtn.addActionListener(e -> confirmOrder());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(confirmBtn);

        footerPanel.add(buttonPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void confirmOrder() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to complete this order?\n",
                "Confirm Order Completion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                "Order completed successfully!\n" +
                "Table " + tablePanel.getTableNumber() + " is now available.",
                "Order Completed",
                JOptionPane.INFORMATION_MESSAGE);
            
            tablePanel.setOccupied(false);
            OrderManager.getInstance().removeOrder(tablePanel.getTableNumber());

            dispose();
            
            SwingUtilities.invokeLater(() -> {
                RestaurantDashboard newDashboard = new RestaurantDashboard();
                newDashboard.updateTableState(tablePanel.getTableNumber(), false);
                newDashboard.setVisible(true);
            });
        }
    }
}