package src;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MenuManagement extends JFrame implements RestaurantFrame {
    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<String> categoryCombo;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton; //gin add ko ni
    //private JButton refreshButton;//kakson nlng ni ky wla mni use -kay eunice ni
    private int selectedItemId = -1;
    private List<MenuItemData> menuItems; 

    public MenuManagement() {
        setTitle("Menu Management - Frances & Francis");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(215, 201, 174));

        createHeaderPanel();
        createMainPanel();
        createFooterPanel();
        
        loadMenuItems();
    }

    @Override
    public JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 45, 45));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Menu Management System", SwingConstants.CENTER);
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

    private void createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(215, 201, 174));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));


        String[] columnNames = {"Name", "Price", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        menuTable = new JTable(tableModel);
        menuTable.setFont(new Font("Arial", Font.PLAIN, 14));
        menuTable.setRowHeight(25);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedItem();
            }
        });


        menuTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Name
        menuTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Price
        menuTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Category

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Menu Items"));
        scrollPane.setPreferredSize(new Dimension(500, 300));


        JPanel formPanel = createFormPanel();


        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(formPanel);
        splitPane.setDividerLocation(500);
        splitPane.setBackground(new Color(215, 201, 174));

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(234, 224, 210));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Menu Item Details"),
            new EmptyBorder(15, 15, 15, 15)
        ));

    
        JLabel nameLabel = new JLabel("Item Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 
        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT); 

        
        JLabel priceLabel = new JLabel("Price (₱):");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 
        priceField = new JTextField();
        priceField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        priceField.setFont(new Font("Arial", Font.PLAIN, 14));
        priceField.setAlignmentX(Component.LEFT_ALIGNMENT); 


        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT); 
        categoryCombo = new JComboBox<>();
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryCombo.setEditable(false); 
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT); 
        loadCategories();

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        buttonPanel.setBackground(new Color(234, 224, 210));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        addButton = new JButton("Add Item");
        addButton.setBackground(new Color(60, 231, 74));
        addButton.setForeground(Color.BLACK);
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addMenuItem());

        updateButton = new JButton("Update Item");
        updateButton.setBackground(new Color(52, 152, 219));
        updateButton.setForeground(Color.BLACK);
        updateButton.setFont(new Font("Arial", Font.BOLD, 12));
        updateButton.setFocusPainted(false);
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> updateMenuItem());

        deleteButton = new JButton("Delete Item");
        deleteButton.setBackground(new Color(231, 71, 60));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteMenuItem());

        clearButton = new JButton("Clear Item");
        clearButton.setBackground(new Color(60, 231, 74));
        clearButton.setForeground(Color.BLACK);
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearForm());



        buttonPanel.add(addButton);
        buttonPanel.add(clearButton); //gin add ko ni
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(priceLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(priceField);
        formPanel.add(Box.createVerticalStrut(15));

        formPanel.add(categoryLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(categoryCombo);
        formPanel.add(Box.createVerticalStrut(20));

        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalGlue());

        return formPanel;
    }

    @Override
    public JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(166, 135, 99));
        footerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setFocusPainted(false);
        backButton.setBorder(new EmptyBorder(8, 15, 8, 15));
        backButton.addActionListener(e -> {
            dispose();
            RestaurantDashboard.showDashboard();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(166, 135, 99));
        rightPanel.add(backButton);

        footerPanel.add(rightPanel, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        return footerPanel;
    }

    private void loadMenuItems() {
        try {
            menuItems = Database.getAllMenuItems(); 
            tableModel.setRowCount(0); 

            for (MenuItemData item : menuItems) {
                Object[] row = {
                    item.getName(),
                    String.format("₱%.2f", item.getPrice()),
                    item.getCategory()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading menu items: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try {
            List<String> categories = Database.getCategories();
            categoryCombo.removeAllItems();
            
            // Add def categories
            String[] defaultCategories = {"Main Course", "Appetizer", "Dessert", "Drinks", "Sides", "Noodles"};
            for (String category : defaultCategories) {
                categoryCombo.addItem(category);
            }
            
            for (String category : categories) {
                boolean exists = false;
                for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                    if (categoryCombo.getItemAt(i).equals(category)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    categoryCombo.addItem(category);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading categories: " + e.getMessage());
        }
    }

    private void loadSelectedItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < menuItems.size()) {

            MenuItemData selectedItem = menuItems.get(selectedRow);
            selectedItemId = selectedItem.getId();
            
            nameField.setText(selectedItem.getName());
            priceField.setText(String.format("%.2f", selectedItem.getPrice()));
            categoryCombo.setSelectedItem(selectedItem.getCategory());
            
            addButton.setEnabled(false); // gin add ko ni
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            clearForm();
        }
    }

    private void clearForm() {
        nameField.setText("");
        priceField.setText("");
        categoryCombo.setSelectedIndex(0);
        selectedItemId = -1;
        addButton.setEnabled(true); // gin add ko ni
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        menuTable.clearSelection();
    }

   
    private void addMenuItem() {
        if (validateForm()) {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String category = (String) categoryCombo.getSelectedItem();

                boolean success = Database.addMenuItem(name, price, category);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Menu item added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadMenuItems(); 
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to add menu item.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void updateMenuItem() {
        if (selectedItemId > 0 && validateForm()) {
            try {
                String name = nameField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                String category = (String) categoryCombo.getSelectedItem();

                // Get the current item data to compare for changes
            MenuItemData currentItem = null;
            for (MenuItemData item : menuItems) {
                if (item.getId() == selectedItemId) {
                    currentItem = item;
                    break;
                }
            }

            // Check if there are any actual changes
            if (currentItem != null) {
                boolean hasChanges = !currentItem.getName().equals(name) ||
                                   Math.abs(currentItem.getPrice() - price) > 0.001 ||
                                   !currentItem.getCategory().equals(category);

                if (!hasChanges) {
                    JOptionPane.showMessageDialog(this,
                        "No changes detected. Please modify the item details before updating.",
                        "No Changes",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            boolean success = Database.updateMenuItem(selectedItemId, name, price, category);

                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Menu item updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    addButton.setEnabled(true); // gin add ko ni
                    loadMenuItems(); 
                    clearForm();

                }else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to update menu item.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void deleteMenuItem() {
        if (selectedItemId > 0) {
            int selectedRow = menuTable.getSelectedRow();
            String itemName = (selectedRow >= 0) ? menuItems.get(selectedRow).getName() : "this item";
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + itemName + "'?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean success = Database.deleteMenuItem(selectedItemId);
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "Menu item deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        addButton.setEnabled(true); // gin add ko ni
                        loadMenuItems(); 
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to delete menu item.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                        "Database error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }














    private boolean validateForm() {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter an item name.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a price.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            priceField.requestFocus();
            return false;
        }

        //gin add ni eunice ang next 7 lines
        if (!name.matches("[a-zA-Z ]+")){
            JOptionPane.showMessageDialog(null,
            "Invalid input! Menu item name must contain only letters.",
            "Input Error",
                JOptionPane.ERROR_MESSAGE);
                nameField.setText(""); // gin add ko ni
                return false;
            }

        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Price must be greater than 0.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                priceField.setText(""); // gin add ko ni
                priceField.requestFocus();
                return false;
            }else if (price > 10000){ // gin add ko ni ang 7 lines
                    JOptionPane.showMessageDialog(this,
                    "Maximum Price is 10,000.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                priceField.setText("");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid price (numbers only).",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
                priceField.setText(""); // gin add ko ni
            priceField.requestFocus();
            return false;
        }

        if (category == null || category.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select or enter a category.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            categoryCombo.requestFocus();
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MenuManagement().setVisible(true);
        });
    }
}