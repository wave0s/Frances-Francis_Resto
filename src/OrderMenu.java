package src;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Comparator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class OrderMenu extends JFrame implements RestaurantFrame {
    private TablePanel tablePanel;
    private RestaurantDashboard dashboard;
    private JPanel orderItemsPanel;
    private JLabel totalLabel;
    private double totalAmount = 0.0;
    private Map<String, OrderItem> orderItems;
    private JScrollPane orderScrollPane;
    private ArrayList<String> cart = new ArrayList<>();

    // New fields for sorting
    private JPanel menuGridPanel;
    private List<MenuItem> allMenuItems;
    private JComboBox<String> sortComboBox;
    private JComboBox<String> categoryFilterComboBox;

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
        this.allMenuItems = new ArrayList<>();

        setTitle("Order Menu - Table " + tablePanel.getTableNumber());
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(215, 201, 174));

        createHeaderPanel();
        createMenuPanel();
        createFooterPanel();

        loadMenuItems(); // Load items after UI is created
    }

    @Override
    public JPanel createHeaderPanel() {
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
        return headerPanel;
    }

    private void createMenuPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(215, 201, 174));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create sorting controls panel
        JPanel sortingPanel = createSortingPanel();
        mainPanel.add(sortingPanel, BorderLayout.NORTH);

        // Create menu grid panel
        menuGridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        menuGridPanel.setBackground(new Color(215, 201, 174));

        JScrollPane menuScrollPane = new JScrollPane(menuGridPanel);
        menuScrollPane.setBackground(new Color(215, 201, 174));
        menuScrollPane.getViewport().setBackground(new Color(215, 201, 174));
        menuScrollPane.setBorder(BorderFactory.createTitledBorder("Menu Items"));
        menuScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel orderSection = createOrderSection();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(menuScrollPane);
        splitPane.setRightComponent(orderSection);
        splitPane.setDividerLocation(550);
        splitPane.setBackground(new Color(215, 201, 174));
        splitPane.setBorder(null);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSortingPanel() {
        JPanel sortingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        sortingPanel.setBackground(new Color(215, 201, 174));
        sortingPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Sort & Filter Options",
                0, 0, new Font("Arial", Font.BOLD, 14), new Color(52, 73, 94)
        ));

        // Sort by dropdown
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font("Arial", Font.BOLD, 12));
        sortLabel.setForeground(new Color(52, 73, 94));

        String[] sortOptions = {"Name (A-Z)", "Name (Z-A)", "Price (Low to High)", "Price (High to Low)", "Category"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        sortComboBox.setPreferredSize(new Dimension(150, 30));
        sortComboBox.addActionListener(e -> applySortingAndFiltering());

        // Category filter dropdown
        JLabel filterLabel = new JLabel("Filter by Category:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 12));
        filterLabel.setForeground(new Color(52, 73, 94));

        categoryFilterComboBox = new JComboBox<>();
        categoryFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryFilterComboBox.setPreferredSize(new Dimension(120, 30));
        categoryFilterComboBox.addActionListener(e -> applySortingAndFiltering());

        // Reset button
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.BOLD, 11));
        resetButton.setBackground(new Color(52, 152, 219));
        resetButton.setForeground(Color.BLACK);
        resetButton.setFocusPainted(false);
        resetButton.setPreferredSize(new Dimension(70, 30));
        resetButton.addActionListener(e -> resetSortingAndFiltering());

        sortingPanel.add(sortLabel);
        sortingPanel.add(sortComboBox);
        sortingPanel.add(Box.createHorizontalStrut(20));
        sortingPanel.add(filterLabel);
        sortingPanel.add(categoryFilterComboBox);
        sortingPanel.add(Box.createHorizontalStrut(10));
        sortingPanel.add(resetButton);

        return sortingPanel;
    }

    private void loadMenuItems() {
        allMenuItems.clear();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, price, category FROM menu_item ORDER BY category, name")) {

            // Load all menu items
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                allMenuItems.add(new MenuItem(name, price, category));
            }

            // Populate category filter dropdown
            populateCategoryFilter();

            // Display items with default sorting
            applySortingAndFiltering();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load menu items: " + e.getMessage());
        }
    }

    private void populateCategoryFilter() {
        categoryFilterComboBox.removeAllItems();
        categoryFilterComboBox.addItem("All Categories");

        // Get unique categories
        allMenuItems.stream()
                .map(MenuItem::getCategory)
                .distinct()
                .sorted()
                .forEach(categoryFilterComboBox::addItem);
    }

    private void applySortingAndFiltering() {
        // Clear current display
        menuGridPanel.removeAll();

        // Get filtered items
        List<MenuItem> filteredItems = getFilteredItems();

        // Sort items
        List<MenuItem> sortedItems = getSortedItems(filteredItems);

        // Display sorted and filtered items
        for (MenuItem item : sortedItems) {
            JPanel menuItemPanel = createMenuItemPanel(item.getName(), item.getPrice(), item.getCategory());
            menuGridPanel.add(menuItemPanel);
        }

        // Update display
        menuGridPanel.revalidate();
        menuGridPanel.repaint();
    }

    private List<MenuItem> getFilteredItems() {
        String selectedCategory = (String) categoryFilterComboBox.getSelectedItem();

        if (selectedCategory == null || selectedCategory.equals("All Categories")) {
            return new ArrayList<>(allMenuItems);
        }

        return allMenuItems.stream()
                .filter(item -> item.getCategory().equals(selectedCategory))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private List<MenuItem> getSortedItems(List<MenuItem> items) {
        String sortOption = (String) sortComboBox.getSelectedItem();
        List<MenuItem> sortedItems = new ArrayList<>(items);

        switch (sortOption) {
            case "Name (A-Z)":
                sortedItems.sort(Comparator.comparing(MenuItem::getName));
                break;
            case "Name (Z-A)":
                sortedItems.sort(Comparator.comparing(MenuItem::getName).reversed());
                break;
            case "Price (Low to High)":
                sortedItems.sort(Comparator.comparing(MenuItem::getPrice));
                break;
            case "Price (High to Low)":
                sortedItems.sort(Comparator.comparing(MenuItem::getPrice).reversed());
                break;
            case "Category":
                sortedItems.sort(Comparator.comparing(MenuItem::getCategory)
                        .thenComparing(MenuItem::getName));
                break;
        }

        return sortedItems;
    }

    private void resetSortingAndFiltering() {
        sortComboBox.setSelectedIndex(0);
        categoryFilterComboBox.setSelectedIndex(0);
        applySortingAndFiltering();
    }

    private JPanel createMenuItemPanel(String itemName, double price, String category) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setPreferredSize(new Dimension(160, 140));
        itemPanel.setBackground(new Color(234, 224, 210));
        itemPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        JLabel nameLabel = new JLabel(itemName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setBorder(new EmptyBorder(8, 5, 3, 5));

        JLabel categoryLabel = new JLabel(category, SwingConstants.CENTER);
        categoryLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        categoryLabel.setForeground(new Color(100, 100, 100));

        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", price), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        priceLabel.setForeground(new Color(52, 73, 94));

        JButton addButton = new JButton("Add Item");
        addButton.setFont(new Font("Arial", Font.BOLD, 10));
        addButton.setFocusPainted(false);
        addButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        addButton.setBackground(new Color(60, 231, 74));
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(e -> addToOrder(itemName, price));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(nameLabel, BorderLayout.CENTER);
        topPanel.add(categoryLabel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(priceLabel, BorderLayout.CENTER);
        centerPanel.add(addButton, BorderLayout.SOUTH);

        itemPanel.add(topPanel, BorderLayout.NORTH);
        itemPanel.add(centerPanel, BorderLayout.CENTER);

        return itemPanel;
    }

    // Enhanced MenuItem class to include categ
    private static class MenuItem {
        private String name;
        private double price;
        private String category;

        public MenuItem(String name, double price, String category) {
            this.name = name;
            this.price = price;
            this.category = category;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getCategory() { return category; }
    }

    private JPanel createOrderSection() {
        JPanel orderSection = new JPanel(new BorderLayout());
        orderSection.setBackground(new Color(234, 224, 210));
        orderSection.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                new EmptyBorder(15, 15, 15, 15)
        ));
        orderSection.setPreferredSize(new Dimension(300, 0));

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

        totalLabel = new JLabel("Total: ₱ 0.00", SwingConstants.CENTER);
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
        minusButton.setBorder(BorderFactory.createEmptyBorder());
        minusButton.addActionListener(e -> decrementItem(item));

        JButton plusButton = new JButton("+");
        plusButton.setPreferredSize(new Dimension(25, 25));
        plusButton.setFont(new Font("Arial", Font.BOLD, 12));
        plusButton.setBackground(new Color(60, 231, 74));
        plusButton.setForeground(Color.BLACK);
        plusButton.setFocusPainted(false);
        plusButton.setBorder(BorderFactory.createEmptyBorder());
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
        String text = String.format("%s x%d - ₱%.2f",
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
        totalLabel.setText("Total: ₱ " + String.format("%.2f", totalAmount));
    }

    @Override
    public JPanel createFooterPanel() {
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
            RestaurantDashboard.showDashboard();
        });

        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.setBackground(new Color(60, 231, 74));
        placeOrderBtn.setForeground(Color.BLACK);
        placeOrderBtn.setFont(new Font("Arial", Font.BOLD, 12));
        placeOrderBtn.setFocusPainted(false);
        placeOrderBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        placeOrderBtn.addActionListener(e -> {
            if (!orderItems.isEmpty()) {

                int confirm = JOptionPane.showConfirmDialog(this,
                "Once your order has been placed, it cannot be changed.",
                "Confirm Order",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                
                   boolean success = true;

                    if (success) {
                       
                    OrderManager.getInstance().createOrder(tablePanel.getTableNumber());
                    Order order = OrderManager.getInstance().getOrder(tablePanel.getTableNumber());
                    
                    for (Map.Entry<String, OrderItem> entry : orderItems.entrySet()) {
                    OrderItem item = entry.getValue();
                    order.addItem(item.name, item.price, item.quantity);
                    }
                    order.calculateTotals();
                    
                    tablePanel.setOccupied(true);
                     dispose();
                    RestaurantDashboard dashboard = RestaurantDashboard.getInstance();
                    dashboard.updateTableState(tablePanel.getTableNumber(), true);
                    dashboard.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to delete menu item.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }

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
            totalLabel.setText("Total: ₱ 0.00");
            orderItemsPanel.revalidate();
            orderItemsPanel.repaint();
        });

        buttonPanel.add(backBtn);
        buttonPanel.add(clearOrderBtn);
        buttonPanel.add(placeOrderBtn);

        footerPanel.add(buttonPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        return footerPanel;
    }
}