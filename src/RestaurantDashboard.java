package src;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class RestaurantDashboard extends JFrame implements RestaurantFrame {
    private static final int NUM_TABLES = 10;
    private Map<Integer, TablePanel> tables;
    private JPanel tablePanel;
    private JLabel statusLabel;
    private static RestaurantDashboard instance;

    public static RestaurantDashboard getInstance() {
        if (instance == null) {
            instance = new RestaurantDashboard();
        }
        return instance;
    }

    public RestaurantDashboard() {
        setTitle("Frances & Francis - Restaurant Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH); 

        tables = new HashMap<>();
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        tablePanel = new JPanel(new GridLayout(2, 5, 15, 15));
        tablePanel.setBackground(new Color(215, 201, 174));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(tablePanel, BorderLayout.CENTER); 

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
        loadTables();

        setLocationRelativeTo(null);
        setResizable(true);
        updateStats();
    }

    public void updateTableState(int tableNumber, boolean occupied) {
        if (tables.containsKey(tableNumber)) {
            tables.get(tableNumber).setOccupied(occupied);
            updateStats();
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 45, 45));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Frances & Francis", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        /*statusLabel = new JLabel("Cookin'");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(46, 204, 113));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(45, 45, 45));
        rightPanel.add(statusLabel);
*/
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        //headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }
   
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(166, 135, 99));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton backToHomeButton = new JButton("Back to Home");
        backToHomeButton.setBackground(new Color(52, 152, 219));
        backToHomeButton.setForeground(Color.BLACK);
        backToHomeButton.setFont(new Font("Arial", Font.BOLD, 12));
        backToHomeButton.setFocusPainted(false);
        backToHomeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backToHomeButton.addActionListener(e -> {
            this.setVisible(false);
            RestaurantHomePage.getInstance().setVisible(true);
        });

        JButton menuManagementButton = new JButton("Manage Menu");
        menuManagementButton.setBackground(new Color(155, 89, 182));
        menuManagementButton.setForeground(Color.BLUE);
        menuManagementButton.setFont(new Font("Arial", Font.BOLD, 12));
        menuManagementButton.setFocusPainted(false);
        menuManagementButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuManagementButton.addActionListener(e -> {
            this.setVisible(false);
            new MenuManagement().setVisible(true);
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(166, 135, 99));
        rightPanel.add(menuManagementButton);
        rightPanel.add(backToHomeButton);

        footerPanel.add(rightPanel, BorderLayout.EAST);

        return footerPanel;
    }

    public void loadTables() {
        tablePanel.removeAll();
        tables.clear();

        try {
            Database.testConnection();
            Database.synchronizeTableStates();
        
            List<TablePanel> loadedTables = TablePanel.loadFromDatabase(this);
            for (TablePanel t : loadedTables) {
                tables.put(t.getTableNumber(), t);
                tablePanel.add(t);
            }
        
            System.out.println("Loaded " + loadedTables.size() + " tables from database");
        
        } catch (Exception e) {
            System.err.println("Error loading tables: " + e.getMessage());
            e.printStackTrace();
            for (int i = 1; i <= NUM_TABLES; i++) {
                TablePanel table = new TablePanel(i, this);
                tables.put(i, table);
                tablePanel.add(table);
            }
            System.out.println("Created default tables due to database error");
        }

        tablePanel.revalidate();
        tablePanel.repaint();
    }

    public static void launchDashboard() {
        SwingUtilities.invokeLater(() -> {
            RestaurantDashboard dashboard = new RestaurantDashboard();
            dashboard.setVisible(true);
        });
    }

    public void updateStats() {
        // Method kept for interface compatibility
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            RestaurantDashboard dashboard = getInstance();
            dashboard.setVisible(true);
        });
    }

    public static void showDashboard() {
        SwingUtilities.invokeLater(() -> {
            RestaurantDashboard dashboard = getInstance();
            dashboard.setVisible(true);
        });
    }
}
