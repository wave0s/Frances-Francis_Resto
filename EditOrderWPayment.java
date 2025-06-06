import java.awt.*;
import javax.swing.*;


public class EditOrderWPayment extends JFrame {
    private TablePanel tablePanel;
    private RestaurantDashboard parentDashboard;
    private JTextField paymentField = new JTextField();
    private JLabel change = new JLabel("Change: P 0.00");
    private Order order;
   


    public EditOrderWPayment(TablePanel tablePanel) {
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
        order = OrderManager.getInstance().getOrder(tablePanel.getTableNumber());
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
   
     
       
        JPanel paymentPanel = new JPanel();
        paymentPanel.setPreferredSize(new Dimension(350, 100));
        paymentPanel.setBackground(new Color(234, 224, 210));
        paymentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));






        JLabel PaymentTitle = new JLabel("Payment for Table " + tablePanel.getTableNumber());
        JLabel lines = new JLabel("=========================================");
        JLabel dash = new JLabel("---------------------------------------------------------------------------------");
        JLabel totalLabel = new JLabel("Total: P " + String.format("%.2f", order.getTotal()));
        JLabel Cash = new JLabel(" Enter Payment Amount:");
 


        PaymentTitle.setFont(new Font("Arial", Font.BOLD, 15));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        paymentField.setMaximumSize(new Dimension(300,30));
        change.setFont(new Font("Arial", Font.BOLD, 12));
       
        paymentPanel.add(PaymentTitle);
        paymentPanel.add(dash);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(totalLabel);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(change);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(lines);
        paymentPanel.add(Box.createVerticalStrut(20));
        paymentPanel.add(Cash);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(paymentField);


        orderDetails.setText(orderText.toString());
        orderDetails.setEditable(false);


        JScrollPane scrollPane = new JScrollPane(orderDetails);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        scrollPane.setBackground(new Color(215, 201, 174));


        JPanel thePanels = new JPanel(new BorderLayout());
        thePanels.add(scrollPane, BorderLayout.CENTER);
        thePanels.add(paymentPanel, BorderLayout.EAST);


        mainPanel.add(sectionTitle, BorderLayout.NORTH);
        mainPanel.add(thePanels, BorderLayout.CENTER);


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




        JButton confirmBtn = new JButton("Confirm & Complete Order");
        confirmBtn.setBackground(new Color(60, 231, 74));
        confirmBtn.setForeground(Color.BLACK);
        confirmBtn.setFont(new Font("Arial", Font.BOLD, 12));
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        confirmBtn.addActionListener(e -> confirmOrder(paymentField, order, change));
       
        buttonPanel.add(cancelBtn);
        buttonPanel.add(confirmBtn);


        footerPanel.add(buttonPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }




    private void confirmOrder(JTextField paymentField, Order order, JLabel change) {
           try{
                double userPayment = Double.parseDouble(paymentField.getText());
                double totalAmount = order.getTotal();
                if (userPayment < totalAmount){
                    JOptionPane.showMessageDialog(this,
                        "Insufficient Payment Amount. Please pay again.",
                        "Insufficient Amount.",
                        JOptionPane.WARNING_MESSAGE);
                        paymentField.setText("");
                        return;
                } else if(userPayment >= totalAmount){
                    double userChange = userPayment - totalAmount;
                    change.setText(String.format("Change: P %.2f", userChange));
                }
            } catch(NumberFormatException nfe){
                JOptionPane.showMessageDialog(this,
                    "Input input. Please pay again.",
                    "Invalid Input.",
                    JOptionPane.WARNING_MESSAGE);
                    paymentField.setText("");
                    return;
            }


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

