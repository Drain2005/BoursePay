package org.example.ui;

import org.example.model.Etudiant;
import org.example.model.Montant;
import org.example.model.Payer;
import org.example.service.BourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class ConsoleUI {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleUI.class);
    private final Scanner scanner;
    private final BourseService bourseService;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.bourseService = new BourseService();
    }

    public void demarrer() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║        SYSTÈME DE GESTION DE BOURSE ÉTUDIANTE             ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();

        boolean continuer = true;
        while (continuer) {
            try {
                afficherMenuPrincipal();
                int choix = lireEntier("Votre choix: ");

                switch (choix) {
                    case 1 -> menuEtudiants();
                    case 2 -> menuMontants();
                    case 3 -> menuPaiements();
                    case 4 -> menuRapports();
                    case 5 -> menuNotifications();
                    case 9 -> genererRecuExemple();
                    case 0 -> {
                        continuer = false;
                        System.out.println("\n🎓 Merci d'avoir utilisé le système de gestion de bourse!");
                        System.out.println("Au revoir! 👋");
                    }
                    default -> System.out.println("❌ Choix invalide! Veuillez choisir un nombre entre 0 et 5.");
                }
            } catch (Exception e) {
                logger.error("Erreur dans l'interface utilisateur", e);
                System.out.println("❌ Une erreur inattendue s'est produite. Veuillez réessayer.");
            }
        }
    }

    private void afficherMenuPrincipal() {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("                    📚 MENU PRINCIPAL 📚");
        System.out.println("═".repeat(60));
        System.out.println("1. 👨‍🎓 Gestion des Étudiants");
        System.out.println("2. 💰 Gestion des Montants");
        System.out.println("3. 💳 Gestion des Paiements");
        System.out.println("4. 📊 Rapports et Recherches");
        System.out.println("5. 📧 Notifications");
        System.out.println("9. 📄 Générer reçu d'exemple");
        System.out.println("0. 🚪 Quitter");
        System.out.println("═".repeat(60));
    }

    private void menuEtudiants() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("         👨‍🎓 GESTION DES ÉTUDIANTS 👨‍🎓");
        System.out.println("═".repeat(50));
        System.out.println("1. ➕ Créer un étudiant");
        System.out.println("2. 📋 Lister tous les étudiants");
        System.out.println("3. 🔍 Rechercher un étudiant");
        System.out.println("4. ✏️ Modifier un étudiant");
        System.out.println("5. 🗑️ Supprimer un étudiant");
        System.out.println("0. ⬅️ Retour au menu principal");

        int choix = lireEntier("Votre choix: ");

        switch (choix) {
            case 1 -> creerEtudiant();
            case 2 -> listerEtudiants();
            case 3 -> rechercherEtudiant();
            case 4 -> modifierEtudiant();
            case 5 -> supprimerEtudiant();
            case 0 -> { /* Retour au menu principal */ }
            default -> System.out.println("❌ Choix invalide!");
        }
    }

    private void menuMontants() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("           💰 GESTION DES MONTANTS 💰");
        System.out.println("═".repeat(50));
        System.out.println("1. ➕ Créer un montant");
        System.out.println("2. 📋 Lister tous les montants");
        System.out.println("3. ✏️ Modifier un montant");
        System.out.println("4. 🗑️ Supprimer un montant");
        System.out.println("0. ⬅️ Retour au menu principal");

        int choix = lireEntier("Votre choix: ");

        switch (choix) {
            case 1 -> creerMontant();
            case 2 -> listerMontants();
            case 3 -> modifierMontant();
            case 4 -> supprimerMontant();
            case 0 -> { /* Retour au menu principal */ }
            default -> System.out.println("❌ Choix invalide!");
        }
    }

    private void menuPaiements() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("          💳 GESTION DES PAIEMENTS 💳");
        System.out.println("═".repeat(50));
        System.out.println("1. ✅ Effectuer un paiement");
        System.out.println("2. 📋 Lister tous les paiements");
        System.out.println("3. ✏️ Modifier un paiement");
        System.out.println("4. 🗑️ Supprimer un paiement");
        System.out.println("5. 📄 Générer reçu PDF");
        System.out.println("0. ⬅️ Retour au menu principal");

        int choix = lireEntier("Votre choix: ");

        switch (choix) {
            case 1 -> effectuerPaiement();
            case 2 -> listerPaiements();
            case 3 -> modifierPaiement();
            case 4 -> supprimerPaiement();
            case 5 -> genererRecuPDF();
            case 0 -> { /* Retour au menu principal */ }
            default -> System.out.println("❌ Choix invalide!");
        }
    }

    private void menuRapports() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("         📊 RAPPORTS ET RECHERCHES 📊");
        System.out.println("═".repeat(50));
        System.out.println("1. 🔍 Rechercher étudiant par nom (LIKE %...%)");
        System.out.println("2. 🏫 Lister étudiants par niveau et établissement");
        System.out.println("3. 👶 Lister étudiants mineurs");
        System.out.println("4. ⏰ Lister retardataires pour un mois");
        System.out.println("0. ⬅️ Retour au menu principal");

        int choix = lireEntier("Votre choix: ");

        switch (choix) {
            case 1 -> rechercherEtudiantParNom();
            case 2 -> listerEtudiantsParNiveauEtEtablissement();
            case 3 -> listerEtudiantsMineurs();
            case 4 -> listerRetardataires();
            case 0 -> { /* Retour au menu principal */ }
            default -> System.out.println("❌ Choix invalide!");
        }
    }

    private void menuNotifications() {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("            📧 NOTIFICATIONS 📧");
        System.out.println("═".repeat(50));
        System.out.println("1. 📬 Notifier retardataires (après 3 semaines)");
        System.out.println("2. 🔧 Tester connexion email");
        System.out.println("0. ⬅️ Retour au menu principal");

        int choix = lireEntier("Votre choix: ");

        switch (choix) {
            case 1 -> notifierRetardataires();
            case 2 -> testerConnexionEmail();
            case 0 -> { /* Retour au menu principal */ }
            default -> System.out.println("❌ Choix invalide!");
        }
    }

    // ===================== MÉTHODES ÉTUDIANTS =====================

    private void creerEtudiant() {
        System.out.println("\n➕ CRÉER UN NOUVEL ÉTUDIANT");
        System.out.println("-".repeat(30));

        try {
            String matricule = lireChaine("Matricule: ");
            if (matricule.isEmpty()) {
                System.out.println("❌ Le matricule ne peut pas être vide.");
                return;
            }

            String anneeUniv = lireChaine("Année universitaire (ex: 2023-2024): ");
            if (anneeUniv.isEmpty()) {
                System.out.println("❌ L'année universitaire ne peut pas être vide.");
                return;
            }

            // Vérifier si l'étudiant existe déjà
            if (bourseService.obtenirEtudiant(matricule, anneeUniv) != null) {
                System.out.println("❌ Un étudiant avec ce matricule existe déjà pour cette année.");
                return;
            }

            String nom = lireChaine("Nom complet: ");
            String sexe = lireChaine("Sexe (M/F): ").toUpperCase();
            if (!sexe.equals("M") && !sexe.equals("F")) {
                System.out.println("❌ Sexe invalide. Utilisez M ou F.");
                return;
            }

            LocalDate datenais = lireDate("Date de naissance (yyyy-mm-dd): ");
            String institution = lireChaine("Institution: ");
            String mail = lireChaine("Email: ");
            String idniv = lireChaine("ID Niveau (L1, L2, L3, M1, M2): ");

            Etudiant etudiant = new Etudiant(matricule, anneeUniv, nom, sexe, datenais, institution, mail, idniv);

            if (bourseService.creerEtudiant(etudiant)) {
                System.out.println("✅ Étudiant créé avec succès!");
                System.out.println("👤 " + nom + " (" + matricule + ") - Age: " + etudiant.getAge() + " ans");
            } else {
                System.out.println("❌ Erreur lors de la création de l'étudiant.");
            }
        } catch (Exception e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }

    private void listerEtudiants() {
        System.out.println("\n📋 LISTE DES ÉTUDIANTS");
        System.out.println("=".repeat(120));

        List<Etudiant> etudiants = bourseService.listerEtudiants();

        if (etudiants.isEmpty()) {
            System.out.println("📭 Aucun étudiant trouvé.");
            return;
        }

        System.out.printf("%-10s %-12s %-25s %-5s %-12s %-4s %-15s %-25s %-8s%n",
                "Matricule", "Année", "Nom", "Sexe", "Naissance", "Age", "Institution", "Email", "Niveau");
        System.out.println("-".repeat(120));

        for (Etudiant etudiant : etudiants) {
            System.out.printf("%-10s %-12s %-25s %-5s %-12s %-4d %-15s %-25s %-8s%n",
                    etudiant.getMatricule(),
                    etudiant.getAnneeUniv(),
                    etudiant.getNom().length() > 23 ? etudiant.getNom().substring(0, 23) + ".." : etudiant.getNom(),
                    etudiant.getSexe(),
                    etudiant.getDatenais(),
                    etudiant.getAge(),
                    etudiant.getInstitution().length() > 13 ? etudiant.getInstitution().substring(0, 13) + ".." : etudiant.getInstitution(),
                    etudiant.getMail().length() > 23 ? etudiant.getMail().substring(0, 23) + ".." : etudiant.getMail(),
                    etudiant.getIdniv());
        }
        System.out.println("\n👥 Total: " + etudiants.size() + " étudiants");
    }

    private void rechercherEtudiant() {
        System.out.println("\n🔍 RECHERCHER UN ÉTUDIANT");
        System.out.println("-".repeat(30));

        String matricule = lireChaine("Matricule: ");
        String anneeUniv = lireChaine("Année universitaire: ");

        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant != null) {
            System.out.println("\n✅ Étudiant trouvé:");
            afficherEtudiant(etudiant);
        } else {
            System.out.println("❌ Étudiant non trouvé.");
        }
    }

    private void modifierEtudiant() {
        System.out.println("\n✏️ MODIFIER UN ÉTUDIANT");
        System.out.println("-".repeat(30));

        String matricule = lireChaine("Matricule: ");
        String anneeUniv = lireChaine("Année universitaire: ");

        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant == null) {
            System.out.println("❌ Étudiant non trouvé.");
            return;
        }

        System.out.println("\n📋 Informations actuelles:");
        afficherEtudiant(etudiant);

        System.out.println("\n✏️ Nouvelles informations (laissez vide pour garder la valeur actuelle):");

        String nom = lireChaine("Nom [" + etudiant.getNom() + "]: ");
        if (!nom.isEmpty()) etudiant.setNom(nom);

        String sexe = lireChaine("Sexe [" + etudiant.getSexe() + "]: ");
        if (!sexe.isEmpty()) etudiant.setSexe(sexe.toUpperCase());

        String dateStr = lireChaine("Date de naissance [" + etudiant.getDatenais() + "]: ");
        if (!dateStr.isEmpty()) {
            try {
                etudiant.setDatenais(LocalDate.parse(dateStr));
            } catch (DateTimeParseException e) {
                System.out.println("⚠️ Format de date invalide, valeur conservée.");
            }
        }

        String institution = lireChaine("Institution [" + etudiant.getInstitution() + "]: ");
        if (!institution.isEmpty()) etudiant.setInstitution(institution);

        String mail = lireChaine("Email [" + etudiant.getMail() + "]: ");
        if (!mail.isEmpty()) etudiant.setMail(mail);

        String idniv = lireChaine("ID Niveau [" + etudiant.getIdniv() + "]: ");
        if (!idniv.isEmpty()) etudiant.setIdniv(idniv);

        if (bourseService.modifierEtudiant(etudiant)) {
            System.out.println("✅ Étudiant modifié avec succès!");
        } else {
            System.out.println("❌ Erreur lors de la modification.");
        }
    }

    private void supprimerEtudiant() {
        System.out.println("\n🗑️ SUPPRIMER UN ÉTUDIANT");
        System.out.println("-".repeat(30));

        String matricule = lireChaine("Matricule: ");
        String anneeUniv = lireChaine("Année universitaire: ");

        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant == null) {
            System.out.println("❌ Étudiant non trouvé.");
            return;
        }

        System.out.println("\n⚠️ Étudiant à supprimer:");
        afficherEtudiant(etudiant);

        System.out.println("\n🚨 ATTENTION: Cette action supprimera également tous les paiements de l'étudiant!");
        String confirmation = lireChaine("Êtes-vous sûr de vouloir supprimer cet étudiant? (oui/non): ");
        if (confirmation.equalsIgnoreCase("oui")) {
            if (bourseService.supprimerEtudiant(matricule, anneeUniv)) {
                System.out.println("✅ Étudiant supprimé avec succès!");
            } else {
                System.out.println("❌ Erreur lors de la suppression.");
            }
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    // ===================== MÉTHODES MONTANTS =====================

    private void creerMontant() {
        System.out.println("\n➕ CRÉER UN NOUVEAU MONTANT");
        System.out.println("-".repeat(30));

        String idniv = lireChaine("ID Niveau (ex: L1, L2, M1, EQUIP): ");
        if (idniv.isEmpty()) {
            System.out.println("❌ L'ID niveau ne peut pas être vide.");
            return;
        }

        // Vérifier si le montant existe déjà
        if (bourseService.obtenirMontant(idniv) != null) {
            System.out.println("❌ Un montant avec cet ID existe déjà.");
            return;
        }

        String niveau = lireChaine("Description du niveau: ");
        int montant = lireEntier("Montant (en Ariary): ");

        if (montant <= 0) {
            System.out.println("❌ Le montant doit être positif.");
            return;
        }

        Montant nouveauMontant = new Montant(idniv, niveau, montant);

        if (bourseService.creerMontant(nouveauMontant)) {
            System.out.println("✅ Montant créé avec succès!");
            System.out.printf("💰 %s - %s: %,d Ar%n", idniv, niveau, montant);
        } else {
            System.out.println("❌ Erreur lors de la création du montant.");
        }
    }

    private void listerMontants() {
        System.out.println("\n📋 LISTE DES MONTANTS");
        System.out.println("=".repeat(60));

        List<Montant> montants = bourseService.listerMontants();

        if (montants.isEmpty()) {
            System.out.println("📭 Aucun montant trouvé.");
            return;
        }

        System.out.printf("%-10s %-25s %-20s%n", "ID Niveau", "Description", "Montant");
        System.out.println("-".repeat(60));

        int totalMontants = 0;
        for (Montant montant : montants) {
            System.out.printf("%-10s %-25s %,15d Ar%n",
                    montant.getIdniv(),
                    montant.getNiveau(),
                    montant.getMontant());
            totalMontants += montant.getMontant();
        }

        System.out.println("-".repeat(60));
        System.out.printf("💰 Total des montants: %,d Ar%n", totalMontants);
        System.out.println("📊 Nombre de niveaux: " + montants.size());
    }

    private void modifierMontant() {
        System.out.println("\n✏️ MODIFIER UN MONTANT");
        System.out.println("-".repeat(30));

        String idniv = lireChaine("ID Niveau: ");

        Montant montant = bourseService.obtenirMontant(idniv);
        if (montant == null) {
            System.out.println("❌ Montant non trouvé.");
            return;
        }

        System.out.println("\n📋 Informations actuelles:");
        System.out.println("ID: " + montant.getIdniv());
        System.out.println("Niveau: " + montant.getNiveau());
        System.out.println("Montant: " + String.format("%,d Ar", montant.getMontant()));

        String niveau = lireChaine("Nouveau niveau [" + montant.getNiveau() + "]: ");
        if (!niveau.isEmpty()) montant.setNiveau(niveau);

        String montantStr = lireChaine("Nouveau montant [" + montant.getMontant() + "]: ");
        if (!montantStr.isEmpty()) {
            try {
                int nouveauMontant = Integer.parseInt(montantStr);
                if (nouveauMontant <= 0) {
                    System.out.println("❌ Le montant doit être positif.");
                    return;
                }
                montant.setMontant(nouveauMontant);
            } catch (NumberFormatException e) {
                System.out.println("❌ Montant invalide, valeur conservée.");
            }
        }

        if (bourseService.modifierMontant(montant)) {
            System.out.println("✅ Montant modifié avec succès!");
        } else {
            System.out.println("❌ Erreur lors de la modification.");
        }
    }

    private void supprimerMontant() {
        System.out.println("\n🗑️ SUPPRIMER UN MONTANT");
        System.out.println("-".repeat(30));

        String idniv = lireChaine("ID Niveau: ");

        Montant montant = bourseService.obtenirMontant(idniv);
        if (montant == null) {
            System.out.println("❌ Montant non trouvé.");
            return;
        }

        System.out.println("\n⚠️ Montant à supprimer:");
        System.out.println("ID: " + montant.getIdniv());
        System.out.println("Niveau: " + montant.getNiveau());
        System.out.println("Montant: " + String.format("%,d Ar", montant.getMontant()));

        System.out.println("\n🚨 ATTENTION: Vérifiez qu'aucun étudiant n'utilise ce niveau!");
        String confirmation = lireChaine("Êtes-vous sûr? (oui/non): ");
        if (confirmation.equalsIgnoreCase("oui")) {
            if (bourseService.supprimerMontant(idniv)) {
                System.out.println("✅ Montant supprimé avec succès!");
            } else {
                System.out.println("❌ Erreur lors de la suppression (peut-être utilisé par des étudiants).");
            }
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    // ===================== MÉTHODES PAIEMENTS =====================

    private void effectuerPaiement() {
        System.out.println("\n✅ EFFECTUER UN PAIEMENT");
        System.out.println("-".repeat(30));

        String matricule = lireChaine("Matricule de l'étudiant: ");
        String anneeUniv = lireChaine("Année universitaire: ");

        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant == null) {
            System.out.println("❌ Étudiant non trouvé.");
            return;
        }

        System.out.println("\n👤 Étudiant: " + etudiant.getNom() + " (" + matricule + ")");
        System.out.println("🏫 Institution: " + etudiant.getInstitution());
        System.out.println("🎓 Niveau: " + etudiant.getIdniv());

        int nbrMois = lireEntier("Nombre de mois à payer: ");
        if (nbrMois <= 0) {
            System.out.println("❌ Le nombre de mois doit être positif.");
            return;
        }

        // Afficher le montant qui sera payé
        Montant montantNiveau = bourseService.obtenirMontant(etudiant.getIdniv());
        if (montantNiveau != null) {
            int total = montantNiveau.getMontant() * nbrMois;
            System.out.printf("💰 Montant à payer: %,d Ar x %d mois = %,d Ar%n",
                    montantNiveau.getMontant(), nbrMois, total);
        }

        String confirmation = lireChaine("Confirmer le paiement? (oui/non): ");
        if (!confirmation.equalsIgnoreCase("oui")) {
            System.out.println("❌ Paiement annulé.");
            return;
        }

        if (bourseService.effectuerPaiement(matricule, anneeUniv, nbrMois)) {
            System.out.println("✅ Paiement effectué avec succès!");
            System.out.println("📅 Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            System.out.println("❌ Erreur lors du paiement.");
        }
    }

    private void listerPaiements() {
        System.out.println("\n📋 LISTE DES PAIEMENTS");
        System.out.println("=".repeat(80));

        List<Payer> paiements = bourseService.listerPaiements();

        if (paiements.isEmpty()) {
            System.out.println("📭 Aucun paiement trouvé.");
            return;
        }

        System.out.printf("%-15s %-10s %-12s %-20s %-8s%n",
                "ID Paiement", "Matricule", "Année", "Date", "Nb Mois");
        System.out.println("-".repeat(80));

        for (Payer paiement : paiements) {
            String idTrunc = paiement.getIdpaye().length() > 12 ?
                    paiement.getIdpaye().substring(0, 12) + "..." : paiement.getIdpaye();

            System.out.printf("%-15s %-10s %-12s %-20s %-8d%n",
                    idTrunc,
                    paiement.getMatricule(),
                    paiement.getAnneeUniv(),
                    paiement.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    paiement.getNbrMois());
        }
        System.out.println("\n💳 Total: " + paiements.size() + " paiements");
    }

    private void modifierPaiement() {
        System.out.println("\n✏️ MODIFIER UN PAIEMENT");
        System.out.println("-".repeat(30));

        String idpaye = lireChaine("ID du paiement: ");

        Payer paiement = bourseService.obtenirPaiement(idpaye);
        if (paiement == null) {
            System.out.println("❌ Paiement non trouvé.");
            return;
        }

        System.out.println("\n📋 Informations actuelles:");
        System.out.println("ID: " + paiement.getIdpaye());
        System.out.println("Matricule: " + paiement.getMatricule());
        System.out.println("Année: " + paiement.getAnneeUniv());
        System.out.println("Date: " + paiement.getDate());
        System.out.println("Nombre de mois: " + paiement.getNbrMois());

        String nbrMoisStr = lireChaine("Nouveau nombre de mois [" + paiement.getNbrMois() + "]: ");
        if (!nbrMoisStr.isEmpty()) {
            try {
                int nouveauNbrMois = Integer.parseInt(nbrMoisStr);
                if (nouveauNbrMois <= 0) {
                    System.out.println("❌ Le nombre de mois doit être positif.");
                    return;
                }
                paiement.setNbrMois(nouveauNbrMois);
            } catch (NumberFormatException e) {
                System.out.println("❌ Nombre invalide, valeur conservée.");
            }
        }

        if (bourseService.modifierPaiement(paiement)) {
            System.out.println("✅ Paiement modifié avec succès!");
        } else {
            System.out.println("❌ Erreur lors de la modification.");
        }
    }

    private void supprimerPaiement() {
        System.out.println("\n🗑️ SUPPRIMER UN PAIEMENT");
        System.out.println("-".repeat(30));

        String idpaye = lireChaine("ID du paiement: ");

        Payer paiement = bourseService.obtenirPaiement(idpaye);
        if (paiement == null) {
            System.out.println("❌ Paiement non trouvé.");
            return;
        }

        System.out.println("\n⚠️ Paiement à supprimer:");
        System.out.println("ID: " + paiement.getIdpaye());
        System.out.println("Matricule: " + paiement.getMatricule());
        System.out.println("Date: " + paiement.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("Mois: " + paiement.getNbrMois());

        String confirmation = lireChaine("Êtes-vous sûr? (oui/non): ");
        if (confirmation.equalsIgnoreCase("oui")) {
            if (bourseService.supprimerPaiement(idpaye)) {
                System.out.println("✅ Paiement supprimé avec succès!");
            } else {
                System.out.println("❌ Erreur lors de la suppression.");
            }
        } else {
            System.out.println("❌ Suppression annulée.");
        }
    }

    private void genererRecuPDF() {
        System.out.println("\n📄 GÉNÉRER REÇU PDF");
        System.out.println("-".repeat(30));

        String matricule = lireChaine("Matricule de l'étudiant: ");
        String anneeUniv = lireChaine("Année universitaire: ");

        Etudiant etudiant = bourseService.obtenirEtudiant(matricule, anneeUniv);
        if (etudiant == null) {
            System.out.println("❌ Étudiant non trouvé.");
            return;
        }

        String cheminFichier = lireChaine("Nom du fichier PDF [recu_" + matricule + ".pdf]: ");
        if (cheminFichier.isEmpty()) {
            cheminFichier = "recu_" + matricule + ".pdf";
        }

        if (!cheminFichier.toLowerCase().endsWith(".pdf")) {
            cheminFichier += ".pdf";
        }

        System.out.println("\n📄 Génération du reçu pour " + etudiant.getNom() + "...");

        if (bourseService.genererRecuPaiement(matricule, anneeUniv, cheminFichier)) {
            System.out.println("✅ Reçu PDF généré avec succès!");
            System.out.println("📁 Fichier: " + cheminFichier);
        } else {
            System.out.println("❌ Erreur lors de la génération du reçu.");
        }
    }

    // ===================== MÉTHODES RAPPORTS =====================

    private void rechercherEtudiantParNom() {
        System.out.println("\n🔍 RECHERCHE PAR NOM (LIKE %...%)");
        System.out.println("-".repeat(40));

        String nom = lireChaine("Nom à rechercher (partiel accepté): ");
        if (nom.isEmpty()) {
            System.out.println("❌ Le nom ne peut pas être vide.");
            return;
        }

        List<Etudiant> etudiants = bourseService.rechercherEtudiants(nom);

        if (etudiants.isEmpty()) {
            System.out.println("❌ Aucun étudiant trouvé avec le nom contenant: " + nom);
            return;
        }

        System.out.println("\n✅ Étudiants trouvés (" + etudiants.size() + "):");
        System.out.println("=".repeat(80));

        for (Etudiant etudiant : etudiants) {
            afficherEtudiantResume(etudiant);
            System.out.println("-".repeat(50));
        }
    }

    private void listerEtudiantsParNiveauEtEtablissement() {
        System.out.println("\n🏫 ÉTUDIANTS PAR NIVEAU ET ÉTABLISSEMENT");
        System.out.println("=".repeat(60));

        Map<String, List<Etudiant>> groupes = bourseService.listerEtudiantsParNiveauEtEtablissement();

        if (groupes.isEmpty()) {
            System.out.println("📭 Aucune donnée trouvée.");
            return;
        }

        for (Map.Entry<String, List<Etudiant>> entry : groupes.entrySet()) {
            System.out.println("\n📚 " + entry.getKey() + " (" + entry.getValue().size() + " étudiants):");
            System.out.println("-".repeat(40));

            for (Etudiant etudiant : entry.getValue()) {
                System.out.printf("  👤 %-25s (%s) - %d ans%n",
                        etudiant.getNom(),
                        etudiant.getMatricule(),
                        etudiant.getAge());
            }
        }

        int totalEtudiants = groupes.values().stream().mapToInt(List::size).sum();
        System.out.println("\n👥 Total général: " + totalEtudiants + " étudiants dans " + groupes.size() + " groupes");
    }

    private void listerEtudiantsMineurs() {
        System.out.println("\n👶 ÉTUDIANTS MINEURS (< 18 ans)");
        System.out.println("=".repeat(50));

        List<Etudiant> mineurs = bourseService.listerEtudiantsMineurs();

        if (mineurs.isEmpty()) {
            System.out.println("✅ Aucun étudiant mineur trouvé.");
            return;
        }

        System.out.println("⚠️ " + mineurs.size() + " étudiant(s) mineur(s) trouvé(s):");
        System.out.println("-".repeat(50));

        for (Etudiant etudiant : mineurs) {
            System.out.printf("👶 %-25s (%s) - %d ans - %s%n",
                    etudiant.getNom(),
                    etudiant.getMatricule(),
                    etudiant.getAge(),
                    etudiant.getInstitution());
        }
    }

    private void listerRetardataires() {
        System.out.println("\n⏰ RETARDATAIRES POUR UN MOIS");
        System.out.println("-".repeat(40));

        String moisStr = lireChaine("Mois à vérifier (yyyy-mm, ex: 2024-03): ");
        if (moisStr.isEmpty()) {
            System.out.println("❌ Le mois ne peut pas être vide.");
            return;
        }

        try {
            YearMonth mois = YearMonth.parse(moisStr);
            List<Etudiant> retardataires = bourseService.obtenirRetardataires(mois, false);

            if (retardataires.isEmpty()) {
                System.out.println("✅ Aucun retardataire pour " + mois.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)) + ".");
                return;
            }

            System.out.println("\n⚠️ Retardataires pour " + mois.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)) + " (" + retardataires.size() + "):");
            System.out.println("=".repeat(80));

            for (Etudiant etudiant : retardataires) {
                System.out.printf("📧 %-25s (%s) - %s - %s%n",
                        etudiant.getNom(),
                        etudiant.getMatricule(),
                        etudiant.getInstitution(),
                        etudiant.getMail());
            }

            System.out.println("\n💡 Utilisez l'option 'Notifications' pour envoyer des rappels à ces étudiants.");

        } catch (DateTimeParseException e) {
            System.out.println("❌ Format de mois invalide. Utilisez yyyy-mm (ex: 2024-03).");
        }
    }

    // ===================== MÉTHODES NOTIFICATIONS =====================

    private void notifierRetardataires() {
        System.out.println("\n📬 NOTIFIER RETARDATAIRES (après 3 semaines)");
        System.out.println("-".repeat(50));

        String moisStr = lireChaine("Mois de référence (yyyy-mm): ");
        if (moisStr.isEmpty()) {
            System.out.println("❌ Le mois ne peut pas être vide.");
            return;
        }

        try {
            YearMonth mois = YearMonth.parse(moisStr);

            // Vérifier d'abord combien de retardataires
            List<Etudiant> retardataires = bourseService.obtenirRetardataires(mois, false);

            if (retardataires.isEmpty()) {
                System.out.println("✅ Aucun retardataire trouvé pour " + mois + ".");
                return;
            }

            System.out.println("\n📊 " + retardataires.size() + " retardataire(s) trouvé(s) pour " +
                    mois.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH)));

            for (Etudiant etudiant : retardataires) {
                System.out.println("  📧 " + etudiant.getNom() + " (" + etudiant.getMail() + ")");
            }

            System.out.println("\n⚠️ ATTENTION: Des emails de rappel vont être envoyés à ces étudiants.");
            String confirmation = lireChaine("Confirmer l'envoi des notifications? (oui/non): ");

            if (!confirmation.equalsIgnoreCase("oui")) {
                System.out.println("❌ Envoi annulé.");
                return;
            }

            System.out.println("\n📤 Envoi des notifications en cours...");

            if (bourseService.notifierRetardataires(mois)) {
                System.out.println("✅ Notifications envoyées avec succès!");
                System.out.println("📊 " + retardataires.size() + " email(s) de rappel envoyé(s).");
            } else {
                System.out.println("⚠️ Erreur lors de l'envoi des notifications.");
                System.out.println("💡 Vérifiez la configuration email dans database.properties");
            }

        } catch (DateTimeParseException e) {
            System.out.println("❌ Format de mois invalide. Utilisez yyyy-mm.");
        }
    }

    private void testerConnexionEmail() {
        System.out.println("\n🔧 TEST CONNEXION EMAIL");
        System.out.println("-".repeat(30));

        System.out.println("📧 Test de la connexion au serveur SMTP...");
        System.out.println("💡 Cette fonctionnalité nécessite la configuration email dans database.properties");
        System.out.println("⚠️ Fonctionnalité non implémentée dans cette version de l'interface.");
        System.out.println("📝 Pour tester: configurez les paramètres SMTP et utilisez la classe EmailService directement.");
    }

    // ===================== MÉTHODES UTILITAIRES =====================

    private void genererRecuExemple() {
        System.out.println("\n📄 GÉNÉRER REÇU D'EXEMPLE");
        System.out.println("-".repeat(35));

        String cheminFichier = lireChaine("Nom du fichier [recu_exemple.pdf]: ");
        if (cheminFichier.isEmpty()) {
            cheminFichier = "recu_exemple.pdf";
        }

        if (!cheminFichier.toLowerCase().endsWith(".pdf")) {
            cheminFichier += ".pdf";
        }

        System.out.println("\n📄 Génération du reçu d'exemple selon le format du sujet...");
        System.out.println("👤 RAKOTO Bernard - Matricule: 3432");
        System.out.println("🏫 ENI - Niveau: L2");
        System.out.println("💰 Équipement: 110.000 Ar + 3 mois à 23.000 Ar = 179.000 Ar");

        if (bourseService.genererRecuExemple(cheminFichier)) {
            System.out.println("\n✅ Reçu d'exemple généré avec succès!");
            System.out.println("📁 Fichier: " + cheminFichier);
            System.out.println("📝 Ce reçu correspond exactement au format du sujet (25 Avril 2024)");
        } else {
            System.out.println("❌ Erreur lors de la génération du reçu d'exemple.");
        }
    }

    private void afficherEtudiant(Etudiant etudiant) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║             FICHE ÉTUDIANT                ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.printf("║ Matricule      : %-24s ║%n", etudiant.getMatricule());
        System.out.printf("║ Année univ.    : %-24s ║%n", etudiant.getAnneeUniv());
        System.out.printf("║ Nom            : %-24s ║%n", etudiant.getNom());
        System.out.printf("║ Sexe           : %-24s ║%n", etudiant.getSexe());
        System.out.printf("║ Date naiss.    : %-24s ║%n", etudiant.getDatenais());
        System.out.printf("║ Âge            : %-19d ans ║%n", etudiant.getAge());
        System.out.printf("║ Statut         : %-24s ║%n", etudiant.isMineur() ? "Mineur ⚠️" : "Majeur");
        System.out.printf("║ Institution    : %-24s ║%n", etudiant.getInstitution());
        System.out.printf("║ Email          : %-24s ║%n", etudiant.getMail());
        System.out.printf("║ Niveau         : %-24s ║%n", etudiant.getIdniv());
        System.out.println("╚═══════════════════════════════════════════╝");
    }

    private void afficherEtudiantResume(Etudiant etudiant) {
        System.out.printf("👤 %-25s | 🆔 %-8s | 🎓 %-12s | 📧 %s%n",
                etudiant.getNom(),
                etudiant.getMatricule(),
                etudiant.getAnneeUniv(),
                etudiant.getMail());
        System.out.printf("   🏫 %-20s | 🎂 %d ans | 📚 %s%n",
                etudiant.getInstitution(),
                etudiant.getAge(),
                etudiant.getIdniv());
    }

    private String lireChaine(String prompt) {
        System.out.print("📝 " + prompt);
        return scanner.nextLine().trim();
    }

    private int lireEntier(String prompt) {
        while (true) {
            try {
                System.out.print("🔢 " + prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("❌ Veuillez entrer un nombre.");
                    continue;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Veuillez entrer un nombre valide.");
            }
        }
    }

    private LocalDate lireDate(String prompt) {
        while (true) {
            try {
                System.out.print("📅 " + prompt);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("❌ Veuillez entrer une date.");
                    continue;
                }
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Format de date invalide. Utilisez yyyy-mm-dd (ex: 2000-10-23).");
            }
        }
    }
}