import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

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

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 45, 45));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Frances & Francis Resto Bar", SwingConstants.CENTER);
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

        JLabel sectionTitle = new JLabel("Overview");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 18));
        sectionTitle.setForeground(new Color(52, 73, 94));
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel gridPanel = new JPanel(new GridLayout(2, 5, 15, 15));
        gridPanel.setBackground(new Color(215, 201, 174));

        for (int i = 1; i <= NUM_TABLES; i++) {
            TablePanel tablePanel = new TablePanel(i);
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

    private void updateStats() {
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

    private class TablePanel extends JPanel {
        private int tableNumber;
        private boolean occupied;
        private JLabel tableLabel;
        private JLabel statusLabel;
        private JButton toggleButton;

        private final int ARC_WIDTH = 30;
        private final int ARC_HEIGHT = 30;

        public TablePanel(int tableNumber) {
            this.tableNumber = tableNumber;
            this.occupied = false;

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
            toggleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toggleOccupancy();
                }
            });

            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setOpaque(false);
            centerPanel.add(statusLabel, BorderLayout.CENTER);
            centerPanel.add(toggleButton, BorderLayout.SOUTH);

            add(tableLabel, BorderLayout.NORTH);
            add(centerPanel, BorderLayout.CENTER);
        }

        private void toggleOccupancy() {
            occupied = !occupied;
            updateAppearance();
            updateStats();
        }

        private void updateAppearance() {
            if (occupied) {
                setBackground(new Color(234, 224, 210));
                tableLabel.setForeground(Color.BLACK);
                statusLabel.setText("OCCUPIED");
                statusLabel.setForeground(Color.BLACK);
                toggleButton.setText("Free Table");
                toggleButton.setBackground(new Color(46, 204, 113));
                toggleButton.setForeground(Color.BLACK);
            } else {
                setBackground(new Color(234, 224, 210));
                tableLabel.setForeground(Color.BLACK);
                statusLabel.setText("AVAILABLE");
                statusLabel.setForeground(Color.BLACK);
                toggleButton.setText("Occupy Table");
                toggleButton.setBackground(new Color(231, 76, 60));
                toggleButton.setForeground(Color.BLACK);
            }

            tableLabel.setOpaque(false);
            statusLabel.setOpaque(false);
            setOpaque(true);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_WIDTH, ARC_HEIGHT);
            g2.dispose();
        }

        public boolean isOccupied() {
            return occupied;
        }

        public int getTableNumber() {
            return tableNumber;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RestaurantDashboard().setVisible(true);
            }
        });
    }
}

