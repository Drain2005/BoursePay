package org.example.ui;

import org.example.model.Etudiant;
import org.example.service.BourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
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
    private JPanel mainContentPanel;

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
        mainContentPanel = new JPanel(new CardLayout());
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
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panel.setPreferredSize(new Dimension(200, 580));

        JButton etudiantsNiveauButton = createReportButton("📊 PAR NIVEAU", primaryColor);
        JButton etudiantsMineursButton = createReportButton("👶 MINEURS", secondaryColor);
        JButton retardatairesButton = createReportButton("⏰ RETARDATAIRES", accentColor);
        JButton statistiquesButton = createReportButton("📈 STATISTIQUES", new Color(155, 89, 182));
        JButton notifierButton = createReportButton("📧 NOTIFIER TOUS", new Color(52, 152, 219));
        JButton notifierIndividuelButton = createReportButton("📩 NOTIFIER UN", new Color(46, 204, 113));
        JButton exporterButton = createReportButton("💾 EXPORTER", new Color(241, 196, 15));

        etudiantsNiveauButton.addActionListener(e -> showEtudiantsParNiveau());
        etudiantsMineursButton.addActionListener(e -> showEtudiantsMineurs());
        retardatairesButton.addActionListener(e -> showParametresRetardataires());
        statistiquesButton.addActionListener(e -> showStatistiques());
        notifierButton.addActionListener(e -> notifierRetardataires());
        notifierIndividuelButton.addActionListener(e -> notifierEtudiantIndividuel());
        exporterButton.addActionListener(e -> exporterRapport());

        panel.add(etudiantsNiveauButton);
        panel.add(etudiantsMineursButton);
        panel.add(retardatairesButton);
        panel.add(statistiquesButton);
        panel.add(notifierButton);
        panel.add(notifierIndividuelButton);
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
        CardLayout cl = (CardLayout) mainContentPanel.getLayout();
        cl.show(mainContentPanel, "chart");
    }

    private void showTable() {
        CardLayout cl = (CardLayout) mainContentPanel.getLayout();
        cl.show(mainContentPanel, "table");
    }

    private void showStatistiques() {
        showChart();
        chartPanel.removeAll();

        // Récupérer les données statistiques
        int totalEtudiants = bourseService.listerEtudiants().size();
        int totalPaiements = bourseService.listerPaiements().size();
        YearMonth moisPrecedent = YearMonth.now().minusMonths(1);
        int retardataires = bourseService.obtenirRetardataires(moisPrecedent, false).size(); // FIX: Correction ici
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
                String title = "STATISTIQUES GÉNÉRALES - " + YearMonth.now().format(DateTimeFormatter.ofPattern("MMMM yyyy", new java.util.Locale("fr")));
                int titleWidth = g2d.getFontMetrics().stringWidth(title);
                g2d.drawString(title, (width - titleWidth) / 2, 25);

                // Éviter la division par zéro
                if (totalEtudiants == 0) {
                    g2d.setColor(textColor);
                    g2d.setFont(new Font("Arial", Font.BOLD, 14));
                    g2d.drawString("Aucune donnée disponible", width / 2 - 80, height / 2);
                    return;
                }

                // Dessiner le diagramme à barres
                int barWidth = chartWidth / 4;
                int maxValue = Math.max(totalEtudiants, Math.max(totalPaiements, retardataires)) + 10;

                if (maxValue > 0) {
                    // Barre des étudiants totaux
                    drawBar(g2d, padding, 50, barWidth, chartHeight, totalEtudiants, maxValue, primaryColor, "Étudiants", totalEtudiants);

                    // Barre des paiements
                    drawBar(g2d, padding + barWidth, 50, barWidth, chartHeight, totalPaiements, maxValue, secondaryColor, "Paiements", totalPaiements);

                    // Barre des retardataires
                    drawBar(g2d, padding + 2 * barWidth, 50, barWidth, chartHeight, retardataires, maxValue, new Color(231, 76, 60), "Retardataires", retardataires);

                    // Barre de régularité
                    drawBar(g2d, padding + 3 * barWidth, 50, barWidth, chartHeight, etudiantsReguliers, maxValue, accentColor, "Réguliers", etudiantsReguliers);
                }

                // Dessiner le diagramme circulaire
                int pieX = width - 200;
                int pieY = height / 2;
                int pieRadius = 80;

                // Diagramme circulaire pour la régularité
                int totalAngle = totalEtudiants > 0 ? (etudiantsReguliers * 360 / totalEtudiants) : 0;
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
                double pourcentageReguliers = totalEtudiants > 0 ? (etudiantsReguliers * 100.0 / totalEtudiants) : 0;
                g2d.drawString("Réguliers: " + etudiantsReguliers + " (" +
                                String.format("%.1f", pourcentageReguliers) + "%)",
                        pieX + pieRadius + 30, pieY - 30);

                g2d.setColor(new Color(231, 76, 60));
                g2d.fillRect(pieX + pieRadius + 10, pieY - 20, 15, 15);
                g2d.setColor(textColor);
                double pourcentageRetardataires = totalEtudiants > 0 ? (retardataires * 100.0 / totalEtudiants) : 0;
                g2d.drawString("Retardataires: " + retardataires + " (" +
                                String.format("%.1f", pourcentageRetardataires) + "%)",
                        pieX + pieRadius + 30, pieY - 10);
            }

            private void drawBar(Graphics2D g2d, int x, int y, int width, int height, int value, int maxValue, Color color, String label, int count) {
                if (maxValue <= 0) return;

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

        double tauxRegularite = totalEtudiants > 0 ? ((totalEtudiants - retardataires) * 100.0 / totalEtudiants) : 0;
        statsPanel.add(createStatBox("✅ Taux de régularité",
                String.format("%.1f%%", tauxRegularite), accentColor));

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
        int currentMonth = YearMonth.now().getMonthValue();
        int defaultMonthIndex = Math.max(0, currentMonth - 2); // Mois précédent ou 0 si janvier
        moisCombo.setSelectedIndex(defaultMonthIndex);

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
        showTable(); // Afficher la vue tableau

        String nomMois = mois.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("fr"));
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
        int currentMonth = YearMonth.now().getMonthValue();
        int defaultMonthIndex = Math.max(0, currentMonth - 2); // Mois précédent ou 0 si janvier
        moisNotifCombo.setSelectedIndex(defaultMonthIndex);

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
        // Vérifier d'abord si le délai de 3 semaines est dépassé
        LocalDateTime dateLimite = mois.atEndOfMonth().atTime(23, 59, 59).plusWeeks(3);
        LocalDateTime maintenant = LocalDateTime.now();
        boolean delaiDepasse = maintenant.isAfter(dateLimite);

        if (delaiDepasse) {
            String nomMois = mois.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("fr"));
            int annee = mois.getYear();

            JOptionPane.showMessageDialog(this,
                    "⚠️ DÉLAI DÉPASSÉ ⚠️\n\n" +
                            "Le délai de 3 semaines après la fin du mois de " + nomMois + " " + annee + " est déjà dépassé.\n" +
                            "Les notifications ne peuvent plus être envoyées pour ce mois.\n\n" +
                            "Date limite était le : " + dateLimite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")) + "\n" +
                            "Date actuelle : " + maintenant.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")),
                    "Délai dépassé", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Le délai n'est pas dépassé, on peut envoyer les notifications
        List<Etudiant> retardataires = bourseService.obtenirRetardataires(mois, false); // Sans vérification de délai

        if (retardataires.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Aucun étudiant retardataire trouvé pour ce mois.\n" +
                            "Tous les étudiants ont payé leur bourse à temps.",
                    "Aucun retardataire", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String nomMois = mois.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("fr"));
        int annee = mois.getYear();

        // Calculer le temps restant avant la date limite
        long joursRestants = java.time.temporal.ChronoUnit.DAYS.between(maintenant.toLocalDate(), dateLimite.toLocalDate());
        String tempsRestant = "";
        if (joursRestants > 0) {
            tempsRestant = "\n⏰ Temps restant avant délai : " + joursRestants + " jour(s)";
        } else {
            long heuresRestantes = java.time.temporal.ChronoUnit.HOURS.between(maintenant, dateLimite);
            tempsRestant = "\n⏰ Temps restant avant délai : " + heuresRestantes + " heure(s)";
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "📧 ENVOI DE NOTIFICATIONS\n\n" +
                        "Êtes-vous sûr de vouloir notifier " + retardataires.size() +
                        " étudiant(s) retardataire(s) pour " + nomMois + " " + annee + "?\n\n" +
                        "✉️ Les étudiants recevront un email de rappel urgent." + tempsRestant + "\n\n" +
                        "⚠️ Cette action enverra des emails à tous les retardataires.",
                "Confirmation d'envoi de notifications",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Créer une barre de progression
            JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Envoi en cours...", true);
            JProgressBar progressBar = new JProgressBar(0, retardataires.size());
            progressBar.setStringPainted(true);
            progressBar.setString("Préparation...");

            JLabel statusLabel = new JLabel("Initialisation...", SwingConstants.CENTER);

            JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
            progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            progressPanel.add(new JLabel("Envoi des notifications en cours...", SwingConstants.CENTER), BorderLayout.NORTH);
            progressPanel.add(progressBar, BorderLayout.CENTER);
            progressPanel.add(statusLabel, BorderLayout.SOUTH);

            progressDialog.add(progressPanel);
            progressDialog.setSize(400, 150);
            progressDialog.setLocationRelativeTo(this);
            progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            // Tâche d'envoi en arrière-plan
            SwingWorker<Boolean, String> emailWorker = new SwingWorker<Boolean, String>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    publish("Démarrage de l'envoi...");
                    boolean globalSuccess = false;
                    int envoyesAvecSucces = 0;

                    for (int i = 0; i < retardataires.size(); i++) {
                        Etudiant etudiant = retardataires.get(i);
                        publish("Envoi à " + etudiant.getNom() + " (" + (i + 1) + "/" + retardataires.size() + ")");

                        // Simuler l'envoi (remplacer par l'appel réel au service)
                        boolean envoyeAvecSucces = envoyerNotificationIndividuelle(etudiant, mois);

                        if (envoyeAvecSucces) {
                            envoyesAvecSucces++;
                            globalSuccess = true; // Au moins un envoi réussi
                        }

                        // Mettre à jour la barre de progression
                        final int current = i + 1;
                        final int total = retardataires.size();
                        final int reussis = envoyesAvecSucces;

                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(current);
                            progressBar.setString(reussis + "/" + current + " envoyés (" +
                                    String.format("%.0f", (current * 100.0 / total)) + "%)");
                        });

                        // Pause courte entre les envois pour éviter de surcharger le serveur email
                        Thread.sleep(500);
                    }

                    publish("Finalisation... (" + envoyesAvecSucces + "/" + retardataires.size() + " réussis)");
                    Thread.sleep(500);

                    return globalSuccess;
                }

                @Override
                protected void process(java.util.List<String> chunks) {
                    String lastMessage = chunks.get(chunks.size() - 1);
                    statusLabel.setText(lastMessage);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        progressDialog.dispose();

                        if (success) {
                            JOptionPane.showMessageDialog(RapportPanel.this,
                                    "✅ NOTIFICATIONS ENVOYÉES\n\n" +
                                            "📧 Emails de rappel envoyés avec succès!\n" +
                                            "📊 Statistiques d'envoi :\n" +
                                            "   • Retardataires notifiés : " + retardataires.size() + "\n" +
                                            "   • Mois concerné : " + nomMois + " " + annee + "\n" +
                                            "   • Date d'envoi : " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")),
                                    "Envoi réussi", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(RapportPanel.this,
                                    "❌ ERREUR D'ENVOI\n\n" +
                                            "Une erreur s'est produite lors de l'envoi des notifications.\n\n" +
                                            "🔧 Vérifications à effectuer :\n" +
                                            "   • Configuration du serveur email (SMTP)\n" +
                                            "   • Connexion Internet\n" +
                                            "   • Validité des adresses email des étudiants\n" +
                                            "   • Authentification du compte email\n\n" +
                                            "💡 Consultez les logs pour plus de détails.",
                                    "Erreur d'envoi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        progressDialog.dispose();
                        JOptionPane.showMessageDialog(RapportPanel.this,
                                "❌ ERREUR INATTENDUE\n\n" +
                                        "Une erreur inattendue s'est produite :\n" + e.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            emailWorker.execute();
            progressDialog.setVisible(true);
        }
    }

    private void notifierEtudiantIndividuel() {
        // Dialog principal pour sélectionner l'étudiant et le mois
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Notification individuelle", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(backgroundColor);

        // Panel de titre
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(backgroundColor);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel titleLabel = new JLabel("📩 NOTIFICATION INDIVIDUELLE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(primaryColor);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Panel de sélection du mois
        JPanel moisPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        moisPanel.setBackground(backgroundColor);
        moisPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor),
                "Sélectionner le mois concerné",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                primaryColor
        ));

        int currentYear = YearMonth.now().getYear();
        String[] annees = {String.valueOf(currentYear - 1), String.valueOf(currentYear), String.valueOf(currentYear + 1)};
        JComboBox<String> anneeCombo = new JComboBox<>(annees);
        anneeCombo.setSelectedItem(String.valueOf(currentYear));

        String[] mois = {"01 - Janvier", "02 - Février", "03 - Mars", "04 - Avril",
                "05 - Mai", "06 - Juin", "07 - Juillet", "08 - Août",
                "09 - Septembre", "10 - Octobre", "11 - Novembre", "12 - Décembre"};
        JComboBox<String> moisCombo = new JComboBox<>(mois);
        int currentMonth = YearMonth.now().getMonthValue();
        int defaultMonthIndex = Math.max(0, currentMonth - 2);
        moisCombo.setSelectedIndex(defaultMonthIndex);

        moisPanel.add(new JLabel("Année:"));
        moisPanel.add(anneeCombo);
        moisPanel.add(new JLabel("Mois:"));
        moisPanel.add(moisCombo);

        // Panel de sélection de l'étudiant
        JPanel etudiantPanel = new JPanel(new BorderLayout(10, 10));
        etudiantPanel.setBackground(backgroundColor);
        etudiantPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(accentColor),
                "Sélectionner l'étudiant",
                0, 0,
                new Font("Arial", Font.BOLD, 12),
                accentColor
        ));

        // Panel de recherche
        JPanel recherchePanel = new JPanel(new BorderLayout(5, 5));
        recherchePanel.setBackground(backgroundColor);

        JTextField rechercheField = new JTextField();
        rechercheField.setToolTipText("Tapez le nom ou matricule de l'étudiant...");

        JButton rechercheButton = new JButton("🔍 Rechercher");
        rechercheButton.setBackground(primaryColor);
        rechercheButton.setForeground(Color.WHITE);
        rechercheButton.setPreferredSize(new Dimension(120, 25));

        recherchePanel.add(new JLabel("Recherche:"), BorderLayout.WEST);
        recherchePanel.add(rechercheField, BorderLayout.CENTER);
        recherchePanel.add(rechercheButton, BorderLayout.EAST);

        // Liste des étudiants
        DefaultListModel<EtudiantListItem> listModel = new DefaultListModel<>();
        JList<EtudiantListItem> etudiantsList = new JList<>(listModel);
        etudiantsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        etudiantsList.setCellRenderer(new EtudiantListCellRenderer());

        JScrollPane etudiantsScrollPane = new JScrollPane(etudiantsList);
        etudiantsScrollPane.setPreferredSize(new Dimension(450, 150));

        etudiantPanel.add(recherchePanel, BorderLayout.NORTH);
        etudiantPanel.add(etudiantsScrollPane, BorderLayout.CENTER);

        // Charger tous les étudiants initialement
        List<Etudiant> tousEtudiants = bourseService.listerEtudiants();
        for (Etudiant etudiant : tousEtudiants) {
            listModel.addElement(new EtudiantListItem(etudiant));
        }

        // Action de recherche
        Runnable rechercheAction = () -> {
            String terme = rechercheField.getText().trim();
            listModel.clear();

            if (terme.isEmpty()) {
                // Afficher tous les étudiants
                for (Etudiant etudiant : tousEtudiants) {
                    listModel.addElement(new EtudiantListItem(etudiant));
                }
            } else {
                // Rechercher par nom ou matricule
                List<Etudiant> resultats = bourseService.rechercherEtudiants(terme);
                for (Etudiant etudiant : resultats) {
                    listModel.addElement(new EtudiantListItem(etudiant));
                }

                // Également rechercher par matricule si pas trouvé par nom
                for (Etudiant etudiant : tousEtudiants) {
                    if (etudiant.getMatricule().toLowerCase().contains(terme.toLowerCase()) &&
                            !resultats.contains(etudiant)) {
                        listModel.addElement(new EtudiantListItem(etudiant));
                    }
                }
            }
        };

        rechercheButton.addActionListener(e -> rechercheAction.run());
        rechercheField.addActionListener(e -> rechercheAction.run());

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(backgroundColor);

        JButton envoyerButton = new JButton("📧 Envoyer la notification");
        JButton annulerButton = new JButton("❌ Annuler");

        envoyerButton.setBackground(new Color(46, 204, 113));
        envoyerButton.setForeground(Color.WHITE);
        envoyerButton.setFont(new Font("Arial", Font.BOLD, 12));

        annulerButton.setBackground(new Color(231, 76, 60));
        annulerButton.setForeground(Color.WHITE);

        envoyerButton.addActionListener(e -> {
            EtudiantListItem selectedItem = etudiantsList.getSelectedValue();
            if (selectedItem == null) {
                JOptionPane.showMessageDialog(dialog,
                        "⚠️ Veuillez sélectionner un étudiant dans la liste.",
                        "Aucun étudiant sélectionné", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String anneeStr = (String) anneeCombo.getSelectedItem();
            String moisStr = (String) moisCombo.getSelectedItem();
            String numeroMois = moisStr.substring(0, 2);

            YearMonth moisAnnee = YearMonth.of(Integer.parseInt(anneeStr), Integer.parseInt(numeroMois));

            // Vérifier si le délai est dépassé
            if (bourseService.estDelaiDepasse(moisAnnee)) {
                String nomMois = moisAnnee.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("fr"));
                JOptionPane.showMessageDialog(dialog,
                        "⚠️ DÉLAI DÉPASSÉ ⚠️\n\n" +
                                "Le délai de 3 semaines pour " + nomMois + " " + moisAnnee.getYear() + " est dépassé.\n" +
                                "Les notifications ne peuvent plus être envoyées pour ce mois.",
                        "Délai dépassé", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dialog.dispose();
            envoyerNotificationIndividuelle(selectedItem.getEtudiant(), moisAnnee);
        });

        annulerButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(envoyerButton);
        buttonPanel.add(annulerButton);

        // Assemblage du dialog
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        mainPanel.add(moisPanel, BorderLayout.NORTH);
        mainPanel.add(etudiantPanel, BorderLayout.CENTER);

        dialog.add(titlePanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void envoyerNotificationIndividuelle(Etudiant etudiant, YearMonth mois) {
        // Vérifier si l'étudiant est vraiment retardataire pour ce mois
        boolean estRetardataire = !bourseService.etudiantAPayePourMois(
                etudiant.getMatricule(), etudiant.getAnneeUniv(), mois);

        String nomMois = mois.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("fr"));
        int annee = mois.getYear();

        if (!estRetardataire) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "ℹ️ ÉTUDIANT À JOUR\n\n" +
                            etudiant.getNom() + " (Matricule: " + etudiant.getMatricule() + ")\n" +
                            "a déjà payé sa bourse pour " + nomMois + " " + annee + ".\n\n" +
                            "Voulez-vous quand même envoyer une notification de rappel ?",
                    "Étudiant à jour",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String tempsRestant = bourseService.getTempsRestantDelai(mois);

        int confirm = JOptionPane.showConfirmDialog(this,
                "📧 CONFIRMATION D'ENVOI\n\n" +
                        "Étudiant : " + etudiant.getNom() + "\n" +
                        "Matricule : " + etudiant.getMatricule() + "\n" +
                        "Email : " + etudiant.getMail() + "\n" +
                        "Institution : " + etudiant.getInstitution() + "\n" +
                        "Niveau : " + etudiant.getIdniv() + "\n\n" +
                        "Mois concerné : " + nomMois + " " + annee + "\n" +
                        "⏰ " + tempsRestant + "\n" +
                        "Statut : " + (estRetardataire ? "❌ Retardataire" : "✅ À jour") + "\n\n" +
                        "Confirmez-vous l'envoi de la notification ?",
                "Confirmation d'envoi individuel",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Créer une barre de progression simple
            JDialog progressDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                    "Envoi en cours...", true);
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setString("Envoi de la notification...");
            progressBar.setStringPainted(true);

            JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
            progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            progressPanel.add(new JLabel("Envoi de la notification à " + etudiant.getNom() + "...", SwingConstants.CENTER), BorderLayout.NORTH);
            progressPanel.add(progressBar, BorderLayout.CENTER);

            progressDialog.add(progressPanel);
            progressDialog.setSize(400, 120);
            progressDialog.setLocationRelativeTo(this);
            progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            // Tâche d'envoi en arrière-plan
            SwingWorker<Boolean, Void> emailWorker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return bourseService.envoyerNotificationIndividuelle(etudiant, mois);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        progressDialog.dispose();

                        if (success) {
                            JOptionPane.showMessageDialog(RapportPanel.this,
                                    "✅ NOTIFICATION ENVOYÉE\n\n" +
                                            "📧 La notification a été envoyée avec succès !\n\n" +
                                            "Destinataire : " + etudiant.getNom() + "\n" +
                                            "Email : " + etudiant.getMail() + "\n" +
                                            "Mois : " + nomMois + " " + annee + "\n" +
                                            "Date d'envoi : " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")),
                                    "Envoi réussi", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(RapportPanel.this,
                                    "❌ ERREUR D'ENVOI\n\n" +
                                            "La notification n'a pas pu être envoyée à " + etudiant.getNom() + ".\n\n" +
                                            "🔧 Vérifications à effectuer :\n" +
                                            "   • Adresse email valide : " + etudiant.getMail() + "\n" +
                                            "   • Configuration du serveur SMTP\n" +
                                            "   • Connexion Internet\n" +
                                            "   • Authentification email\n\n" +
                                            "💡 Consultez les logs pour plus de détails.",
                                    "Erreur d'envoi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        progressDialog.dispose();
                        JOptionPane.showMessageDialog(RapportPanel.this,
                                "❌ ERREUR INATTENDUE\n\n" +
                                        "Une erreur inattendue s'est produite :\n" + e.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            emailWorker.execute();
            progressDialog.setVisible(true);
        }
    }

    /**
     * Méthode helper pour envoyer une notification individuelle
     * MAINTENANT À L'INTÉRIEUR DE LA CLASSE
     */
    private boolean envoyerNotificationIndividuelle(Etudiant etudiant, YearMonth mois) {
        try {
            String nomMois = mois.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("fr"));
            int annee = mois.getYear();

            String sujet = String.format("RAPPEL URGENT - Paiement de bourse en retard (%s %d)", nomMois, annee);

            String message = String.format(
                    "Bonjour %s,\n\n" +
                            "Nous vous informons que votre paiement de bourse pour le mois de %s %d " +
                            "n'a pas encore été effectué.\n\n" +
                            "INFORMATIONS DE VOTRE DOSSIER :\n" +
                            "• Matricule : %s\n" +
                            "• Année universitaire : %s\n" +
                            "• Institution : %s\n" +
                            "• Niveau : %s\n\n" +
                            "⚠️ ATTENTION : Le délai de paiement expire bientôt !\n" +
                            "Veuillez régulariser votre situation IMMÉDIATEMENT auprès du service des bourses.\n\n" +
                            "📞 Pour toute question ou assistance, contactez-nous dès que possible.\n\n" +
                            "Cordialement,\n" +
                            "Service de Gestion des Bourses Étudiantes\n\n" +
                            "---\n" +
                            "Ceci est un message automatique. Ne pas répondre directement à cet email.",

                    etudiant.getNom(),
                    nomMois, annee,
                    etudiant.getMatricule(),
                    etudiant.getAnneeUniv(),
                    etudiant.getInstitution(),
                    etudiant.getIdniv()
            );

            // Appeler le service d'email (remplacer par votre implémentation)
            // Ici on simule l'appel au service
            return bourseService.envoyerEmailIndividuel(etudiant.getMail(), sujet, message);

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi à " + etudiant.getNom() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Classe pour représenter un étudiant dans la liste
     */
    private static class EtudiantListItem {
        private final Etudiant etudiant;

        public EtudiantListItem(Etudiant etudiant) {
            this.etudiant = etudiant;
        }

        public Etudiant getEtudiant() {
            return etudiant;
        }

        @Override
        public String toString() {
            return etudiant.getMatricule() + " - " + etudiant.getNom() +
                    " (" + etudiant.getInstitution() + " - " + etudiant.getIdniv() + ")";
        }
    }

    /**
     * Renderer personnalisé pour la liste des étudiants
     */
    private class EtudiantListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof EtudiantListItem) {
                EtudiantListItem item = (EtudiantListItem) value;
                Etudiant etudiant = item.getEtudiant();

                String text = "<html>" +
                        "<b>" + etudiant.getMatricule() + "</b> - " + etudiant.getNom() + "<br>" +
                        "<small><i>" + etudiant.getInstitution() + " • " + etudiant.getIdniv() +
                        " • " + etudiant.getMail() + "</i></small>" +
                        "</html>";

                setText(text);

                if (isSelected) {
                    setBackground(primaryColor);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
            }

            return c;
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
        showTable(); // Afficher la vue tableau
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
        showTable(); // Afficher la vue tableau
        JOptionPane.showMessageDialog(this,
                mineurs.size() + " étudiant(s) mineur(s) trouvé(s)",
                "Résultats", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exporterRapport() {
        if (tableModel == null || tableModel.getRowCount() == 0) {
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