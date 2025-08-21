package org.example.ui;

import org.example.model.Etudiant;
import org.example.model.Montant;
import org.example.model.Payer;
import org.example.service.BourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaiementPanel extends JPanel {
    private final BourseService bourseService;
    private final Color primaryColor;
    private final Color secondaryColor;
    private final Color accentColor;
    private final Color backgroundColor;
    private final Color textColor;

    private JTable table;
    private DefaultTableModel tableModel;

    // Champs du formulaire
    private JTextField matriculeField;
    private JTextField anneeField;
    private JSpinner moisSpinner;
    private JCheckBox equipementCheckBox;
    private JLabel montantTotalLabel;

    public PaiementPanel(BourseService bourseService, Color primary, Color secondary, Color accent, Color background, Color text) {
        this.bourseService = bourseService;
        this.primaryColor = primary;
        this.secondaryColor = secondary;
        this.accentColor = accent;
        this.backgroundColor = background;
        this.textColor = text;

        setLayout(new BorderLayout(10, 10));
        setBackground(backgroundColor);
        initializeUI();
        loadPaiements();
    }

    private void initializeUI() {
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de titre
        add(createTitlePanel(), BorderLayout.NORTH);

        // Panel de contenu avec tableau et formulaire
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createTablePanel(), createFormPanel());
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        // Barre d'outils en bas
        add(createToolbarPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("GESTION DES PAIEMENTS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(primaryColor);

        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                "Liste des paiements",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                primaryColor
        ));
        panel.setBackground(backgroundColor);

        // SUPPRIMÉ LA COLONNE "ID Paiement"
        String[] columns = {"Matricule", "Année Universitaire", "Date", "Nombre Mois", "Équipement"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setSelectionBackground(secondaryColor);
        table.setSelectionForeground(Color.WHITE);

        // Centrer toutes les cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style de l'en-tête du tableau
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 250));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(secondaryColor, 2),
                "Formulaire Paiement",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                secondaryColor
        ));
        panel.setBackground(backgroundColor);
        panel.setPreferredSize(new Dimension(800, 180));

        // Panel principal du formulaire avec GridBagLayout pour un alignement parfait
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Initialiser les champs
        matriculeField = createFormTextField();
        matriculeField.setPreferredSize(new Dimension(150, 25));

        anneeField = createFormTextField();
        anneeField.setPreferredSize(new Dimension(150, 25));

        moisSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) moisSpinner.getEditor();
        spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        moisSpinner.setPreferredSize(new Dimension(80, 25));

        equipementCheckBox = new JCheckBox("Inclure équipement");
        equipementCheckBox.setFont(new Font("Arial", Font.BOLD, 12));
        equipementCheckBox.setBackground(backgroundColor);
        equipementCheckBox.setForeground(textColor);
        equipementCheckBox.addActionListener(e -> calculerMontantTotal());

        moisSpinner.addChangeListener(e -> calculerMontantTotal());

        montantTotalLabel = new JLabel("Montant total: 0 Ar");
        montantTotalLabel.setFont(new Font("Arial", Font.BOLD, 12));
        montantTotalLabel.setForeground(primaryColor);

        // Première ligne - Matricule
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Matricule:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(matriculeField, gbc);

        // Deuxième ligne - Année universitaire
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Année Universitaire:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        formPanel.add(anneeField, gbc);

        // Troisième ligne - Nombre de mois
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Nombre de Mois:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.7;
        formPanel.add(moisSpinner, gbc);

        // Quatrième ligne - Équipement
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Options:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.7;
        formPanel.add(equipementCheckBox, gbc);

        // Cinquième ligne - Montant total
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Total:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.7;
        formPanel.add(montantTotalLabel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private void calculerMontantTotal() {
        String matricule = matriculeField.getText().trim();
        String anneeUniv = anneeField.getText().trim();

        if (matricule.isEmpty() || anneeUniv.isEmpty()) {
            montantTotalLabel.setText("Montant total: 0 Ar");
            return;
        }

        // Vérifier si l'étudiant existe et récupérer son niveau
        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant == null) {
            montantTotalLabel.setText("Montant total: 0 Ar (Étudiant non trouvé)");
            return;
        }

        // Récupérer le montant du niveau
        Montant montantNiveau = bourseService.obtenirMontant(etudiant.getIdniv());
        if (montantNiveau == null) {
            montantTotalLabel.setText("Montant total: 0 Ar (Niveau non trouvé)");
            return;
        }

        int nbrMois = (Integer) moisSpinner.getValue();
        boolean avecEquipement = equipementCheckBox.isSelected();

        // Récupérer le montant de l'équipement si sélectionné
        int montantEquipement = 0;
        if (avecEquipement) {
            Montant equipement = bourseService.obtenirMontant("EQUIP");
            if (equipement != null) {
                montantEquipement = equipement.getMontant();
            }
        }

        int total = (montantNiveau.getMontant() * nbrMois) + montantEquipement;
        montantTotalLabel.setText(String.format("Montant total: %,d Ar", total));
    }

    private void clearForm() {
        matriculeField.setText("");
        anneeField.setText("");
        moisSpinner.setValue(1);
        equipementCheckBox.setSelected(false);
        montantTotalLabel.setText("Montant total: 0 Ar");
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(textColor);
        return label;
    }

    private JTextField createFormTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(secondaryColor, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        // Ajouter un écouteur pour recalculer le montant quand les champs changent
        textField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculerMontantTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculerMontantTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculerMontantTotal(); }
        });
        return textField;
    }

    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton addButton = createStyledButton(" EFFECTUER PAIEMENT", accentColor);
        JButton deleteButton = createStyledButton(" SUPPRIMER", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton(" ACTUALISER", new Color(155, 89, 182));
        JButton pdfButton = createStyledButton(" GÉNÉRER PDF", new Color(52, 152, 219));
        JButton clearButton = createStyledButton(" NOUVEAU", new Color(150, 150, 150));

        addButton.addActionListener(e -> effectuerPaiement());
        deleteButton.addActionListener(e -> deletePaiement());
        refreshButton.addActionListener(e -> loadPaiements());
        pdfButton.addActionListener(e -> genererPDF());
        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        panel.add(pdfButton);
        panel.add(clearButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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

    private void loadPaiements() {
        tableModel.setRowCount(0);
        List<Payer> paiements = bourseService.listerPaiements();

        for (Payer paiement : paiements) {
            // Pour l'affichage, on indique si le paiement incluait l'équipement
            String equipementInfo = "Non"; // Par défaut

            tableModel.addRow(new Object[]{
                    paiement.getMatricule(),
                    paiement.getAnneeUniv(),
                    paiement.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    paiement.getNbrMois(),
                    equipementInfo
            });
        }
    }

    private void effectuerPaiement() {
        try {
            String matricule = matriculeField.getText().trim();
            String anneeUniv = anneeField.getText().trim();
            int nbrMois = (Integer) moisSpinner.getValue();
            boolean avecEquipement = equipementCheckBox.isSelected();

            if (matricule.isEmpty() || anneeUniv.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Les champs sont obligatoires",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier si l'étudiant existe
            Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
            if (etudiant == null) {
                JOptionPane.showMessageDialog(this,
                        "Aucun étudiant trouvé avec ce matricule et année universitaire",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calculer le montant total pour confirmation
            int montantTotal = calculerMontantTotalPourConfirmation(etudiant, nbrMois, avecEquipement);

            // Demander confirmation
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirmer le paiement:\n\n" +
                            "Étudiant: " + etudiant.getNom() + "\n" +
                            "Matricule: " + matricule + "\n" +
                            "Mois payés: " + nbrMois + "\n" +
                            "Équipement: " + (avecEquipement ? "Oui" : "Non") + "\n" +
                            "Montant total: " + String.format("%,d", montantTotal) + " Ar\n\n" +
                            "Êtes-vous sûr de vouloir effectuer ce paiement?",
                    "Confirmation de paiement",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Effectuer le paiement principal (mois)
            boolean success = bourseService.effectuerPaiement(matricule, anneeUniv, nbrMois);

            if (success && avecEquipement) {
                // Si équipement est coché, effectuer un paiement supplémentaire pour l'équipement
                bourseService.effectuerPaiement(matricule, anneeUniv, 0); // 0 mois pour l'équipement
            }

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Paiement effectué avec succès!\n" +
                                "Étudiant: " + etudiant.getNom() + "\n" +
                                "Mois payés: " + nbrMois + "\n" +
                                "Équipement: " + (avecEquipement ? "Oui" : "Non") + "\n" +
                                "Montant total: " + String.format("%,d", montantTotal) + " Ar",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadPaiements();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du paiement",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calculerMontantTotalPourConfirmation(Etudiant etudiant, int nbrMois, boolean avecEquipement) {
        Montant montantNiveau = bourseService.obtenirMontant(etudiant.getIdniv());
        if (montantNiveau == null) return 0;

        int montantEquipement = 0;
        if (avecEquipement) {
            Montant equipement = bourseService.obtenirMontant("EQUIP");
            if (equipement != null) {
                montantEquipement = equipement.getMontant();
            }
        }

        return (montantNiveau.getMontant() * nbrMois) + montantEquipement;
    }

    private void deletePaiement() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un paiement à supprimer",
                    "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String matricule = (String) tableModel.getValueAt(selectedRow, 0);
        String anneeUniv = (String) tableModel.getValueAt(selectedRow, 1);
        int nbrMois = (Integer) tableModel.getValueAt(selectedRow, 3);

        // Pour supprimer, on a besoin de l'ID du paiement
        // On va chercher le paiement correspondant dans la base
        List<Payer> paiements = bourseService.listerPaiements();
        Payer paiementASupprimer = null;

        for (Payer paiement : paiements) {
            if (paiement.getMatricule().equals(matricule) &&
                    paiement.getAnneeUniv().equals(anneeUniv) &&
                    paiement.getNbrMois() == nbrMois) {
                paiementASupprimer = paiement;
                break;
            }
        }

        if (paiementASupprimer == null) {
            JOptionPane.showMessageDialog(this,
                    "Paiement non trouvé",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce paiement?\n\n" +
                        "Matricule: " + matricule + "\n" +
                        "Année: " + anneeUniv + "\n" +
                        "Mois payés: " + nbrMois + "\n\n" +
                        "⚠️ Cette action est irréversible!",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bourseService.supprimerPaiement(paiementASupprimer.getIdpaye());
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Paiement supprimé avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                loadPaiements();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression du paiement",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void genererPDF() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un paiement pour générer le PDF",
                    "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String matricule = (String) tableModel.getValueAt(selectedRow, 0);
        String anneeUniv = (String) tableModel.getValueAt(selectedRow, 1);

        // Vérifier si l'étudiant existe
        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant == null) {
            JOptionPane.showMessageDialog(this,
                    "Étudiant non trouvé pour ce paiement",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Dialog pour choisir le nom du fichier
        String defaultFileName = "recu_" + matricule + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

        JTextField fileNameField = new JTextField(defaultFileName);
        fileNameField.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("Nom du fichier PDF:"), BorderLayout.NORTH);
        inputPanel.add(fileNameField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, inputPanel,
                "Générer le reçu PDF", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String fileName = fileNameField.getText().trim();
            if (fileName.isEmpty()) {
                fileName = defaultFileName;
            }
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }

            // Générer le PDF
            boolean success = bourseService.genererRecuPaiement(matricule, anneeUniv, fileName);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Reçu PDF généré avec succès!\n" +
                                "Fichier: " + fileName + "\n" +
                                "Étudiant: " + etudiant.getNom(),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la génération du PDF",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}