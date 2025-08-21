package org.example.ui;

import org.example.model.Etudiant;
import org.example.service.BourseService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class EtudiantPanel extends JPanel {
    private final BourseService bourseService;
    private final Color primaryColor;
    private final Color secondaryColor;
    private final Color accentColor;
    private final Color backgroundColor;
    private final Color textColor;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private Timer searchTimer;

    // Champs du formulaire
    private JTextField matriculeField;
    private JTextField anneeField;
    private JTextField nomField;
    private JComboBox<String> sexeCombo;
    private JTextField naissanceField;
    private JTextField institutionField;
    private JTextField mailField;
    private JTextField niveauField;

    // Nouveaux champs pour les filtres
    private JComboBox<String> niveauFilterCombo;
    private JComboBox<String> institutionFilterCombo;

    public EtudiantPanel(BourseService bourseService, Color primary, Color secondary, Color accent, Color background, Color text) {
        this.bourseService = bourseService;
        this.primaryColor = primary;
        this.secondaryColor = secondary;
        this.accentColor = accent;
        this.backgroundColor = background;
        this.textColor = text;

        this.searchTimer = new Timer();

        setLayout(new BorderLayout(10, 10));
        setBackground(backgroundColor);
        initializeUI();
        loadEtudiants();
        updateFilterCombos();
    }

    private void initializeUI() {
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel de titre
        add(createTitlePanel(), BorderLayout.NORTH);

        // Panel de contenu avec tableau et formulaire
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createTablePanel(), createFormPanel());
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        // Barre d'outils en bas
        add(createToolbarPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("GESTION DES ÉTUDIANTS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(primaryColor);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setBackground(backgroundColor);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(secondaryColor, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Écouteur de texte pour la recherche en temps réel
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scheduleSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scheduleSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scheduleSearch();
            }

            private void scheduleSearch() {
                // Annule la recherche précédente
                searchTimer.cancel();
                searchTimer = new Timer();

                // Lance une nouvelle recherche après 300ms de pause
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(() -> {
                            searchEtudiants();
                        });
                    }
                }, 300);
            }
        });

        // BOUTON EFFACER SEULEMENT
        JButton clearButton = createStyledButton(" Effacer", new Color(231, 76, 60));
        clearButton.addActionListener(e -> {
            searchField.setText("");
            niveauFilterCombo.setSelectedIndex(0);
            institutionFilterCombo.setSelectedIndex(0);
            loadEtudiants();
        });

        searchPanel.add(new JLabel("Recherche :"));
        searchPanel.add(searchField);
        searchPanel.add(clearButton); // SEULEMENT le bouton Effacer

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(primaryColor, 2),
                "Liste des étudiants",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                primaryColor
        ));
        panel.setBackground(backgroundColor);

        // Panel pour les filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(backgroundColor);

        filterPanel.add(new JLabel("Filtrer par niveau:"));
        niveauFilterCombo = new JComboBox<>();
        niveauFilterCombo.addItem("Tous les niveaux");
        niveauFilterCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        niveauFilterCombo.addActionListener(e -> applyFilters());
        filterPanel.add(niveauFilterCombo);

        filterPanel.add(new JLabel("Filtrer par institution:"));
        institutionFilterCombo = new JComboBox<>();
        institutionFilterCombo.addItem("Toutes les institutions");
        institutionFilterCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        institutionFilterCombo.addActionListener(e -> applyFilters());
        filterPanel.add(institutionFilterCombo);

        String[] columns = {"Matricule", "Année Univ.", "Nom", "Sexe", "Date Naissance", "Institution", "Mail", " Niveau"};
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

        // CENTRER TOUTES LES CELLULES
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Appliquer le centrage à toutes les colonnes
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style de l'en-tête du tableau - CENTRER AUSSI LES EN-TÊTES
        JTableHeader header = table.getTableHeader();
        header.setBackground(primaryColor);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));

        // Centrer les en-têtes de colonnes
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setBackground(primaryColor);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 13));

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Ajouter un écouteur de sélection pour remplir le formulaire
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormWithSelectedStudent();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 350));

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(secondaryColor, 2),
                "Formulaire Étudiant",
                0, 0,
                new Font("Arial", Font.BOLD, 14),
                secondaryColor
        ));
        panel.setBackground(backgroundColor);

        JPanel formPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Initialiser les champs
        matriculeField = createFormTextField();
        anneeField = createFormTextField();
        nomField = createFormTextField();
        sexeCombo = new JComboBox<>(new String[]{"M", "F"});
        sexeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        naissanceField = createFormTextField();
        institutionField = createFormTextField();
        mailField = createFormTextField();
        niveauField = createFormTextField();

        // Première ligne
        formPanel.add(createFormLabel("Matricule:"));
        formPanel.add(matriculeField);

        formPanel.add(createFormLabel("Année Univ.:"));
        formPanel.add(anneeField);

        // Deuxième ligne
        formPanel.add(createFormLabel("Nom:"));
        formPanel.add(nomField);

        formPanel.add(createFormLabel("Sexe:"));
        formPanel.add(sexeCombo);

        // Troisième ligne
        formPanel.add(createFormLabel("Date Naissance:"));
        formPanel.add(naissanceField);

        formPanel.add(createFormLabel("Institution:"));
        formPanel.add(institutionField);

        // Quatrième ligne
        formPanel.add(createFormLabel("Mail:"));
        formPanel.add(mailField);

        formPanel.add(createFormLabel(" Niveau:"));
        formPanel.add(niveauField);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private void fillFormWithSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String matricule = (String) tableModel.getValueAt(selectedRow, 0);
            String anneeUniv = (String) tableModel.getValueAt(selectedRow, 1);

            Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
            if (etudiant != null) {
                matriculeField.setText(etudiant.getMatricule());
                anneeField.setText(etudiant.getAnneeUniv());
                nomField.setText(etudiant.getNom());
                sexeCombo.setSelectedItem(etudiant.getSexe());
                naissanceField.setText(etudiant.getDatenais().toString());
                institutionField.setText(etudiant.getInstitution());
                mailField.setText(etudiant.getMail());
                niveauField.setText(etudiant.getIdniv());
            }
        }
    }

    private void clearForm() {
        matriculeField.setText("");
        anneeField.setText("");
        nomField.setText("");
        sexeCombo.setSelectedIndex(0);
        naissanceField.setText("");
        institutionField.setText("");
        mailField.setText("");
        niveauField.setText("");
        table.clearSelection();
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(textColor);
        return label;
    }

    private JTextField createFormTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
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
        JButton deleteButton = createStyledButton("️ SUPPRIMER", new Color(231, 76, 60));
        JButton refreshButton = createStyledButton(" ACTUALISER", new Color(155, 89, 182));
        JButton clearButton = createStyledButton(" NOUVEAU", new Color(52, 152, 219));

        addButton.addActionListener(e -> createStudent());
        editButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        refreshButton.addActionListener(e -> {
            loadEtudiants();
            updateFilterCombos();
        });
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
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet de survol
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

    private void loadEtudiants() {
        tableModel.setRowCount(0);
        List<Etudiant> etudiants = bourseService.listerEtudiants();

        for (Etudiant etudiant : etudiants) {
            tableModel.addRow(new Object[]{
                    etudiant.getMatricule(),
                    etudiant.getAnneeUniv(),
                    etudiant.getNom(),
                    etudiant.getSexe(),
                    etudiant.getDatenais().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    etudiant.getInstitution(),
                    etudiant.getMail(),
                    etudiant.getIdniv()
            });
        }

        updateStudentCount(etudiants.size());
    }

    private void updateFilterCombos() {
        // Récupérer tous les étudiants pour extraire les niveaux et institutions uniques
        List<Etudiant> etudiants = bourseService.listerEtudiants();

        Set<String> niveaux = new HashSet<>();
        Set<String> institutions = new HashSet<>();

        for (Etudiant etudiant : etudiants) {
            if (etudiant.getIdniv() != null && !etudiant.getIdniv().trim().isEmpty()) {
                niveaux.add(etudiant.getIdniv().trim());
            }
            if (etudiant.getInstitution() != null && !etudiant.getInstitution().trim().isEmpty()) {
                institutions.add(etudiant.getInstitution().trim());
            }
        }

        // Mettre à jour le combo des niveaux
        niveauFilterCombo.removeAllItems();
        niveauFilterCombo.addItem("Tous les niveaux");
        for (String niveau : niveaux) {
            niveauFilterCombo.addItem(niveau);
        }

        // Mettre à jour le combo des institutions
        institutionFilterCombo.removeAllItems();
        institutionFilterCombo.addItem("Toutes les institutions");
        for (String institution : institutions) {
            institutionFilterCombo.addItem(institution);
        }
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim();
        String selectedNiveau = (String) niveauFilterCombo.getSelectedItem();
        String selectedInstitution = (String) institutionFilterCombo.getSelectedItem();

        // Si aucun filtre n'est sélectionné, charger tous les étudiants
        if (("Tous les niveaux".equals(selectedNiveau) || selectedNiveau == null) &&
                ("Toutes les institutions".equals(selectedInstitution) || selectedInstitution == null) &&
                searchText.isEmpty()) {
            loadEtudiants();
            return;
        }

        tableModel.setRowCount(0);
        List<Etudiant> etudiants = bourseService.listerEtudiants();

        for (Etudiant etudiant : etudiants) {
            // Vérifier le filtre de recherche texte
            boolean matchesSearch = searchText.isEmpty() ||
                    etudiant.getMatricule().toLowerCase().contains(searchText.toLowerCase()) ||
                    etudiant.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                    etudiant.getAnneeUniv().toLowerCase().contains(searchText.toLowerCase()) ||
                    etudiant.getInstitution().toLowerCase().contains(searchText.toLowerCase()) ||
                    etudiant.getMail().toLowerCase().contains(searchText.toLowerCase()) ||
                    etudiant.getIdniv().toLowerCase().contains(searchText.toLowerCase());

            // Vérifier le filtre de niveau
            boolean matchesNiveau = "Tous les niveaux".equals(selectedNiveau) ||
                    selectedNiveau == null ||
                    selectedNiveau.equals(etudiant.getIdniv());

            // Vérifier le filtre d'institution
            boolean matchesInstitution = "Toutes les institutions".equals(selectedInstitution) ||
                    selectedInstitution == null ||
                    selectedInstitution.equals(etudiant.getInstitution());

            if (matchesSearch && matchesNiveau && matchesInstitution) {
                tableModel.addRow(new Object[]{
                        etudiant.getMatricule(),
                        etudiant.getAnneeUniv(),
                        etudiant.getNom(),
                        etudiant.getSexe(),
                        etudiant.getDatenais().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        etudiant.getInstitution(),
                        etudiant.getMail(),
                        etudiant.getIdniv()
                });
            }
        }

        updateStudentCount(tableModel.getRowCount());
    }

    private void searchEtudiants() {
        String searchText = searchField.getText().trim();
        String selectedNiveau = (String) niveauFilterCombo.getSelectedItem();
        String selectedInstitution = (String) institutionFilterCombo.getSelectedItem();

        if (searchText.isEmpty() &&
                ("Tous les niveaux".equals(selectedNiveau) || selectedNiveau == null) &&
                ("Toutes les institutions".equals(selectedInstitution) || selectedInstitution == null)) {
            loadEtudiants();
            return;
        }

        applyFilters();
    }

    private void updateStudentCount(int count) {
        // Met à jour le compteur d'étudiants dans le titre
        Component[] components = ((JPanel)getComponent(0)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel && comp != ((JPanel)getComponent(0)).getComponent(0)) {
                ((JLabel)comp).setText(count + " étudiant(s)");
                break;
            }
        }
    }

    private void createStudent() {
        try {
            // Validation des champs obligatoires
            if (matriculeField.getText().trim().isEmpty() ||
                    anneeField.getText().trim().isEmpty() ||
                    nomField.getText().trim().isEmpty() ||
                    naissanceField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Les champs Matricule, Année Univ., Nom et Date Naissance sont obligatoires",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Vérifier si l'étudiant existe déjà
            Etudiant existing = bourseService.obtenirEtudiant(
                    matriculeField.getText().trim(),
                    anneeField.getText().trim()
            );

            if (existing != null) {
                JOptionPane.showMessageDialog(this,
                        "Un étudiant avec ce matricule existe déjà pour cette année universitaire",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer le nouvel étudiant
            Etudiant etudiant = new Etudiant(
                    matriculeField.getText().trim(),
                    anneeField.getText().trim(),
                    nomField.getText().trim(),
                    (String) sexeCombo.getSelectedItem(),
                    LocalDate.parse(naissanceField.getText().trim()),
                    institutionField.getText().trim(),
                    mailField.getText().trim(),
                    niveauField.getText().trim()
            );

            boolean success = bourseService.creerEtudiant(etudiant);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Étudiant créé avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadEtudiants();
                updateFilterCombos(); // Mettre à jour les filtres après l'ajout
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la création de l'étudiant",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez le format yyyy-MM-dd",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un étudiant à modifier",
                    "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String matricule = (String) tableModel.getValueAt(selectedRow, 0);
            String anneeUniv = (String) tableModel.getValueAt(selectedRow, 1);

            // Vérifier que les champs obligatoires sont remplis
            if (nomField.getText().trim().isEmpty() ||
                    naissanceField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "Les champs Nom et Date Naissance sont obligatoires",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer l'étudiant avec les modifications
            Etudiant etudiant = new Etudiant(
                    matricule, // Matricule non modifiable
                    anneeUniv, // Année non modifiable
                    nomField.getText().trim(),
                    (String) sexeCombo.getSelectedItem(),
                    LocalDate.parse(naissanceField.getText().trim()),
                    institutionField.getText().trim(),
                    mailField.getText().trim(),
                    niveauField.getText().trim()
            );

            boolean success = bourseService.modifierEtudiant(etudiant);

            if (success) {
                // ✅ RAFRAÎCHIR LA LIGNE MODIFIÉE DANS LE TABLEAU
                tableModel.setValueAt(nomField.getText().trim(), selectedRow, 2);
                tableModel.setValueAt((String) sexeCombo.getSelectedItem(), selectedRow, 3);
                tableModel.setValueAt(naissanceField.getText().trim(), selectedRow, 4);
                tableModel.setValueAt(institutionField.getText().trim(), selectedRow, 5);
                tableModel.setValueAt(mailField.getText().trim(), selectedRow, 6);
                tableModel.setValueAt(niveauField.getText().trim(), selectedRow, 7);

                JOptionPane.showMessageDialog(this,
                        "Étudiant modifié avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                // ✅ FORCER LE RAFRAÎCHISSEMENT VISUEL
                table.repaint();

                updateFilterCombos(); // Mettre à jour les filtres après modification

            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification de l'étudiant",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez le format yyyy-MM-dd",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un étudiant à supprimer",
                    "Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String matricule = (String) tableModel.getValueAt(selectedRow, 0);
        String anneeUniv = (String) tableModel.getValueAt(selectedRow, 1);
        String nom = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer l'étudiant : " + nom + "?\n" +
                        "Matricule: " + matricule + "\n" +
                        "Année universitaire: " + anneeUniv + "\n\n" +
                        "⚠️ Cette action supprimera également tous ses paiements!",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bourseService.supprimerEtudiant(matricule, anneeUniv);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Étudiant supprimé avec succès!",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadEtudiants();
                updateFilterCombos(); // Mettre à jour les filtres après suppression
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression de l'étudiant",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}