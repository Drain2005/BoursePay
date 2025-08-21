package org.example.service;

import org.example.dao.EtudiantDAO;
import org.example.dao.MontantDAO;
import org.example.dao.PayerDAO;
import org.example.model.Etudiant;
import org.example.model.Montant;
import org.example.model.Payer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class BourseService {
    private static final Logger logger = LoggerFactory.getLogger(BourseService.class);

    private final EtudiantDAO etudiantDAO;
    private final MontantDAO montantDAO;
    private final PayerDAO payerDAO;
    private final PDFService pdfService;
    private final EmailService emailService;

    public BourseService() {
        this.etudiantDAO = new EtudiantDAO();
        this.montantDAO = new MontantDAO();
        this.payerDAO = new PayerDAO();
        this.pdfService = new PDFService();
        this.emailService = new EmailService();
    }

    // ===================== CRUD ETUDIANT =====================

    public boolean creerEtudiant(Etudiant etudiant) {
        if (etudiant == null) {
            logger.error("Tentative de création d'un étudiant null");
            return false;
        }
        return etudiantDAO.create(etudiant);
    }

    public List<Etudiant> listerEtudiants() {
        return etudiantDAO.findAll();
    }

    public Etudiant obtenirEtudiant(String matricule, String anneeUniv) {
        if (matricule == null || anneeUniv == null) {
            logger.error("Matricule ou année universitaire null");
            return null;
        }
        return etudiantDAO.findById(matricule, anneeUniv);
    }

    public boolean modifierEtudiant(Etudiant etudiant) {
        if (etudiant == null) {
            logger.error("Tentative de modification d'un étudiant null");
            return false;
        }
        return etudiantDAO.update(etudiant);
    }

    public boolean supprimerEtudiant(String matricule, String anneeUniv) {
        if (matricule == null || anneeUniv == null) {
            logger.error("Matricule ou année universitaire null pour suppression");
            return false;
        }
        return etudiantDAO.delete(matricule, anneeUniv);
    }

    // ===================== CRUD MONTANT =====================

    public boolean creerMontant(Montant montant) {
        if (montant == null) {
            logger.error("Tentative de création d'un montant null");
            return false;
        }
        return montantDAO.create(montant);
    }

    public List<Montant> listerMontants() {
        return montantDAO.findAll();
    }

    public Montant obtenirMontant(String idniv) {
        if (idniv == null) {
            logger.error("ID niveau null");
            return null;
        }
        return montantDAO.findById(idniv);
    }

    public boolean modifierMontant(Montant montant) {
        if (montant == null) {
            logger.error("Tentative de modification d'un montant null");
            return false;
        }
        return montantDAO.update(montant);
    }

    public boolean supprimerMontant(String idniv) {
        if (idniv == null) {
            logger.error("ID niveau null pour suppression");
            return false;
        }
        return montantDAO.delete(idniv);
    }

    // ===================== CRUD PAIEMENT =====================

    public boolean creerPaiement(Payer payer) {
        if (payer == null) {
            logger.error("Tentative de création d'un paiement null");
            return false;
        }
        if (payer.getIdpaye() == null || payer.getIdpaye().isEmpty()) {
            payer.setIdpaye(UUID.randomUUID().toString());
        }
        return payerDAO.create(payer);
    }

    public List<Payer> listerPaiements() {
        return payerDAO.findAll();
    }

    public Payer obtenirPaiement(String idpaye) {
        if (idpaye == null) {
            logger.error("ID paiement null");
            return null;
        }
        return payerDAO.findById(idpaye);
    }

    public boolean modifierPaiement(Payer payer) {
        if (payer == null) {
            logger.error("Tentative de modification d'un paiement null");
            return false;
        }
        return payerDAO.update(payer);
    }

    public boolean supprimerPaiement(String idpaye) {
        if (idpaye == null) {
            logger.error("ID paiement null pour suppression");
            return false;
        }
        return payerDAO.delete(idpaye);
    }

    // ===================== FONCTIONNALITES SPECIALES =====================

    /**
     * Recherche d'étudiants par nom en utilisant LIKE %...%
     */
    public List<Etudiant> rechercherEtudiants(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            logger.warn("Nom de recherche vide");
            return List.of();
        }
        return etudiantDAO.searchByName(nom.trim());
    }

    /**
     * Liste des étudiants par niveau et par chaque établissement
     */
    public Map<String, List<Etudiant>> listerEtudiantsParNiveauEtEtablissement() {
        return etudiantDAO.findByNiveauAndInstitution();
    }

    /**
     * Liste des étudiants mineurs (âge < 18 ans)
     */
    public List<Etudiant> listerEtudiantsMineurs() {
        return etudiantDAO.findMineurs();
    }

    /**
     * Liste des retardataires pour un mois donné
     * Un retardataire est un étudiant qui n'a pas payé sa bourse pour le mois spécifié
     */
    public List<Etudiant> obtenirRetardataires(YearMonth mois, boolean verifierDelai) {
        if (mois == null) {
            logger.error("Mois null pour recherche retardataires");
            return List.of();
        }

        if (verifierDelai) {
            return payerDAO.findRetardataires(mois); // Pour les notifications
        } else {
            return payerDAO.findRetardatairesSansDelai(mois); // Pour l'affichage
        }
    }

    /**
     * Génère un reçu de paiement PDF pour un étudiant
     */
    public boolean genererRecuPaiement(String matricule, String anneeUniv, String cheminFichier) {
        try {
            if (matricule == null || anneeUniv == null || cheminFichier == null) {
                logger.error("Paramètres null pour génération reçu");
                return false;
            }

            Etudiant etudiant = etudiantDAO.findById(matricule, anneeUniv);
            if (etudiant == null) {
                logger.error("Étudiant non trouvé: {} - {}", matricule, anneeUniv);
                return false;
            }

            List<Payer> paiements = payerDAO.findByEtudiant(matricule, anneeUniv);
            Montant montantNiveau = montantDAO.findById(etudiant.getIdniv());
            Montant equipement = montantDAO.findById("EQUIP");

            return pdfService.genererRecu(etudiant, paiements, montantNiveau, equipement, cheminFichier);
        } catch (Exception e) {
            logger.error("Erreur lors de la génération du reçu", e);
            return false;
        }
    }

    /**
     * Notifie par mail les retardataires concernant le délai
     * Version originale - maintenue pour compatibilité
     */
    public boolean notifierRetardataires(YearMonth mois) {
        return notifierRetardataires(mois, false);
    }

    /**
     * Notifie par mail les retardataires avec option de forçage
     * @param mois Le mois concerné
     * @param forceEnvoi Si true, ignore la vérification du délai de 3 semaines
     * @return true si au moins un email a été envoyé avec succès
     */
    public boolean notifierRetardataires(YearMonth mois, boolean forceEnvoi) {
        try {
            if (mois == null) {
                logger.error("Mois null pour notification");
                return false;
            }

            // Vérifier le délai de 3 semaines si forceEnvoi n'est pas activé
            if (!forceEnvoi && estDelaiDepasse(mois)) {
                logger.warn("Délai de 3 semaines dépassé pour {}, notifications non envoyées", mois);
                return false;
            }

            List<Etudiant> retardataires = payerDAO.findRetardatairesSansDelai(mois);

            if (retardataires.isEmpty()) {
                logger.info("Aucun retardataire trouvé pour {}", mois);
                return true;
            }

            String nomMois = mois.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            int annee = mois.getYear();

            int nbEnvoyes = 0;

            for (Etudiant etudiant : retardataires) {
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
                                "⚠️ ATTENTION : Veuillez régulariser votre situation IMMÉDIATEMENT.\n" +
                                "Tout retard supplémentaire pourrait entraîner des conséquences sur votre bourse.\n\n" +
                                "📞 Pour toute question, contactez-nous.\n\n" +
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

                if (emailService.envoyerEmail(etudiant.getMail(), sujet, message)) {
                    nbEnvoyes++;
                    logger.info("Notification envoyée à {} ({})", etudiant.getNom(), etudiant.getMail());
                } else {
                    logger.warn("Échec envoi notification à {} ({})", etudiant.getNom(), etudiant.getMail());
                }
            }

            logger.info("Notifications envoyées : {}/{} retardataires pour {}", nbEnvoyes, retardataires.size(), mois);
            return nbEnvoyes > 0;

        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi des notifications", e);
            return false;
        }
    }

    /**
     * Envoie un email individuel à un étudiant
     * @param destinataire L'adresse email du destinataire
     * @param sujet Le sujet de l'email
     * @param message Le contenu du message
     * @return true si l'email a été envoyé avec succès
     */
    public boolean envoyerEmailIndividuel(String destinataire, String sujet, String message) {
        if (destinataire == null || destinataire.trim().isEmpty()) {
            logger.error("Adresse email destinataire vide ou null");
            return false;
        }

        if (sujet == null || sujet.trim().isEmpty()) {
            logger.error("Sujet de l'email vide ou null");
            return false;
        }

        if (message == null || message.trim().isEmpty()) {
            logger.error("Message de l'email vide ou null");
            return false;
        }

        try {
            boolean success = emailService.envoyerEmail(destinataire, sujet, message);
            if (success) {
                logger.info("Email individuel envoyé avec succès à: {}", destinataire);
            } else {
                logger.warn("Échec de l'envoi de l'email individuel à: {}", destinataire);
            }
            return success;
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email individuel à: " + destinataire, e);
            return false;
        }
    }

    /**
     * Vérifie si le délai de 3 semaines est dépassé pour un mois donné
     * @param mois Le mois à vérifier
     * @return true si le délai de 3 semaines après la fin du mois est dépassé
     */
    public boolean estDelaiDepasse(YearMonth mois) {
        if (mois == null) {
            logger.error("Mois null pour vérification délai");
            return true;
        }

        LocalDateTime dateLimite = mois.atEndOfMonth().atTime(23, 59, 59).plusWeeks(3);
        LocalDateTime maintenant = LocalDateTime.now();

        boolean delaiDepasse = maintenant.isAfter(dateLimite);
        logger.debug("Vérification délai pour {} : limite={}, maintenant={}, dépassé={}",
                mois, dateLimite, maintenant, delaiDepasse);

        return delaiDepasse;
    }

    /**
     * Calcule le temps restant avant l'expiration du délai de 3 semaines
     * @param mois Le mois concerné
     * @return Une chaîne décrivant le temps restant ou "Délai dépassé"
     */
    public String getTempsRestantDelai(YearMonth mois) {
        if (mois == null) {
            return "Mois invalide";
        }

        LocalDateTime dateLimite = mois.atEndOfMonth().atTime(23, 59, 59).plusWeeks(3);
        LocalDateTime maintenant = LocalDateTime.now();

        if (maintenant.isAfter(dateLimite)) {
            return "Délai dépassé";
        }

        long joursRestants = java.time.temporal.ChronoUnit.DAYS.between(
                maintenant.toLocalDate(), dateLimite.toLocalDate());

        if (joursRestants > 1) {
            return joursRestants + " jours restants";
        } else if (joursRestants == 1) {
            return "1 jour restant";
        } else {
            long heuresRestantes = java.time.temporal.ChronoUnit.HOURS.between(maintenant, dateLimite);
            if (heuresRestantes > 1) {
                return heuresRestantes + " heures restantes";
            } else if (heuresRestantes == 1) {
                return "1 heure restante";
            } else {
                long minutesRestantes = java.time.temporal.ChronoUnit.MINUTES.between(maintenant, dateLimite);
                return minutesRestantes + " minutes restantes";
            }
        }
    }

    /**
     * Obtient la date limite pour les notifications d'un mois donné
     * @param mois Le mois concerné
     * @return La date limite (fin du mois + 3 semaines) ou null si mois invalide
     */
    public LocalDateTime getDateLimiteNotification(YearMonth mois) {
        if (mois == null) {
            return null;
        }
        return mois.atEndOfMonth().atTime(23, 59, 59).plusWeeks(3);
    }

    /**
     * Effectue un paiement pour un étudiant
     */
    public boolean effectuerPaiement(String matricule, String anneeUniv, int nbrMois) {
        if (matricule == null || anneeUniv == null || nbrMois <= 0) {
            logger.error("Paramètres invalides pour paiement");
            return false;
        }

        // Vérifier que l'étudiant existe
        Etudiant etudiant = etudiantDAO.findById(matricule, anneeUniv);
        if (etudiant == null) {
            logger.error("Étudiant non trouvé pour paiement: {} - {}", matricule, anneeUniv);
            return false;
        }

        Payer paiement = new Payer();
        paiement.setIdpaye(UUID.randomUUID().toString());
        paiement.setMatricule(matricule);
        paiement.setAnneeUniv(anneeUniv);
        paiement.setDate(LocalDateTime.now());
        paiement.setNbrMois(nbrMois);

        boolean succes = creerPaiement(paiement);
        if (succes) {
            logger.info("Paiement effectué : {} mois pour étudiant {} ({})", nbrMois, etudiant.getNom(), matricule);
        }
        return succes;
    }

    /**
     * Génère un reçu d'exemple selon le format du sujet
     */
    public boolean genererRecuExemple(String cheminFichier) {
        return pdfService.genererRecuExemple(cheminFichier != null ? cheminFichier : "recu_exemple.pdf");
    }

    /**
     * Vérifie si un étudiant a payé pour un mois donné
     */
    public boolean etudiantAPayePourMois(String matricule, String anneeUniv, YearMonth mois) {
        if (matricule == null || anneeUniv == null || mois == null) {
            return false;
        }
        return payerDAO.hasPaymentForMonth(matricule, anneeUniv, mois);
    }

    /**
     * Envoie une notification individuelle à un étudiant spécifique pour un mois donné
     * @param etudiant L'étudiant à notifier
     * @param mois Le mois concerné par la notification
     * @return true si la notification a été envoyée avec succès
     */
    public boolean envoyerNotificationIndividuelle(Etudiant etudiant, YearMonth mois) {
        if (etudiant == null) {
            logger.error("Étudiant null pour notification individuelle");
            return false;
        }

        if (mois == null) {
            logger.error("Mois null pour notification individuelle");
            return false;
        }

        // Vérifier si le délai est dépassé
        if (estDelaiDepasse(mois)) {
            logger.warn("Délai de 3 semaines dépassé pour {}, notification individuelle non envoyée à {}",
                    mois, etudiant.getMatricule());
            return false;
        }

        try {
            String nomMois = mois.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH);
            int annee = mois.getYear();

            // Vérifier si l'étudiant est retardataire
            boolean estRetardataire = !etudiantAPayePourMois(etudiant.getMatricule(), etudiant.getAnneeUniv(), mois);

            String sujet = String.format("RAPPEL %s - Paiement de bourse (%s %d)",
                    estRetardataire ? "URGENT" : "INFORMATIF", nomMois, annee);

            String message;
            if (estRetardataire) {
                // Message pour retardataire
                message = String.format(
                        "Bonjour %s,\n\n" +
                                "Nous vous informons que votre paiement de bourse pour le mois de %s %d " +
                                "n'a pas encore été effectué.\n\n" +
                                "INFORMATIONS DE VOTRE DOSSIER :\n" +
                                "• Matricule : %s\n" +
                                "• Année universitaire : %s\n" +
                                "• Institution : %s\n" +
                                "• Niveau : %s\n" +
                                "• Email : %s\n\n" +
                                "🚨 ATTENTION : Veuillez régulariser votre situation RAPIDEMENT.\n" +
                                "Délai restant : %s\n\n" +
                                "📞 Pour toute question ou assistance, contactez immédiatement le service des bourses.\n" +
                                "📧 Cette notification vous est envoyée personnellement suite à une vérification.\n\n" +
                                "Cordialement,\n" +
                                "Service de Gestion des Bourses Étudiantes\n\n" +
                                "---\n" +
                                "Ceci est un message automatique personnalisé. Pour toute question, contactez-nous.",

                        etudiant.getNom(),
                        nomMois, annee,
                        etudiant.getMatricule(),
                        etudiant.getAnneeUniv(),
                        etudiant.getInstitution(),
                        etudiant.getIdniv(),
                        etudiant.getMail(),
                        getTempsRestantDelai(mois)
                );
            } else {
                // Message informatif pour étudiant à jour
                message = String.format(
                        "Bonjour %s,\n\n" +
                                "Nous vous écrivons concernant votre bourse pour le mois de %s %d.\n\n" +
                                "INFORMATIONS DE VOTRE DOSSIER :\n" +
                                "• Matricule : %s\n" +
                                "• Année universitaire : %s\n" +
                                "• Institution : %s\n" +
                                "• Niveau : %s\n" +
                                "• Email : %s\n\n" +
                                "✅ STATUT : Votre paiement pour ce mois est à jour.\n" +
                                "📋 Cette notification vous est envoyée à titre informatif.\n\n" +
                                "📞 Pour toute question, n'hésitez pas à nous contacter.\n\n" +
                                "Cordialement,\n" +
                                "Service de Gestion des Bourses Étudiantes\n\n" +
                                "---\n" +
                                "Ceci est un message automatique personnalisé.",

                        etudiant.getNom(),
                        nomMois, annee,
                        etudiant.getMatricule(),
                        etudiant.getAnneeUniv(),
                        etudiant.getInstitution(),
                        etudiant.getIdniv(),
                        etudiant.getMail()
                );
            }

            boolean success = emailService.envoyerEmail(etudiant.getMail(), sujet, message);

            if (success) {
                logger.info("Notification individuelle envoyée avec succès à {} ({}) pour {}",
                        etudiant.getNom(), etudiant.getMail(), mois);
            } else {
                logger.warn("Échec de l'envoi de notification individuelle à {} ({}) pour {}",
                        etudiant.getNom(), etudiant.getMail(), mois);
            }

            return success;

        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de notification individuelle à {} pour {}",
                    etudiant.getMatricule(), mois, e);
            return false;
        }
    }

    /**
     * Recherche d'étudiants par nom, matricule ou email
     * @param terme Le terme de recherche
     * @return Liste des étudiants correspondants
     */
    public List<Etudiant> rechercherEtudiantsAvance(String terme) {
        if (terme == null || terme.trim().isEmpty()) {
            return listerEtudiants();
        }

        terme = terme.trim().toLowerCase();
        List<Etudiant> resultats = new ArrayList<>();
        List<Etudiant> tousEtudiants = listerEtudiants();

        for (Etudiant etudiant : tousEtudiants) {
            boolean correspond = false;

            // Recherche par nom
            if (etudiant.getNom() != null && etudiant.getNom().toLowerCase().contains(terme)) {
                correspond = true;
            }

            // Recherche par matricule
            if (etudiant.getMatricule() != null && etudiant.getMatricule().toLowerCase().contains(terme)) {
                correspond = true;
            }

            // Recherche par email
            if (etudiant.getMail() != null && etudiant.getMail().toLowerCase().contains(terme)) {
                correspond = true;
            }

            // Recherche par institution
            if (etudiant.getInstitution() != null && etudiant.getInstitution().toLowerCase().contains(terme)) {
                correspond = true;
            }

            if (correspond) {
                resultats.add(etudiant);
            }
        }

        logger.debug("Recherche '{}' : {} résultat(s) trouvé(s)", terme, resultats.size());
        return resultats;
    }

    /**
     * Obtient des statistiques sur les retardataires
     * @param mois Le mois à analyser
     * @return Un objet contenant les statistiques
     */
    public StatistiquesRetardataires getStatistiquesRetardataires(YearMonth mois) {
        if (mois == null) {
            return new StatistiquesRetardataires(0, 0, true, "Mois invalide");
        }

        List<Etudiant> retardataires = payerDAO.findRetardatairesSansDelai(mois);
        int totalEtudiants = listerEtudiants().size();
        boolean delaiDepasse = estDelaiDepasse(mois);
        String tempsRestant = getTempsRestantDelai(mois);

        return new StatistiquesRetardataires(retardataires.size(), totalEtudiants, delaiDepasse, tempsRestant);
    }

    /**
     * Classe interne pour les statistiques des retardataires
     */
    public static class StatistiquesRetardataires {
        private final int nombreRetardataires;
        private final int totalEtudiants;
        private final boolean delaiDepasse;
        private final String tempsRestant;

        public StatistiquesRetardataires(int nombreRetardataires, int totalEtudiants, boolean delaiDepasse, String tempsRestant) {
            this.nombreRetardataires = nombreRetardataires;
            this.totalEtudiants = totalEtudiants;
            this.delaiDepasse = delaiDepasse;
            this.tempsRestant = tempsRestant;
        }

        public int getNombreRetardataires() { return nombreRetardataires; }
        public int getTotalEtudiants() { return totalEtudiants; }
        public boolean isDelaiDepasse() { return delaiDepasse; }
        public String getTempsRestant() { return tempsRestant; }
        public double getPourcentageRetardataires() {
            return totalEtudiants > 0 ? (nombreRetardataires * 100.0 / totalEtudiants) : 0;
        }
    }
}