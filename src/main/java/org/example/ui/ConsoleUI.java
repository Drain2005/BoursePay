package org.example.ui;

import org.example.model.Etudiant;
import org.example.model.Montant;
import org.example.model.Payer;
import org.example.service.BourseService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final BourseService bourseService;
    
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.bourseService = new BourseService();
    }
    
    public void demarrer() {
        System.out.println("=== SYSTÈME DE GESTION DE BOURSE ÉTUDIANTE ===\n");
        
        boolean continuer = true;
        while (continuer) {
            afficherMenuPrincipal();
            int choix = lireEntier("Votre choix: ");
            
            switch (choix) {
                case 1 -> menuEtudiants();
                case 2 -> menuMontants();
                case 3 -> menuPaiements();
                case 4 -> menuRapports();
                case 5 -> menuNotifications();
                case 0 -> {
                    continuer = false;
                    System.out.println("Au revoir!");
                }
                default -> System.out.println("Choix invalide!");
            }
        }
    }
    
    private void afficherMenuPrincipal() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Gestion des Étudiants");
        System.out.println("2. Gestion des Montants");
        System.out.println("3. Gestion des Paiements");
        System.out.println("4. Rapports et Recherches");
        System.out.println("5. Notifications");
        System.out.println("0. Quitter");
    }
    
    private void menuEtudiants() {
        System.out.println("\n=== GESTION DES ÉTUDIANTS ===");
        System.out.println("1. Créer un étudiant");
        System.out.println("2. Lister les étudiants");
        System.out.println("3. Rechercher un étudiant");
        System.out.println("4. Modifier un étudiant");
        System.out.println("5. Supprimer un étudiant");
        System.out.println("0. Retour");
        
        int choix = lireEntier("Votre choix: ");
        
        switch (choix) {
            case 1 -> creerEtudiant();
            case 2 -> listerEtudiants();
            case 3 -> rechercherEtudiant();
            case 4 -> modifierEtudiant();
            case 5 -> supprimerEtudiant();
        }
    }
    
    private void menuMontants() {
        System.out.println("\n=== GESTION DES MONTANTS ===");
        System.out.println("1. Créer un montant");
        System.out.println("2. Lister les montants");
        System.out.println("3. Modifier un montant");
        System.out.println("4. Supprimer un montant");
        System.out.println("0. Retour");
        
        int choix = lireEntier("Votre choix: ");
        
        switch (choix) {
            case 1 -> creerMontant();
            case 2 -> listerMontants();
            case 3 -> modifierMontant();
            case 4 -> supprimerMontant();
        }
    }
    
    private void menuPaiements() {
        System.out.println("\n=== GESTION DES PAIEMENTS ===");
        System.out.println("1. Effectuer un paiement");
        System.out.println("2. Lister les paiements");
        System.out.println("3. Modifier un paiement");
        System.out.println("4. Supprimer un paiement");
        System.out.println("5. Générer reçu PDF");
        System.out.println("0. Retour");
        
        int choix = lireEntier("Votre choix: ");
        
        switch (choix) {
            case 1 -> effectuerPaiement();
            case 2 -> listerPaiements();
            case 3 -> modifierPaiement();
            case 4 -> supprimerPaiement();
            case 5 -> genererRecuPDF();
        }
    }
    
    private void menuRapports() {
        System.out.println("\n=== RAPPORTS ET RECHERCHES ===");
        System.out.println("1. Rechercher étudiant par nom");
        System.out.println("2. Lister étudiants par niveau et établissement");
        System.out.println("3. Lister étudiants mineurs");
        System.out.println("4. Lister retardataires pour un mois");
        System.out.println("0. Retour");
        
        int choix = lireEntier("Votre choix: ");
        
        switch (choix) {
            case 1 -> rechercherEtudiantParNom();
            case 2 -> listerEtudiantsParNiveauEtEtablissement();
            case 3 -> listerEtudiantsMineurs();
            case 4 -> listerRetardataires();
        }
    }
    
    private void menuNotifications() {
        System.out.println("\n=== NOTIFICATIONS ===");
        System.out.println("1. Notifier retardataires");
        System.out.println("2. Tester connexion email");
        System.out.println("0. Retour");
        
        int choix = lireEntier("Votre choix: ");
        
        switch (choix) {
            case 1 -> notifierRetardataires();
            case 2 -> testerConnexionEmail();
        }
    }
    
    // Méthodes pour étudiants
    private void creerEtudiant() {
        System.out.println("\n=== CRÉER UN ÉTUDIANT ===");
        
        String matricule = lireChaine("Matricule: ");
        String anneeUniv = lireChaine("Année universitaire: ");
        String nom = lireChaine("Nom: ");
        String sexe = lireChaine("Sexe (M/F): ");
        LocalDate datenais = lireDate("Date de naissance (yyyy-mm-dd): ");
        String institution = lireChaine("Institution: ");
        String mail = lireChaine("Email: ");
        String idniv = lireChaine("ID Niveau: ");
        
        Etudiant etudiant = new Etudiant(matricule, anneeUniv, nom, sexe, datenais, institution, mail, idniv);
        
        if (bourseService.creerEtudiant(etudiant)) {
            System.out.println("Étudiant créé avec succès!");
        } else {
            System.out.println("Erreur lors de la création de l'étudiant.");
        }
    }
    
    private void listerEtudiants() {
        System.out.println("\n=== LISTE DES ÉTUDIANTS ===");
        List<Etudiant> etudiants = bourseService.listerEtudiants();
        
        if (etudiants.isEmpty()) {
            System.out.println("Aucun étudiant trouvé.");
            return;
        }
        
        System.out.printf("%-10s %-12s %-25s %-5s %-12s %-15s %-25s %-8s%n",
            "Matricule", "Année", "Nom", "Sexe", "Naissance", "Institution", "Email", "Niveau");
        System.out.println("-".repeat(120));
        
        for (Etudiant etudiant : etudiants) {
            System.out.printf("%-10s %-12s %-25s %-5s %-12s %-15s %-25s %-8s%n",
                etudiant.getMatricule(),
                etudiant.getAnneeUniv(),
                etudiant.getNom(),
                etudiant.getSexe(),
                etudiant.getDatenais(),
                etudiant.getInstitution(),
                etudiant.getMail(),
                etudiant.getIdniv());
        }
    }
    
    private void rechercherEtudiant() {
        System.out.println("\n=== RECHERCHER UN ÉTUDIANT ===");
        String matricule = lireChaine("Matricule: ");
        String anneeUniv = lireChaine("Année universitaire: ");
        
        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant != null) {
            afficherEtudiant(etudiant);
        } else {
            System.out.println("Étudiant non trouvé.");
        }
    }
    
    private void modifierEtudiant() {
        System.out.println("\n=== MODIFIER UN ÉTUDIANT ===");
        String matricule = lireChaine("Matricule: ");
        String anneeUniv = lireChaine("Année universitaire: ");
        
        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant == null) {
            System.out.println("Étudiant non trouvé.");
            return;
        }
        
        System.out.println("Informations actuelles:");
        afficherEtudiant(etudiant);
        
        System.out.println("\nNouvelles informations (laissez vide pour garder la valeur actuelle):");
        
        String nom = lireChaine("Nom [" + etudiant.getNom() + "]: ");
        if (!nom.isEmpty()) etudiant.setNom(nom);
        
        String sexe = lireChaine("Sexe [" + etudiant.getSexe() + "]: ");
        if (!sexe.isEmpty()) etudiant.setSexe(sexe);
        
        String dateStr = lireChaine("Date de naissance [" + etudiant.getDatenais() + "]: ");
        if (!dateStr.isEmpty()) {
            try {
                etudiant.setDatenais(LocalDate.parse(dateStr));
            } catch (DateTimeParseException e) {
                System.out.println("Format de date invalide, valeur conservée.");
            }
        }
        
        String institution = lireChaine("Institution [" + etudiant.getInstitution() + "]: ");
        if (!institution.isEmpty()) etudiant.setInstitution(institution);
        
        String mail = lireChaine("Email [" + etudiant.getMail() + "]: ");
        if (!mail.isEmpty()) etudiant.setMail(mail);
        
        String idniv = lireChaine("ID Niveau [" + etudiant.getIdniv() + "]: ");
        if (!idniv.isEmpty()) etudiant.setIdniv(idniv);
        
        if (bourseService.modifierEtudiant(etudiant)) {
            System.out.println("Étudiant modifié avec succès!");
        } else {
            System.out.println("Erreur lors de la modification.");
        }
    }
    
    private void supprimerEtudiant() {
        System.out.println("\n=== SUPPRIMER UN ÉTUDIANT ===");
        String matricule = lireChaine("Matricule: ");
        String anneeUniv = lireChaine("Année universitaire: ");
        
        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant == null) {
            System.out.println("Étudiant non trouvé.");
            return;
        }
        
        System.out.println("Étudiant à supprimer:");
        afficherEtudiant(etudiant);
        
        String confirmation = lireChaine("Êtes-vous sûr de vouloir supprimer cet étudiant? (oui/non): ");
        if (confirmation.equalsIgnoreCase("oui")) {
            if (bourseService.supprimerEtudiant(matricule, anneeUniv)) {
                System.out.println("Étudiant supprimé avec succès!");
            } else {
                System.out.println("Erreur lors de la suppression.");
            }
        }
    }
    
    // Méthodes pour montants
    private void creerMontant() {
        System.out.println("\n=== CRÉER UN MONTANT ===");
        
        String idniv = lireChaine("ID Niveau: ");
        String niveau = lireChaine("Niveau: ");
        int montant = lireEntier("Montant: ");
        
        Montant nouveauMontant = new Montant(idniv, niveau, montant);
        
        if (bourseService.creerMontant(nouveauMontant)) {
            System.out.println("Montant créé avec succès!");
        } else {
            System.out.println("Erreur lors de la création du montant.");
        }
    }
    
    private void listerMontants() {
        System.out.println("\n=== LISTE DES MONTANTS ===");
        List<Montant> montants = bourseService.listerMontants();
        
        if (montants.isEmpty()) {
            System.out.println("Aucun montant trouvé.");
            return;
        }
        
        System.out.printf("%-10s %-20s %-15s%n", "ID Niveau", "Niveau", "Montant");
        System.out.println("-".repeat(50));
        
        for (Montant montant : montants) {
            System.out.printf("%-10s %-20s %-15s%n",
                montant.getIdniv(),
                montant.getNiveau(),
                String.format("%,d Ar", montant.getMontant()));
        }
    }
    
    private void modifierMontant() {
        System.out.println("\n=== MODIFIER UN MONTANT ===");
        String idniv = lireChaine("ID Niveau: ");
        
        Montant montant = bourseService.obtenirMontant(idniv);
        if (montant == null) {
            System.out.println("Montant non trouvé.");
            return;
        }
        
        System.out.println("Informations actuelles:");
        System.out.println("Niveau: " + montant.getNiveau());
        System.out.println("Montant: " + String.format("%,d Ar", montant.getMontant()));
        
        String niveau = lireChaine("Nouveau niveau [" + montant.getNiveau() + "]: ");
        if (!niveau.isEmpty()) montant.setNiveau(niveau);
        
        String montantStr = lireChaine("Nouveau montant [" + montant.getMontant() + "]: ");
        if (!montantStr.isEmpty()) {
            try {
                montant.setMontant(Integer.parseInt(montantStr));
            } catch (NumberFormatException e) {
                System.out.println("Montant invalide, valeur conservée.");
            }
        }
        
        if (bourseService.modifierMontant(montant)) {
            System.out.println("Montant modifié avec succès!");
        } else {
            System.out.println("Erreur lors de la modification.");
        }
    }
    
    private void supprimerMontant() {
        System.out.println("\n=== SUPPRIMER UN MONTANT ===");
        String idniv = lireChaine("ID Niveau: ");
        
        Montant montant = bourseService.obtenirMontant(idniv);
        if (montant == null) {
            System.out.println("Montant non trouvé.");
            return;
        }
        
        System.out.println("Montant à supprimer:");
        System.out.println("Niveau: " + montant.getNiveau());
        System.out.println("Montant: " + String.format("%,d Ar", montant.getMontant()));
        
        String confirmation = lireChaine("Êtes-vous sûr? (oui/non): ");
        if (confirmation.equalsIgnoreCase("oui")) {
            if (bourseService.supprimerMontant(idniv)) {
                System.out.println("Montant supprimé avec succès!");
            } else {
                System.out.println("Erreur lors de la suppression.");
            }
        }
    }
    
    // Méthodes pour paiements
    private void effectuerPaiement() {
        System.out.println("\n=== EFFECTUER UN PAIEMENT ===");
        
        String matricule = lireChaine("Matricule de l'étudiant: ");
        String anneeUniv = lireChaine("Année universitaire: ");
        int nbrMois = lireEntier("Nombre de mois: ");
        
        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant == null) {
            System.out.println("Étudiant non trouvé.");
            return;
        }
        
        if (bourseService.effectuerPaiement(matricule, anneeUniv, nbrMois)) {
            System.out.println("Paiement effectué avec succès!");
        } else {
            System.out.println("Erreur lors du paiement.");
        }
    }
    
    private void listerPaiements() {
        System.out.println("\n=== LISTE DES PAIEMENTS ===");
        List<Payer> paiements = bourseService.listerPaiements();
        
        if (paiements.isEmpty()) {
            System.out.println("Aucun paiement trouvé.");
            return;
        }
        
        System.out.printf("%-15s %-10s %-12s %-20s %-8s%n",
            "ID Paiement", "Matricule", "Année", "Date", "Nb Mois");
        System.out.println("-".repeat(75));
        
        for (Payer paiement : paiements) {
            System.out.printf("%-15s %-10s %-12s %-20s %-8d%n",
                paiement.getIdpaye().substring(0, Math.min(12, paiement.getIdpaye().length())) + "...",
                paiement.getMatricule(),
                paiement.getAnneeUniv(),
                paiement.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                paiement.getNbrMois());
        }
    }
    
    private void modifierPaiement() {
        System.out.println("\n=== MODIFIER UN PAIEMENT ===");
        String idpaye = lireChaine("ID du paiement: ");
        
        Payer paiement = bourseService.obtenirPaiement(idpaye);
        if (paiement == null) {
            System.out.println("Paiement non trouvé.");
            return;
        }
        
        System.out.println("Informations actuelles:");
        System.out.println("Matricule: " + paiement.getMatricule());
        System.out.println("Année: " + paiement.getAnneeUniv());
        System.out.println("Date: " + paiement.getDate());
        System.out.println("Nombre de mois: " + paiement.getNbrMois());
        
        String nbrMoisStr = lireChaine("Nouveau nombre de mois [" + paiement.getNbrMois() + "]: ");
        if (!nbrMoisStr.isEmpty()) {
            try {
                paiement.setNbrMois(Integer.parseInt(nbrMoisStr));
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide, valeur conservée.");
            }
        }
        
        if (bourseService.modifierPaiement(paiement)) {
            System.out.println("Paiement modifié avec succès!");
        } else {
            System.out.println("Erreur lors de la modification.");
        }
    }
    
    private void supprimerPaiement() {
        System.out.println("\n=== SUPPRIMER UN PAIEMENT ===");
        String idpaye = lireChaine("ID du paiement: ");
        
        Payer paiement = bourseService.obtenirPaiement(idpaye);
        if (paiement == null) {
            System.out.println("Paiement non trouvé.");
            return;
        }
        
        System.out.println("Paiement à supprimer:");
        System.out.println("Matricule: " + paiement.getMatricule());
        System.out.println("Date: " + paiement.getDate());
        System.out.println("Montant: " + paiement.getNbrMois() + " mois");
        
        String confirmation = lireChaine("Êtes-vous sûr? (oui/non): ");
        if (confirmation.equalsIgnoreCase("oui")) {
            if (bourseService.supprimerPaiement(idpaye)) {
                System.out.println("Paiement supprimé avec succès!");
            } else {
                System.out.println("Erreur lors de la suppression.");
            }
        }
    }
    
    private void genererRecuPDF() {
        System.out.println("\n=== GÉNÉRER REÇU PDF ===");
        String matricule = lireChaine("Matricule de l'étudiant: ");
        String anneeUniv = lireChaine("Année universitaire: ");
        String cheminFichier = lireChaine("Chemin du fichier PDF (ex: recu_" + matricule + ".pdf): ");
        
        if (cheminFichier.isEmpty()) {
            cheminFichier = "recu_" + matricule + ".pdf";
        }
        
        if (bourseService.genererRecuPaiement(matricule, anneeUniv, cheminFichier)) {
            System.out.println("Reçu PDF généré avec succès: " + cheminFichier);
        } else {
            System.out.println("Erreur lors de la génération du reçu.");
        }
    }
    
    // Méthodes pour rapports
    private void rechercherEtudiantParNom() {
        System.out.println("\n=== RECHERCHE PAR NOM ===");
        String nom = lireChaine("Nom à rechercher: ");
        
        List<Etudiant> etudiants = bourseService.rechercherEtudiants(nom);
        
        if (etudiants.isEmpty()) {
            System.out.println("Aucun étudiant trouvé avec ce nom.");
            return;
        }
        
        System.out.println("Étudiants trouvés:");
        for (Etudiant etudiant : etudiants) {
            afficherEtudiant(etudiant);
            System.out.println("-".repeat(50));
        }
    }
    
    private void listerEtudiantsParNiveauEtEtablissement() {
        System.out.println("\n=== ÉTUDIANTS PAR NIVEAU ET ÉTABLISSEMENT ===");
        Map<String, List<Etudiant>> groupes = bourseService.listerEtudiantsParNiveauEtEtablissement();
        
        for (Map.Entry<String, List<Etudiant>> entry : groupes.entrySet()) {
            System.out.println("\n" + entry.getKey() + ":");
            for (Etudiant etudiant : entry.getValue()) {
                System.out.println("  - " + etudiant.getNom() + " (" + etudiant.getMatricule() + ")");
            }
        }
    }
    
    private void listerEtudiantsMineurs() {
        System.out.println("\n=== ÉTUDIANTS MINEURS ===");
        List<Etudiant> mineurs = bourseService.listerEtudiantsMineurs();
        
        if (mineurs.isEmpty()) {
            System.out.println("Aucun étudiant mineur trouvé.");
            return;
        }
        
        for (Etudiant etudiant : mineurs) {
            System.out.println(etudiant.getNom() + " (" + etudiant.getMatricule() + ") - " + 
                             etudiant.getAge() + " ans");
        }
    }
    
    private void listerRetardataires() {
        System.out.println("\n=== RETARDATAIRES ===");
        String moisStr = lireChaine("Mois (yyyy-mm): ");
        
        try {
            YearMonth mois = YearMonth.parse(moisStr);
            List<Etudiant> retardataires = bourseService.obtenirRetardataires(mois);
            
            if (retardataires.isEmpty()) {
                System.out.println("Aucun retardataire pour ce mois.");
                return;
            }
            
            System.out.println("Retardataires pour " + mois + ":");
            for (Etudiant etudiant : retardataires) {
                System.out.println("- " + etudiant.getNom() + " (" + etudiant.getMatricule() + ") - " + 
                                 etudiant.getMail());
            }
        } catch (DateTimeParseException e) {
            System.out.println("Format de mois invalide (utilisez yyyy-mm).");
        }
    }
    
    // Méthodes pour notifications
    private void notifierRetardataires() {
        System.out.println("\n=== NOTIFIER RETARDATAIRES ===");
        String moisStr = lireChaine("Mois (yyyy-mm): ");
        
        try {
            YearMonth mois = YearMonth.parse(moisStr);
            
            if (bourseService.notifierRetardataires(mois)) {
                System.out.println("Notifications envoyées avec succès!");
            } else {
                System.out.println("Erreur lors de l'envoi des notifications.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Format de mois invalide (utilisez yyyy-mm).");
        }
    }
    
    private void testerConnexionEmail() {
        System.out.println("\n=== TEST CONNEXION EMAIL ===");
        // Note: Cette méthode nécessiterait d'exposer la méthode de test dans EmailService
        System.out.println("Test de connexion email non implémenté dans cette interface.");
    }
    
    // Méthodes utilitaires
    private void afficherEtudiant(Etudiant etudiant) {
        System.out.println("Matricule: " + etudiant.getMatricule());
        System.out.println("Année: " + etudiant.getAnneeUniv());
        System.out.println("Nom: " + etudiant.getNom());
        System.out.println("Sexe: " + etudiant.getSexe());
        System.out.println("Date de naissance: " + etudiant.getDatenais());
        System.out.println("Âge: " + etudiant.getAge() + " ans" + (etudiant.isMineur() ? " (mineur)" : ""));
        System.out.println("Institution: " + etudiant.getInstitution());
        System.out.println("Email: " + etudiant.getMail());
        System.out.println("Niveau: " + etudiant.getIdniv());
    }
    
    private String lireChaine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int lireEntier(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        }
    }
    
    private LocalDate lireDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Format de date invalide. Utilisez yyyy-mm-dd.");
            }
        }
    }
}