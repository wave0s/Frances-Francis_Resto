package src;
import java.awt.*;
import javax.swing.*;


public class EditOrderWPayment extends JFrame implements RestaurantFrame{
    private TablePanel tablePanel;
    private RestaurantDashboard parentDashboard;
    private JTextField paymentField = new JTextField();
    private Order order;
    private JLabel change = new JLabel();


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

    @Override
    public JPanel createHeaderPanel() {
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
        return headerPanel;
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


        order = OrderManager.getInstance().getOrder(tablePanel.getTableNumber());
        StringBuilder orderText = new StringBuilder();
       
        if (order != null) {
            orderText.append("Current Order for Table ").append(tablePanel.getTableNumber()).append(":\n\n");
           
            for (Order.OrderItem item : order.getItems()) {
                orderText.append("• ").append(item.getName())
                         .append(" x").append(item.getQuantity())
                         .append(" - Php ").append(String.format("%.2f", item.getPrice() * item.getQuantity()))
                         .append("\n");
            }
           
            orderText.append("\nSubtotal: Php ").append(String.format("%.2f", order.getSubtotal())); 
            orderText.append("\nTax (12%): Php ").append(String.format("%.2f", order.getTax()));
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
       
        paymentPanel.add(PaymentTitle);
        paymentPanel.add(dash);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(totalLabel);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(lines);
        paymentPanel.add(Box.createVerticalStrut(20));
        paymentPanel.add(Cash);
        paymentPanel.add(Box.createVerticalStrut(10));
        paymentPanel.add(paymentField);
        paymentPanel.add(Box.createVerticalStrut(15));
        
        JPanel numberPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        numberPanel.setBackground(new Color(234, 224, 210));
        numberPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        paymentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        numberPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentField.setAlignmentX(Component.LEFT_ALIGNMENT);
        numberPanel.setMaximumSize(new Dimension(250, 200));
 
        for (int i = 1; i <= 9; i++) {
            JButton numButton = new JButton(String.valueOf(i));
            numButton.setFont(new Font("Arial", Font.BOLD, 16));
            numButton.setBackground(Color.WHITE);
            numButton.setForeground(Color.BLACK);   
            final int num = i; 
            numButton.addActionListener(e -> paymentField.setText(paymentField.getText() + num));
            numberPanel.add(numButton);
        }

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearBtn.setBackground(Color.WHITE);
        clearBtn.setForeground(Color.BLACK);
        clearBtn.addActionListener(e -> paymentField.setText(""));
        clearBtn.setBorder(BorderFactory.createEmptyBorder());
        numberPanel.add(clearBtn);

        JButton zeroBtn = new JButton("0");
        zeroBtn.setFont(new Font("Arial", Font.BOLD, 14));
        zeroBtn.setBackground(Color.WHITE);
        zeroBtn.setForeground(Color.BLACK);
        zeroBtn.addActionListener(e -> paymentField.setText(paymentField.getText() + "0"));
        numberPanel.add(zeroBtn);

        JButton delBtn = new JButton("Del");
        delBtn.setFont(new Font("Arial", Font.BOLD, 14));
        delBtn.setBackground(Color.WHITE);
        delBtn.setForeground(Color.BLACK);
        delBtn.addActionListener(e -> {
            String text = paymentField.getText();
            if (!text.isEmpty()) {
                paymentField.setText(text.substring(0, text.length() - 1));
            }});
        numberPanel.add(delBtn);

        paymentPanel.add(numberPanel);


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

    @Override
    public JPanel createFooterPanel() {
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
            RestaurantDashboard.showDashboard();
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

        return footerPanel;
    }

    private void confirmOrder(JTextField paymentField, Order order, JLabel change) {
        try{
            //ari gin change ko
            if (paymentField.getText().trim().isEmpty()){
                throw new IllegalArgumentException();
                
            }else{ // gin add ko ni and ang next 8 lines
                String payment = paymentField.getText();
                if (payment.matches("0\\d+")) {
                    JOptionPane.showMessageDialog(this,
                            "Leading zeros are not allowed. Please pay again.",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE);
                    paymentField.setText("");
                    return;
                }

                double userPayment = Double.parseDouble(paymentField.getText().trim());
                double totalAmount = order.getTotal();
                if(userPayment >= totalAmount){
                    double userChange = userPayment - totalAmount;
                    change.setText(String.format("Change: P %.2f", userChange));
                    JOptionPane.showMessageDialog(this,
                    "Order completed successfully!\n" +
                    "Table " + tablePanel.getTableNumber() + " is now available.\n\n" + 
                                String.format("Change: P %.2f", userChange),
                            "Order Completed",
                    JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Insufficient Payment Amount. Please pay again.",
                        "Insufficient Amount.",
                        JOptionPane.WARNING_MESSAGE);
                        paymentField.setText("");
                        return;
                } 
            }
        
        } catch(NumberFormatException nfe){
            JOptionPane.showMessageDialog(this,
                "Special characters and spaces are not allowed. Please enter valid payment amount.",
                "Invalid Input.",
                JOptionPane.WARNING_MESSAGE);
                paymentField.setText("");
                return;
        }catch (IllegalArgumentException e) { // gin add ko ni
            JOptionPane.showMessageDialog(this,
                    "Payment Amount Field is empty. Please enter payment amount.",
                    "Empty Field.",
                    JOptionPane.WARNING_MESSAGE);
                    paymentField.setText("");
                    return;
        }

        
    

        double userPayment = Double.parseDouble(paymentField.getText());
        OrderManager.getInstance().completeOrder(tablePanel.getTableNumber(), userPayment);
        
        tablePanel.setOccupied(false);

        dispose();
    

        RestaurantDashboard dashboard = RestaurantDashboard.getInstance();
        dashboard.updateTableState(tablePanel.getTableNumber(), false);
        dashboard.setVisible(true);
    }
}