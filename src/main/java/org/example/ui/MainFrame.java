package org.example.ui;

import org.example.service.BourseService;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final BourseService bourseService;
    private JTabbedPane tabbedPane;

    // Couleurs modernes - LES VÔTRES
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);     // Bleu foncé
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Bleu moyen
    private final Color ACCENT_COLOR = new Color(46, 204, 113);     // Vert
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Gris clair
    private final Color TEXT_COLOR = new Color(51, 51, 51);          // Gris foncé

    public MainFrame() {
        this.bourseService = new BourseService();
        initializeUI();
        setTitle("GESTION DES BOURSES D'ÉTUDIANTS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        // DÉSACTIVEZ le Look and Feel système pour garder VOS couleurs
        // NE mettez PAS UIManager.setLookAndFeel ici

        // Panel principal avec VOS couleurs
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // En-tête avec VOS couleurs
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Onglets avec VOS couleurs
        tabbedPane = new JTabbedPane();
        customizeTabbedPane(tabbedPane);

        tabbedPane.addTab("👨‍🎓 ÉTUDIANTS", new EtudiantPanel(bourseService, PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, BACKGROUND_COLOR, TEXT_COLOR));
        tabbedPane.addTab("💰 MONTANTS", new MontantPanel(bourseService, PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, BACKGROUND_COLOR, TEXT_COLOR));
        tabbedPane.addTab("💳 PAIEMENTS", new PaiementPanel(bourseService, PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, BACKGROUND_COLOR, TEXT_COLOR));
        tabbedPane.addTab("📊 RAPPORTS", new RapportPanel(bourseService, PRIMARY_COLOR, SECONDARY_COLOR, ACCENT_COLOR, BACKGROUND_COLOR, TEXT_COLOR));


        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Barre de statut avec VOS couleurs
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR); // VOTRE couleur bleue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel titleLabel = new JLabel("GESTION DES BOURSES D'ÉTUDIANTS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Système de gestion intégrée des bourses étudiantes", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(PRIMARY_COLOR); // VOTRE couleur
        textPanel.add(titleLabel, BorderLayout.CENTER);
        textPanel.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(textPanel, BorderLayout.CENTER);

        // Logo ou icône
        JLabel logoLabel = new JLabel("🎓", SwingConstants.RIGHT);
        logoLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private void customizeTabbedPane(JTabbedPane tabbedPane) {
        // FORCEZ les couleurs pour les onglets
        tabbedPane.setBackground(BACKGROUND_COLOR); // VOTRE couleur de fond
        tabbedPane.setForeground(TEXT_COLOR); // VOTRE couleur de texte

        // Style des onglets sélectionnés
        tabbedPane.setFocusable(false);
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR); // VOTRE couleur
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(TEXT_COLOR); // VOTRE couleur

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(60, 60, 60)); // Gris foncé
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 25));

        JLabel statusLabel = new JLabel("✅ Connecté à la base de données | © 2024 Gestion des Bourses");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("Utilisateur: Admin");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setForeground(Color.WHITE);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(userLabel, BorderLayout.EAST);

        return statusPanel;
    }
}