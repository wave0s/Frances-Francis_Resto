package src;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;

public class RestaurantHomePage extends JFrame implements RestaurantFrame {
    private static RestaurantHomePage instance;

    public static RestaurantHomePage getInstance() {
        if (instance == null) {
            instance = new RestaurantHomePage();
        }
        return instance;
    }

    public RestaurantHomePage() {
        setTitle("Frances and Francis - Welcome");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(10, 26, 47));

        createWelcomeContent();
    }

    private void createWelcomeContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(10, 26, 47));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Header sec
        JPanel headerSection = createHeaderPanel();
        
        // Welcome msg Sec
        JPanel welcomeSection = createWelcomeSection();
        
        // Button sec
        JPanel buttonSection = createButtonSection();
        
        // Footer sec
        JPanel footerSection = createFooterPanel();

        mainPanel.add(headerSection, BorderLayout.NORTH);
        mainPanel.add(welcomeSection, BorderLayout.CENTER);
        mainPanel.add(buttonSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(footerSection, BorderLayout.SOUTH);
    }

    @Override
    public JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 30));
        headerPanel.setBackground(new Color(10, 26, 47));

        ImageIcon logoIcon = createLogoIcon();
        JLabel logoLabel = new JLabel(logoIcon);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(10, 26, 47));

        Font welcomeFont = new Font("SansSerif", Font.BOLD, 48);
        Font restonameFont = new Font("SansSerif", Font.BOLD, 32);
        Font sloganFont = new Font("SansSerif", Font.BOLD, 18);

        try {
            InputStream is = getClass().getResourceAsStream("/fonts/Prisma.ttf");
            if (is != null) {
                Font fontCustom = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(fontCustom);
                welcomeFont = fontCustom.deriveFont(48f);
                restonameFont = fontCustom.deriveFont(32f);
                sloganFont = fontCustom.deriveFont(Font.BOLD, 18f);
            }
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading font: " + e.getMessage());
        }

        JLabel welcomeLabel = new JLabel("Welcome to");
        welcomeLabel.setFont(welcomeFont);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("FRANCES AND FRANCIS");
        nameLabel.setFont(restonameFont);
        nameLabel.setForeground(new Color(255, 215, 0));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sloganLabel = new JLabel("KAON NA KAMO");
        sloganLabel.setFont(sloganFont);
        sloganLabel.setForeground(Color.LIGHT_GRAY);
        sloganLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(welcomeLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(sloganLabel);

        headerPanel.add(logoLabel);
        headerPanel.add(textPanel);

        return headerPanel;
    }

    private JPanel createWelcomeSection() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(new Color(10, 26, 47));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel descriptionLabel = new JLabel("<html><center>" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. <br><br>" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit,<br>" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit.<br><br>" +
            "Ready?" +
            "</center></html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(descriptionLabel);
        welcomePanel.add(Box.createVerticalGlue());

        return welcomePanel;
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        buttonPanel.setBackground(new Color(10, 26, 47));

        JButton orderNowButton = new JButton("ORDER NOW");
        orderNowButton.setFont(new Font("Arial", Font.BOLD, 24));
        orderNowButton.setPreferredSize(new Dimension(250, 80));
        orderNowButton.setBackground(new Color(60, 231, 74));
        orderNowButton.setForeground(Color.BLACK);
        orderNowButton.setFocusPainted(false);
        orderNowButton.setBorder(BorderFactory.createRaisedBevelBorder());
        orderNowButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        orderNowButton.addActionListener(e -> {
            this.setVisible(false);
            RestaurantDashboard dashboard = RestaurantDashboard.getInstance();
            dashboard.setVisible(true);
        });

        JButton aboutButton = new JButton("ABOUT US");
        aboutButton.setFont(new Font("Arial", Font.BOLD, 18));
        aboutButton.setPreferredSize(new Dimension(180, 60));
        aboutButton.setBackground(new Color(52, 152, 219));
        aboutButton.setForeground(Color.BLACK);
        aboutButton.setFocusPainted(false);
        aboutButton.setBorder(BorderFactory.createRaisedBevelBorder());
        aboutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        aboutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Frances and Francis Restaurant\n\n" +
                "Serving authentic Filipino cuisine since 2025.\n" +
                "We pride ourselves on fresh ingredients,\n" +
                "traditional recipes, and exceptional service.\n\n" +
                "Come and experience the taste of home!",
                "About Us",
                JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(orderNowButton);
        buttonPanel.add(aboutButton);

        return buttonPanel;
    }
    @Override
    public JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(166, 135, 99));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel statusLabel = new JLabel("Ready to serve you!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(new Color(52, 73, 94));

        JLabel hoursLabel = new JLabel("Open Daily: 8:00 AM - 10:00 PM");
        hoursLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        hoursLabel.setForeground(new Color(52, 73, 94));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(166, 135, 99));
        leftPanel.add(hoursLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(166, 135, 99));
        
        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(new Color(231, 71, 60));
        exitButton.setForeground(Color.BLACK);
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        exitButton.addActionListener(e -> System.exit(0));
        
        rightPanel.add(exitButton);

        footerPanel.add(statusLabel, BorderLayout.CENTER);
        footerPanel.add(leftPanel, BorderLayout.WEST);
        footerPanel.add(rightPanel, BorderLayout.EAST);

        return footerPanel;
    }

    private ImageIcon createLogoIcon() {
        try {
            java.net.URL imageURL = getClass().getResource("/assets/logo.png");
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image img = icon.getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH);
                    return new ImageIcon(img);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
        
        BufferedImage placeholder = new BufferedImage(120, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = placeholder.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillRoundRect(10, 10, 100, 80, 15, 15);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.drawString("LOGO", 45, 55);
        g2d.dispose();
        
        return new ImageIcon(placeholder);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            RestaurantHomePage.getInstance().setVisible(true);
        });
    }
}
