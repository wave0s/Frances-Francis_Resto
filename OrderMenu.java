import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class OrderMenu extends JFrame {
    private TablePanel tablePanel;
    private RestaurantDashboard dashboard;
    private JPanel orderItemsPanel;
    private JLabel totalLabel;
    private double totalAmount = 0.0;
    private Map<String, OrderItem> orderItems;
    private JScrollPane orderScrollPane;


    private class OrderItem {
        String name;
        double price;
        int quantity;
        JPanel panel;

        OrderItem(String name, double price) {
            this.name = name;
            this.price = price;
            this.quantity = 1;
        }
    }

    public OrderMenu(TablePanel tablePanel, RestaurantDashboard dashboard) {
        this.tablePanel = tablePanel;
        this.dashboard = dashboard;
        this.orderItems = new HashMap<>();

        setTitle("Order Menu - Table " + tablePanel.getTableNumber());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

    
        getContentPane().setBackground(new Color(215, 201, 174));

        createHeaderPanel();
        createMenuPanel();
        createFooterPanel();
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 45, 45));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Order Menu - Table " + tablePanel.getTableNumber(), SwingConstants.CENTER);
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

    private void createMenuPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(215, 201, 174));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel sectionTitle = new JLabel("Menu Items");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(new Color(52, 73, 94));
        sectionTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        JPanel menuGridPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        menuGridPanel.setBackground(new Color(215, 201, 174));

        String[][] menuItems = {
            {"Classic Burger", "89.99"},
            {"Caesar Salad", "199.99"},
            {"Margherita Pizza", "299.99"},
            {"Fish & Chips", "169.99"},
            {"Chocolate Cake", "369.99"},
            {"Coffee", "169.99"}
        };

        for (String[] item : menuItems) {
            JPanel menuItemPanel = createMenuItemPanel(item[0], Double.parseDouble(item[1]));
            menuGridPanel.add(menuItemPanel);
        }
        JPanel orderSection = createOrderSection();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(215, 201, 174));
        leftPanel.add(sectionTitle, BorderLayout.NORTH);
        leftPanel.add(menuGridPanel, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(orderSection);
        splitPane.setDividerLocation(500);
        splitPane.setBackground(new Color(215, 201, 174));
        splitPane.setBorder(null);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createMenuItemPanel(String itemName, double price) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setPreferredSize(new Dimension(140, 120));
        itemPanel.setBackground(new Color(234, 224, 210));
        itemPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel nameLabel = new JLabel(itemName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setBorder(new EmptyBorder(10, 5, 5, 5));


        JLabel priceLabel = new JLabel("Php" + String.format("%.2f", price), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        priceLabel.setForeground(Color.BLACK);


        JButton addButton = new JButton("Add Item");
        addButton.setFont(new Font("Arial", Font.BOLD, 10));
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        addButton.setBackground(new Color(60, 231, 74));
        addButton.setForeground(Color.BLACK);
        
        addButton.addActionListener(e -> addToOrder(itemName, price));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(priceLabel, BorderLayout.CENTER);
        centerPanel.add(addButton, BorderLayout.SOUTH);

        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(centerPanel, BorderLayout.CENTER);

        return itemPanel;
    }

    private JPanel createOrderSection() {
        JPanel orderSection = new JPanel(new BorderLayout());
        orderSection.setBackground(new Color(234, 224, 210));
        orderSection.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            new EmptyBorder(15, 15, 15, 15)
        ));
        orderSection.setPreferredSize(new Dimension(280, 0));

        JLabel orderTitle = new JLabel("Current Order");
        orderTitle.setFont(new Font("Arial", Font.BOLD, 18));
        orderTitle.setForeground(new Color(52, 73, 94));
        orderTitle.setBorder(new EmptyBorder(0, 0, 15, 0));


        orderItemsPanel = new JPanel();
        orderItemsPanel.setLayout(new BoxLayout(orderItemsPanel, BoxLayout.Y_AXIS));
        orderItemsPanel.setBackground(new Color(215, 201, 174));

        JLabel emptyLabel = new JLabel("Nothing to see here :(", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderItemsPanel.add(emptyLabel);

        orderScrollPane = new JScrollPane(orderItemsPanel);
        orderScrollPane.setBackground(new Color(215, 201, 174));
        orderScrollPane.getViewport().setBackground(new Color(215, 201, 174));
        orderScrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        orderScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        totalLabel = new JLabel("Total: Php0.00", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(52, 73, 94));
        totalLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        orderSection.add(orderTitle, BorderLayout.NORTH);
        orderSection.add(orderScrollPane, BorderLayout.CENTER);
        orderSection.add(totalLabel, BorderLayout.SOUTH);

        return orderSection;
    }

    private void addToOrder(String itemName, double price) {
        if (orderItems.containsKey(itemName)) {

            OrderItem item = orderItems.get(itemName);
            item.quantity++;
            updateOrderItemDisplay(item);
        } else {

            OrderItem newItem = new OrderItem(itemName, price);
            orderItems.put(itemName, newItem);
            
            if (orderItems.size() == 1) {
                orderItemsPanel.removeAll();
            }
            
            createOrderItemPanel(newItem);
        }
        
        updateTotal();
        orderItemsPanel.revalidate();
        orderItemsPanel.repaint();
    }

    private void createOrderItemPanel(OrderItem item) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(new Color(234, 224, 210));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            new EmptyBorder(8, 10, 8, 10)
        ));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel itemLabel = new JLabel();
        updateItemLabel(itemLabel, item);
        itemLabel.setFont(new Font("Arial", Font.BOLD, 12));
        itemLabel.setForeground(new Color(52, 73, 94));


        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        controlPanel.setBackground(new Color(234, 224, 210));

        JButton minusButton = new JButton("-");
        minusButton.setPreferredSize(new Dimension(25, 25));
        minusButton.setFont(new Font("Arial", Font.BOLD, 12));
        minusButton.setBackground(new Color(231, 71, 60));
        minusButton.setForeground(Color.BLACK);
        minusButton.setFocusPainted(false);
        minusButton.addActionListener(e -> decrementItem(item));

        JButton plusButton = new JButton("+");
        plusButton.setPreferredSize(new Dimension(25, 25));
        plusButton.setFont(new Font("Arial", Font.BOLD, 12));
        plusButton.setBackground(new Color(60, 231, 74));
        plusButton.setForeground(Color.BLACK);
        plusButton.setFocusPainted(false);
        plusButton.addActionListener(e -> incrementItem(item));

        controlPanel.add(minusButton);
        controlPanel.add(plusButton);

        itemPanel.add(itemLabel, BorderLayout.CENTER);
        itemPanel.add(controlPanel, BorderLayout.EAST);

        item.panel = itemPanel;
        orderItemsPanel.add(itemPanel);
        orderItemsPanel.add(Box.createVerticalStrut(5)); 
    }

    private void updateItemLabel(JLabel label, OrderItem item) {
        String text = String.format("%s x%d - Php %.2f", 
            item.name, item.quantity, item.price * item.quantity);
        label.setText(text);
    }

    private void updateOrderItemDisplay(OrderItem item) {
        if (item.panel != null) {
            JLabel label = (JLabel) ((BorderLayout) item.panel.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
            updateItemLabel(label, item);
        }
    }

    private void incrementItem(OrderItem item) {
        item.quantity++;
        updateOrderItemDisplay(item);
        updateTotal();
    }

    private void decrementItem(OrderItem item) {
        item.quantity--;
        if (item.quantity <= 0) {
            orderItems.remove(item.name);
            orderItemsPanel.remove(item.panel);
            
            Component[] components = orderItemsPanel.getComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] == item.panel && i + 1 < components.length) {
                    orderItemsPanel.remove(components[i + 1]);
                    break;
                }
            }
            
            if (orderItems.isEmpty()) {
                JLabel emptyLabel = new JLabel("Nothing to see here :(", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                emptyLabel.setForeground(Color.GRAY);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                orderItemsPanel.add(emptyLabel);
            }
        } else {
            updateOrderItemDisplay(item);
        }
        
        updateTotal();
        orderItemsPanel.revalidate();
        orderItemsPanel.repaint();
    }

    private void updateTotal() {
        totalAmount = 0.0;
        for (OrderItem item : orderItems.values()) {
            totalAmount += item.price * item.quantity;
        }
        totalLabel.setText("Total: Php " + String.format("%.2f", totalAmount));
    }

    private void createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(166, 135, 99));
        footerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(166, 135, 99));

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(new Color(52, 152, 219));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backBtn.setFocusPainted(false);
        backBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        backBtn.addActionListener(e -> {
            dispose(); 
            if (dashboard != null) {
                RestaurantDashboard.launchDashboard();
            }
        });

        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.setBackground(new Color(60, 231, 74));
        placeOrderBtn.setForeground(Color.BLACK);
        placeOrderBtn.setFont(new Font("Arial", Font.BOLD, 12));
        placeOrderBtn.setFocusPainted(false);
        placeOrderBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        placeOrderBtn.addActionListener(e -> {
            if (!orderItems.isEmpty()) {
                tablePanel.setOccupied(true);
                if (dashboard != null) {
                    dashboard.updateStats();
                }
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please add items to your order before placing it.", 
                    "Empty Order", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton clearOrderBtn = new JButton("Clear Order");
        clearOrderBtn.setBackground(new Color(231, 71, 60));
        clearOrderBtn.setForeground(Color.BLACK);
        clearOrderBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearOrderBtn.setFocusPainted(false);
        clearOrderBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        clearOrderBtn.addActionListener(e -> {
            orderItems.clear();
            orderItemsPanel.removeAll();
            

            JLabel emptyLabel = new JLabel("Nothing to see here :(", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            orderItemsPanel.add(emptyLabel);
            
            totalAmount = 0.0;
            totalLabel.setText("Total: Php 0.00");
            orderItemsPanel.revalidate();
            orderItemsPanel.repaint();
        });

        buttonPanel.add(backBtn);
        buttonPanel.add(clearOrderBtn);
        buttonPanel.add(placeOrderBtn);

        footerPanel.add(buttonPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
}