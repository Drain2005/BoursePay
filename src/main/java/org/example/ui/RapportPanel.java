package org.example.ui;

import org.example.model.Etudiant;
import org.example.service.BourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class RapportPanel extends JPanel {
    private final BourseService bourseService;
    private final Color primaryColor;
    private final Color secondaryColor;
    private final Color accentColor;
    private final Color backgroundColor;
    private final Color textColor;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> moisCombo;
    private JComboBox<String> anneeCombo;
    private JScrollPane tableScrollPane;
    private JPanel chartPanel;

    public RapportPanel(BourseService bourseService, Color primary, Color secondary, Color accent, Color background, Color text) {
        this.bourseService = bourseService;
        this.primaryColor = primary;
        this.secondaryColor = secondary;
        this.accentColor = accent;
        this.backgroundColor = background;
        this.textColor = text;

        setLayout(new BorderLayout(10, 10));
        setBackground(backgroundColor);
        initializeUI();
    }

    private void initializeUI() {
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de titre
        add(createTitlePanel(), BorderLayout.NORTH);

        // Panel d'options avec boutons
        add(createOptionsPanel(), BorderLayout.WEST);

        // Panel principal pour les résultats (tableau ou graphique)
        JPanel mainContentPanel = new JPanel(new CardLayout());
        mainContentPanel.setBackground(backgroundColor);

        // Panel pour le tableau
        tableScrollPane = new JScrollPane();
        tableScrollPane.setPreferredSize(new Dimension(950, 400));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                "Résultats du rapport",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                primaryColor
        ));

        // Panel pour le graphique statistique
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setPreferredSize(new Dimension(950, 400));
        chartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                "Statistiques - Diagramme",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                primaryColor
        ));
        chartPanel.setBackground(backgroundColor);

        mainContentPanel.add(tableScrollPane, "table");
        mainContentPanel.add(chartPanel, "chart");

        add(mainContentPanel, BorderLayout.CENTER);

        // Initialiser le tableau
        table = new JTable();
        tableScrollPane.setViewportView(table);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("RAPPORTS ET STATISTIQUES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(primaryColor);

        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createOptionsPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.setPreferredSize(new Dimension(200, 500));

        JButton etudiantsNiveauButton = createReportButton("📊 PAR NIVEAU", primaryColor);
        JButton etudiantsMineursButton = createReportButton("👶 MINEURS", secondaryColor);
        JButton retardatairesButton = createReportButton(" RETARDATAIRES", accentColor);
        JButton statistiquesButton = createReportButton("📈 STATISTIQUES", new Color(155, 89, 182));
        JButton notifierButton = createReportButton("📧 NOTIFIER", new Color(52, 152, 219));
        JButton exporterButton = createReportButton("💾 EXPORTER", new Color(241, 196, 15));

        etudiantsNiveauButton.addActionListener(e -> showEtudiantsParNiveau());
        etudiantsMineursButton.addActionListener(e -> showEtudiantsMineurs());
        retardatairesButton.addActionListener(e -> showParametresRetardataires());
        statistiquesButton.addActionListener(e -> showStatistiques());
        notifierButton.addActionListener(e -> notifierRetardataires());
        exporterButton.addActionListener(e -> exporterRapport());

        panel.add(etudiantsNiveauButton);
        panel.add(etudiantsMineursButton);
        panel.add(retardatairesButton);
        panel.add(statistiquesButton);
        panel.add(notifierButton);
        panel.add(exporterButton);

        return panel;
    }

    private JButton createReportButton(String text, Color color) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void showChart() {
        CardLayout cl = (CardLayout) ((JPanel) getComponent(2)).getLayout();
        cl.show(((JPanel) getComponent(2)), "chart");
    }

    private void showTable() {
        CardLayout cl = (CardLayout) ((JPanel) getComponent(2)).getLayout();
        cl.show(((JPanel) getComponent(2)), "table");
    }

    private void showStatistiques() {
        showChart();
        chartPanel.removeAll();

        // Récupérer les données statistiques
        int totalEtudiants = bourseService.listerEtudiants().size();
        int totalPaiements = bourseService.listerPaiements().size();
        YearMonth moisPrecedent = YearMonth.now().minusMonths(1);
        int retardataires = bourseService.obtenirRetardataires(YearMonth).size();
        int etudiantsReguliers = totalEtudiants - retardataires;

        // Créer le diagramme
        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int padding = 40;
                int chartWidth = width - 2 * padding;
                int chartHeight = height - 2 * padding;

                // Dessiner le fond
                g2d.setColor(backgroundColor);
                g2d.fillRect(0, 0, width, height);

                // Dessiner le titre
                g2d.setColor(textColor);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                String title = "STATISTIQUES GÉNÉRALES - " + YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                int titleWidth = g2d.getFontMetrics().stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);

                // Dessiner le diagramme à barres
                int barWidth = chartWidth / 4;
                int maxValue = Math.max(totalEtudiants, Math.max(totalPaiements, retardataires)) + 10;

                // Barre des étudiants totaux
                drawBar(g2d, padding, 50, barWidth, chartHeight, totalEtudiants, maxValue, primaryColor, "Étudiants", totalEtudiants);

                // Barre des paiements
                drawBar(g2d, padding + barWidth, 50, barWidth, chartHeight, totalPaiements, maxValue, secondaryColor, "Paiements", totalPaiements);

                // Barre des retardataires
                drawBar(g2d, padding + 2 * barWidth, 50, barWidth, chartHeight, retardataires, maxValue, new Color(231, 76, 60), "Retardataires", retardataires);

                // Barre de régularité
                drawBar(g2d, padding + 3 * barWidth, 50, barWidth, chartHeight, etudiantsReguliers, maxValue, accentColor, "Réguliers", etudiantsReguliers);

                // Dessiner le diagramme circulaire
                int pieX = width - 200;
                int pieY = height / 2;
                int pieRadius = 80;

                // Diagramme circulaire pour la régularité
                int totalAngle = etudiantsReguliers * 360 / totalEtudiants;
                g2d.setColor(accentColor);
                g2d.fillArc(pieX - pieRadius, pieY - pieRadius, pieRadius * 2, pieRadius * 2, 0, totalAngle);
                g2d.setColor(new Color(231, 76, 60));
                g2d.fillArc(pieX - pieRadius, pieY - pieRadius, pieRadius * 2, pieRadius * 2, totalAngle, 360 - totalAngle);

                // Légende du diagramme circulaire
                g2d.setColor(textColor);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString("RÉGULARITÉ", pieX - 30, pieY - pieRadius - 10);

                g2d.setColor(accentColor);
                g2d.fillRect(pieX + pieRadius + 10, pieY - 40, 15, 15);
                g2d.setColor(textColor);
                g2d.drawString("Réguliers: " + etudiantsReguliers + " (" +
                                String.format("%.1f", (etudiantsReguliers * 100.0 / totalEtudiants)) + "%)",
                        pieX + pieRadius + 30, pieY - 30);

                g2d.setColor(new Color(231, 76, 60));
                g2d.fillRect(pieX + pieRadius + 10, pieY - 20, 15, 15);
                g2d.setColor(textColor);
                g2d.drawString("Retardataires: " + retardataires + " (" +
                                String.format("%.1f", (retardataires * 100.0 / totalEtudiants)) + "%)",
                        pieX + pieRadius + 30, pieY - 10);
            }

            private void drawBar(Graphics2D g2d, int x, int y, int width, int height, int value, int maxValue, Color color, String label, int count) {
                int barHeight = (int) ((double) value / maxValue * height);
                int barY = y + height - barHeight;

                // Dessiner la barre
                g2d.setColor(color);
                g2d.fillRect(x, barY, width - 10, barHeight);

                // Dessiner le contour
                g2d.setColor(color.darker());
                g2d.drawRect(x, barY, width - 10, barHeight);

                // Dessiner la valeur
                g2d.setColor(textColor);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                String valueStr = String.valueOf(count);
                int valueWidth = g2d.getFontMetrics().stringWidth(valueStr);
                g2d.drawString(valueStr, x + (width - 10 - valueWidth) / 2, barY - 5);

                // Dessiner le label
                g2d.setFont(new Font("Arial", Font.PLAIN, 11));
                int labelWidth = g2d.getFontMetrics().stringWidth(label);
                g2d.drawString(label, x + (width - 10 - labelWidth) / 2, y + height + 20);
            }
        };

        chart.setBackground(backgroundColor);
        chartPanel.add(chart, BorderLayout.CENTER);

        // Ajouter les statistiques textuelles
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        statsPanel.setBackground(backgroundColor);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statsPanel.add(createStatBox("🎓 Étudiants totaux", String.valueOf(totalEtudiants), primaryColor));
        statsPanel.add(createStatBox("💳 Paiements totaux", String.valueOf(totalPaiements), secondaryColor));
        statsPanel.add(createStatBox("⏰ Retardataires", String.valueOf(retardataires), new Color(231, 76, 60)));
        statsPanel.add(createStatBox("✅ Taux de régularité",
                String.format("%.1f%%", ((totalEtudiants - retardataires) * 100.0 / totalEtudiants)), accentColor));

        chartPanel.add(statsPanel, BorderLayout.SOUTH);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private JPanel createStatBox(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color.brighter());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(color.darker());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }


    private void showParametresRetardataires() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Paramètres des retardataires", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(backgroundColor);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Année en cours et années précédentes
        int currentYear = YearMonth.now().getYear();
        String[] annees = {String.valueOf(currentYear - 1), String.valueOf(currentYear), String.valueOf(currentYear + 1)};
        anneeCombo = new JComboBox<>(annees);
        anneeCombo.setSelectedItem(String.valueOf(currentYear));

        // Mois
        String[] mois = {"01 - Janvier", "02 - Février", "03 - Mars", "04 - Avril",
                "05 - Mai", "06 - Juin", "07 - Juillet", "08 - Août",
                "09 - Septembre", "10 - Octobre", "11 - Novembre", "12 - Décembre"};
        moisCombo = new JComboBox<>(mois);
        moisCombo.setSelectedIndex(YearMonth.now().getMonthValue() - 2); // Mois précédent par défaut

        formPanel.add(new JLabel("Année:"));
        formPanel.add(anneeCombo);
        formPanel.add(new JLabel("Mois:"));
        formPanel.add(moisCombo);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(backgroundColor);

        JButton validerButton = new JButton("✅ Afficher les retardataires");
        JButton annulerButton = new JButton("❌ Annuler");

        validerButton.setBackground(accentColor);
        validerButton.setForeground(Color.WHITE);
        annulerButton.setBackground(new Color(231, 76, 60));
        annulerButton.setForeground(Color.WHITE);

        validerButton.addActionListener(e -> {
            String annee = (String) anneeCombo.getSelectedItem();
            String moisSelectionne = (String) moisCombo.getSelectedItem();
            String numeroMois = moisSelectionne.substring(0, 2);

            YearMonth moisAnnee = YearMonth.of(Integer.parseInt(annee), Integer.parseInt(numeroMois));
            showRetardataires(moisAnnee);
            dialog.dispose();
        });

        annulerButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(validerButton);
        buttonPanel.add(annulerButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showRetardataires(YearMonth mois) {
        List<Etudiant> retardataires = bourseService.obtenirRetardataires(mois, false);

        String[] columns = {"Matricule", "Nom", "Année Univ.", "Institution", "Niveau", "Email", "Téléphone"};
        tableModel = new DefaultTableModel(columns, 0);

        for (Etudiant etudiant : retardataires) {
            tableModel.addRow(new Object[]{
                    etudiant.getMatricule(),
                    etudiant.getNom(),
                    etudiant.getAnneeUniv(),
                    etudiant.getInstitution(),
                    etudiant.getIdniv(),
                    etudiant.getMail(),
                    "Non disponible" // Vous devriez ajouter un champ téléphone dans Etudiant
            });
        }

        setupTable();
        showTable(); // ← AJOUTEZ CETTE LIGNE CRUCIALE

        String nomMois = mois.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.FRENCH);
        int annee = mois.getYear();

        JOptionPane.showMessageDialog(this,
                retardataires.size() + " étudiant(s) retardataire(s) trouvé(s) pour " + nomMois + " " + annee,
                "Résultats", JOptionPane.INFORMATION_MESSAGE);
    }

    private void notifierRetardataires() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Notifier les retardataires", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(backgroundColor);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        int currentYear = YearMonth.now().getYear();
        String[] annees = {String.valueOf(currentYear - 1), String.valueOf(currentYear), String.valueOf(currentYear + 1)};
        JComboBox<String> anneeNotifCombo = new JComboBox<>(annees);
        anneeNotifCombo.setSelectedItem(String.valueOf(currentYear));

        String[] mois = {"01 - Janvier", "02 - Février", "03 - Mars", "04 - Avril",
                "05 - Mai", "06 - Juin", "07 - Juillet", "08 - Août",
                "09 - Septembre", "10 - Octobre", "11 - Novembre", "12 - Décembre"};
        JComboBox<String> moisNotifCombo = new JComboBox<>(mois);
        moisNotifCombo.setSelectedIndex(YearMonth.now().getMonthValue() - 2);

        formPanel.add(new JLabel("Année:"));
        formPanel.add(anneeNotifCombo);
        formPanel.add(new JLabel("Mois:"));
        formPanel.add(moisNotifCombo);
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(backgroundColor);

        JButton validerButton = new JButton("📧 Envoyer les notifications");
        JButton annulerButton = new JButton("❌ Annuler");

        validerButton.setBackground(accentColor);
        validerButton.setForeground(Color.WHITE);
        annulerButton.setBackground(new Color(231, 76, 60));
        annulerButton.setForeground(Color.WHITE);

        validerButton.addActionListener(e -> {
            String annee = (String) anneeNotifCombo.getSelectedItem();
            String moisSelectionne = (String) moisNotifCombo.getSelectedItem();
            String numeroMois = moisSelectionne.substring(0, 2);

            YearMonth moisAnnee = YearMonth.of(Integer.parseInt(annee), Integer.parseInt(numeroMois));
            envoyerNotifications(moisAnnee);
            dialog.dispose();
        });

        annulerButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(validerButton);
        buttonPanel.add(annulerButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void envoyerNotifications(YearMonth mois) {
        List<Etudiant> retardataires = bourseService.obtenirRetardataires(mois, true);

        if (retardataires.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Aucun retardataire trouvé pour ce mois",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nomMois = mois.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.FRENCH);
        int annee = mois.getYear();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir notifier " + retardataires.size() +
                        " retardataire(s) pour " + nomMois + " " + annee + "?\n\n" +
                        "Les étudiants recevront un email de rappel.",
                "Confirmation d'envoi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bourseService.notifierRetardataires(mois);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Notifications envoyées avec succès!\n" +
                                retardataires.size() + " email(s) de rappel envoyé(s) aux retardataires.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'envoi des notifications.\n" +
                                "Vérifiez la configuration email.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEtudiantsParNiveau() {
        Map<String, List<Etudiant>> result = bourseService.listerEtudiantsParNiveauEtEtablissement();

        String[] columns = {"Groupe", "Nombre d'étudiants"};
        tableModel = new DefaultTableModel(columns, 0);

        for (Map.Entry<String, List<Etudiant>> entry : result.entrySet()) {
            tableModel.addRow(new Object[]{entry.getKey(), entry.getValue().size()});
        }

        setupTable();
        JOptionPane.showMessageDialog(this,
                result.size() + " groupes trouvés",
                "Résultats", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showEtudiantsMineurs() {
        List<Etudiant> mineurs = bourseService.listerEtudiantsMineurs();

        String[] columns = {"Matricule", "Nom", "Âge", "Institution", "Niveau"};
        tableModel = new DefaultTableModel(columns, 0);

        for (Etudiant etudiant : mineurs) {
            tableModel.addRow(new Object[]{
                    etudiant.getMatricule(),
                    etudiant.getNom(),
                    etudiant.getAge(),
                    etudiant.getInstitution(),
                    etudiant.getIdniv()
            });
        }

        setupTable();
        JOptionPane.showMessageDialog(this,
                mineurs.size() + " étudiant(s) mineur(s) trouvé(s)",
                "Résultats", JOptionPane.INFORMATION_MESSAGE);
    }


    private void exporterRapport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Aucune donnée à exporter. Veuillez d'abord générer un rapport.",
                    "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter le rapport");
        fileChooser.setSelectedFile(new java.io.File("rapport_retardataires_" +
                java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this,
                    "Fonctionnalité d'exportation à implémenter complètement.\n" +
                            "Fichier: " + fileToSave.getName(),
                    "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void setupTable() {
        table.setModel(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));

        // Centrer toutes les cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style de l'en-tête
        JTableHeader header = table.getTableHeader();
        header.setBackground(primaryColor);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(primaryColor);
        headerRenderer.setForeground(Color.WHITE);

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Ajuster la largeur des colonnes pour remplir l'espace
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Calculer la largeur totale disponible (environ 950px comme les autres panels)
        int totalWidth = 950;
        int[] columnWidths;

        switch (table.getColumnCount()) {
            case 2: // Étudiants par niveau
                columnWidths = new int[]{700, 250};
                break;
            case 5: // Étudiants mineurs
                columnWidths = new int[]{100, 200, 80, 200, 100};
                break;
            case 7: // Retardataires
                columnWidths = new int[]{100, 150, 100, 150, 80, 200, 120};
                break;
            default:
                columnWidths = new int[table.getColumnCount()];
                int defaultWidth = totalWidth / table.getColumnCount();
                for (int i = 0; i < columnWidths.length; i++) {
                    columnWidths[i] = defaultWidth;
                }
        }

        // Appliquer les largeurs
        for (int i = 0; i < table.getColumnCount() && i < columnWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // Forcer le redimensionnement
        tableScrollPane.revalidate();
        tableScrollPane.repaint();
    }
}