import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class RestaurantDashboard extends JFrame {
    private static final int NUM_TABLES = 10;
    private Map<Integer, TablePanel> tables;
    private JLabel statusLabel;
    private JLabel occupiedCountLabel;

    public RestaurantDashboard() {
        setTitle("Frances & Francis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tables = new HashMap<>();
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel tablesPanel = createTablesPanel();
        add(tablesPanel, BorderLayout.CENTER);

        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);

        setSize(800, 600);
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

        JLabel titleLabel = new JLabel("Frances & Francis Resto", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("Cookin'");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(46, 204, 113));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(45, 45, 45));
        rightPanel.add(statusLabel);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablesPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(215, 201, 174));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel sectionTitle = new JLabel("Table Selection");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(new Color(52, 73, 94));
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel gridPanel = new JPanel(new GridLayout(2, 5, 15, 15));
        gridPanel.setBackground(new Color(215, 201, 174));

        for (int i = 1; i <= NUM_TABLES; i++) {
            TablePanel tablePanel = new TablePanel(i, this);
            tables.put(i, tablePanel);
            gridPanel.add(tablePanel);
        }

        mainPanel.add(sectionTitle, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(166, 135, 99));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        occupiedCountLabel = new JLabel();
        occupiedCountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        occupiedCountLabel.setForeground(new Color(52, 73, 94));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshButton.addActionListener(e -> updateStats());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(166, 135, 99));
        leftPanel.add(occupiedCountLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(166, 135, 99));
        rightPanel.add(refreshButton);

        footerPanel.add(leftPanel, BorderLayout.WEST);
        footerPanel.add(rightPanel, BorderLayout.EAST);

        return footerPanel;
    }

    public static void launchDashboard() {
        SwingUtilities.invokeLater(() -> {
            RestaurantDashboard dashboard = new RestaurantDashboard();
            dashboard.setVisible(true);
        });
    }

    public void updateStats() {
        int occupiedCount = 0;
        for (TablePanel table : tables.values()) {
            if (table.isOccupied()) {
                occupiedCount++;
            }
        }

        int availableCount = NUM_TABLES - occupiedCount;
        occupiedCountLabel.setText(String.format("Tables: %d Occupied | %d Available | %d Total",
                occupiedCount, availableCount, NUM_TABLES));
    }

    public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }
    SwingUtilities.invokeLater(() -> new RestaurantDashboard().setVisible(true));
}
}