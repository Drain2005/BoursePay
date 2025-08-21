package org.example.ui;

import org.example.model.Montant;
import org.example.service.BourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class MontantPanel extends JPanel {
    private final BourseService bourseService;
    private final Color primaryColor;
    private final Color secondaryColor;
    private final Color accentColor;
    private final Color backgroundColor;
    private final Color textColor;

    private JTable table;
    private DefaultTableModel tableModel;

    // Champs du formulaire
    private JTextField idnivField;
    private JTextField niveauField;
    private JTextField montantField;

    public MontantPanel(BourseService bourseService, Color primary, Color secondary, Color accent, Color background, Color text) {
        this.bourseService = bourseService;
        this.primaryColor = primary;
        this.secondaryColor = secondary;
        this.accentColor = accent;
        this.backgroundColor = background;
        this.textColor = text;

        setLayout(new BorderLayout(10, 10));
        setBackground(backgroundColor);
        initializeUI();
        loadMontants();
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

        JLabel titleLabel = new JLabel("GESTION DES MONTANTS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(primaryColor);

        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                "Liste des montants",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                primaryColor
        ));
        panel.setBackground(backgroundColor);

        String[] columns = {"Niveau", "Description", "Montant (Ar)"};
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

        // Ajouter un écouteur de sélection
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormWithSelectedMontant();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 250));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(secondaryColor, 2),
                "Formulaire Montant",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                secondaryColor
        ));
        panel.setBackground(backgroundColor);
        panel.setPreferredSize(new Dimension(800, 150));

        // Panel principal du formulaire avec padding
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialiser les champs
        idnivField = createFormTextField();
        niveauField = createFormTextField();
        montantField = createFormTextField();

        // Première ligne - ID Niveau
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Niveau:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(idnivField, gbc);

        // Deuxième ligne - Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        formPanel.add(niveauField, gbc);

        // Troisième ligne - Montant
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Montant (Ar):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.7;
        formPanel.add(montantField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private void fillFormWithSelectedMontant() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String idniv = (String) tableModel.getValueAt(selectedRow, 0);
            Montant montant = bourseService.obtenirMontant(idniv);
            if (montant != null) {
                idnivField.setText(montant.getIdniv());
                niveauField.setText(montant.getNiveau());
                montantField.setText(String.valueOf(montant.getMontant()));
            }
        }
    }

    private void clearForm() {
        idnivField.setText("");
        niveauField.setText("");
        montantField.setText("");
        table.clearSelection();
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
        return textField;
    }

    private JPanel createToolbarPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton addButton = createStyledButton(" AJOUTER", accentColor);
        JButton editButton = createStyledButton(" MODIFIER", secondaryColor);
        JButton deleteButton = createStyledButton(" SUPPRIMER", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton(" ACTUALISER", new Color(155, 89, 182));
        JButton clearButton = createStyledButton(" NOUVEAU", new Color(52, 152, 219));

        addButton.addActionListener(e -> createMontant());
        editButton.addActionListener(e -> updateMontant());
        deleteButton.addActionListener(e -> deleteMontant());
        refreshButton.addActionListener(e -> loadMontants());
        clearButton.addActionListener(e -> clearForm());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
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

    private void loadMontants() {
        tableModel.setRowCount(0);
        List<Montant> montants = bourseService.listerMontants();

        for (Montant montant : montants) {
            tableModel.addRow(new Object[]{
                    montant.getIdniv(),
                    montant.getNiveau(),
                    String.format("%,d", montant.getMontant())
            });
        }
    }

    private void createMontant() {
        try {
            if (idnivField.getText().trim().isEmpty() ||
                    niveauField.getText().trim().isEmpty() ||
                    montantField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Tous les champs sont obligatoires",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int montantValue = Integer.parseInt(montantField.getText().trim());
            if (montantValue <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Le montant doit être positif",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier si l'ID existe déjà
            Montant existing = bourseService.obtenirMontant(idnivField.getText().trim());
            if (existing != null) {
                JOptionPane.showMessageDialog(this,
                        "Un montant avec ce Niveau existe déjà",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Montant montant = new Montant(
                    idnivField.getText().trim(),
                    niveauField.getText().trim(),
                    montantValue
            );

            boolean success = bourseService.creerMontant(montant);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Montant créé avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMontants();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la création du montant",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Le montant doit être un nombre valide",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMontant() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un montant à modifier",
                    "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String idniv = (String) tableModel.getValueAt(selectedRow, 0);

            if (niveauField.getText().trim().isEmpty() ||
                    montantField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Les champs Description et Montant sont obligatoires",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int montantValue = Integer.parseInt(montantField.getText().trim());
            if (montantValue <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Le montant doit être positif",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Montant montant = new Montant(
                    idniv,
                    niveauField.getText().trim(),
                    montantValue
            );

            boolean success = bourseService.modifierMontant(montant);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Montant modifié avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                loadMontants();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification du montant",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Le montant doit être un nombre valide",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMontant() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un montant à supprimer",
                    "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idniv = (String) tableModel.getValueAt(selectedRow, 0);
        String niveau = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer le montant : " + niveau + "?\n" +
                        "ID: " + idniv + "\n\n" +
                        "⚠️ Cette action est irréversible!",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bourseService.supprimerMontant(idniv);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Montant supprimé avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadMontants();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression. Vérifiez qu'aucun étudiant n'utilise ce niveau.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}