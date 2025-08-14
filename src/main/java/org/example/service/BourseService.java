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
import java.util.List;
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
    
    // CRUD Etudiant
    public boolean creerEtudiant(Etudiant etudiant) {
        return etudiantDAO.create(etudiant);
    }
    
    public List<Etudiant> listerEtudiants() {
        return etudiantDAO.findAll();
    }
    
    public Etudiant obtenirEtudiant(String matricule, String anneeUniv) {
        return etudiantDAO.findById(matricule, anneeUniv);
    }
    
    public boolean modifierEtudiant(Etudiant etudiant) {
        return etudiantDAO.update(etudiant);
    }
    
    public boolean supprimerEtudiant(String matricule, String anneeUniv) {
        return etudiantDAO.delete(matricule, anneeUniv);
    }
    
    // CRUD Montant
    public boolean creerMontant(Montant montant) {
        return montantDAO.create(montant);
    }
    
    public List<Montant> listerMontants() {
        return montantDAO.findAll();
    }
    
    public Montant obtenirMontant(String idniv) {
        return montantDAO.findById(idniv);
    }
    
    public boolean modifierMontant(Montant montant) {
        return montantDAO.update(montant);
    }
    
    public boolean supprimerMontant(String idniv) {
        return montantDAO.delete(idniv);
    }
    
    // CRUD Paiement
    public boolean creerPaiement(Payer payer) {
        if (payer.getIdpaye() == null || payer.getIdpaye().isEmpty()) {
            payer.setIdpaye(UUID.randomUUID().toString());
        }
        return payerDAO.create(payer);
    }
    
    public List<Payer> listerPaiements() {
        return payerDAO.findAll();
    }
    
    public Payer obtenirPaiement(String idpaye) {
        return payerDAO.findById(idpaye);
    }
    
    public boolean modifierPaiement(Payer payer) {
        return payerDAO.update(payer);
    }
    
    public boolean supprimerPaiement(String idpaye) {
        return payerDAO.delete(idpaye);
    }
    
    // Fonctionnalités spéciales
    public List<Etudiant> rechercherEtudiants(String nom) {
        return etudiantDAO.searchByName(nom);
    }
    
    public Map<String, List<Etudiant>> listerEtudiantsParNiveauEtEtablissement() {
        return etudiantDAO.findByNiveauAndInstitution();
    }
    
    public List<Etudiant> listerEtudiantsMineurs() {
        return etudiantDAO.findMineurs();
    }
    
    public List<Etudiant> obtenirRetardataires(YearMonth mois) {
        return payerDAO.findRetardataires(mois);
    }
    
    public boolean genererRecuPaiement(String matricule, String anneeUniv, String cheminFichier) {
        try {
            Etudiant etudiant = etudiantDAO.findById(matricule, anneeUniv);
            if (etudiant == null) {
                logger.error("Étudiant non trouvé: {}", matricule);
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
    
    public boolean notifierRetardataires(YearMonth mois) {
        try {
            List<Etudiant> retardataires = payerDAO.findRetardataires(mois);
            
            for (Etudiant etudiant : retardataires) {
                String sujet = "Rappel - Paiement de bourse en retard";
                String message = String.format(
                    "Bonjour %s,\n\n" +
                    "Nous vous rappelons que votre paiement de bourse pour le mois de %s est en retard.\n" +
                    "Veuillez régulariser votre situation dans les plus brefs délais.\n\n" +
                    "Cordialement,\n" +
                    "Service des Bourses",
                    etudiant.getNom(),
                    mois.toString()
                );
                
                emailService.envoyerEmail(etudiant.getMail(), sujet, message);
            }
            
            logger.info("Notifications envoyées à {} retardataires", retardataires.size());
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi des notifications", e);
            return false;
        }
    }
    
    public boolean effectuerPaiement(String matricule, String anneeUniv, int nbrMois) {
        Payer paiement = new Payer();
        paiement.setIdpaye(UUID.randomUUID().toString());
        paiement.setMatricule(matricule);
        paiement.setAnneeUniv(anneeUniv);
        paiement.setDate(LocalDateTime.now());
        paiement.setNbrMois(nbrMois);
        
        return creerPaiement(paiement);
    }
}