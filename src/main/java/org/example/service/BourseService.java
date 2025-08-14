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
    public List<Etudiant> obtenirRetardataires(YearMonth mois) {
        if (mois == null) {
            logger.error("Mois null pour recherche retardataires");
            return List.of();
        }
        return payerDAO.findRetardataires(mois);
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
     * Selon le sujet : "trois semaines après"
     */
    public boolean notifierRetardataires(YearMonth mois) {
        try {
            if (mois == null) {
                logger.error("Mois null pour notification");
                return false;
            }

            List<Etudiant> retardataires = payerDAO.findRetardataires(mois);

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
                                "est en retard de plus de trois semaines.\n\n" +
                                "INFORMATIONS :\n" +
                                "- Matricule : %s\n" +
                                "- Année universitaire : %s\n" +
                                "- Institution : %s\n" +
                                "- Niveau : %s\n\n" +
                                "Veuillez régulariser votre situation IMMÉDIATEMENT auprès du service des bourses.\n" +
                                "Tout retard supplémentaire pourrait entraîner la suspension de votre bourse.\n\n" +
                                "Pour toute question, contactez-nous.\n\n" +
                                "Cordialement,\n" +
                                "Service de Gestion des Bourses Étudiantes",

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
}