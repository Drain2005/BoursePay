package org.example.dao;

import org.example.config.DatabaseConfig;
import org.example.model.Payer;
import org.example.model.Etudiant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class PayerDAO {
    private static final Logger logger = LoggerFactory.getLogger(PayerDAO.class);

    public boolean create(Payer payer) {
        String sql = "INSERT INTO PAYER (idpaye, matricule, annee_univ, date, nbr_mois) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, payer.getIdpaye());
            pstmt.setString(2, payer.getMatricule());
            pstmt.setString(3, payer.getAnneeUniv());
            pstmt.setTimestamp(4, Timestamp.valueOf(payer.getDate()));
            pstmt.setInt(5, payer.getNbrMois());

            int result = pstmt.executeUpdate();
            logger.info("Paiement créé: {}", payer.getIdpaye());
            return result > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du paiement", e);
            return false;
        }
    }

    public Payer findById(String idpaye) {
        String sql = "SELECT * FROM PAYER WHERE idpaye = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idpaye);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayer(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du paiement", e);
        }
        return null;
    }

    public List<Payer> findAll() {
        List<Payer> paiements = new ArrayList<>();
        String sql = "SELECT * FROM PAYER ORDER BY date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                paiements.add(mapResultSetToPayer(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des paiements", e);
        }
        return paiements;
    }

    public List<Payer> findByEtudiant(String matricule, String anneeUniv) {
        List<Payer> paiements = new ArrayList<>();
        String sql = "SELECT * FROM PAYER WHERE matricule = ? AND annee_univ = ? ORDER BY date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matricule);
            pstmt.setString(2, anneeUniv);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    paiements.add(mapResultSetToPayer(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des paiements de l'étudiant", e);
        }
        return paiements;
    }

    public boolean update(Payer payer) {
        String sql = "UPDATE PAYER SET matricule = ?, annee_univ = ?, date = ?, nbr_mois = ? WHERE idpaye = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, payer.getMatricule());
            pstmt.setString(2, payer.getAnneeUniv());
            pstmt.setTimestamp(3, Timestamp.valueOf(payer.getDate()));
            pstmt.setInt(4, payer.getNbrMois());
            pstmt.setString(5, payer.getIdpaye());

            int result = pstmt.executeUpdate();
            logger.info("Paiement mis à jour: {}", payer.getIdpaye());
            return result > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du paiement", e);
            return false;
        }
    }

    public boolean delete(String idpaye) {
        String sql = "DELETE FROM PAYER WHERE idpaye = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idpaye);

            int result = pstmt.executeUpdate();
            logger.info("Paiement supprimé: {}", idpaye);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du paiement", e);
            return false;
        }
    }

    /**
     * Trouve les retardataires pour un mois donné
     * Un retardataire est un étudiant qui n'a pas payé sa bourse pour le mois spécifié
     * ET dont le délai de 3 semaines après la fin du mois est dépassé
     */
    public List<Etudiant> findRetardataires(YearMonth mois) {
        List<Etudiant> retardataires = new ArrayList<>();

        // Calculer la date limite (fin du mois + 3 semaines)
        LocalDateTime dateLimite = mois.atEndOfMonth().atTime(23, 59, 59).plusWeeks(3);
        LocalDateTime maintenant = LocalDateTime.now();

        // Vérifier si le délai de 3 semaines est dépassé
        if (maintenant.isBefore(dateLimite)) {
            logger.info("Le délai de 3 semaines pour {} n'est pas encore dépassé", mois);
            return retardataires;
        }

        // Requête pour trouver les étudiants qui n'ont PAS payé pour le mois spécifié
        String sql = """
            SELECT DISTINCT e.matricule, e.annee_univ, e.nom, e.sexe, e.datenais, e.institution, e.mail, e.idniv
            FROM ETUDIANT e
            WHERE NOT EXISTS (
                SELECT 1 FROM PAYER p 
                WHERE p.matricule = e.matricule 
                AND p.annee_univ = e.annee_univ 
                AND YEAR(p.date) = ? 
                AND MONTH(p.date) = ?
            )
            ORDER BY e.nom
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mois.getYear());
            pstmt.setInt(2, mois.getMonthValue());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Etudiant etudiant = new Etudiant();
                    etudiant.setMatricule(rs.getString("matricule"));
                    etudiant.setAnneeUniv(rs.getString("annee_univ"));
                    etudiant.setNom(rs.getString("nom"));
                    etudiant.setSexe(rs.getString("sexe"));
                    etudiant.setDatenais(rs.getDate("datenais").toLocalDate());
                    etudiant.setInstitution(rs.getString("institution"));
                    etudiant.setMail(rs.getString("mail"));
                    etudiant.setIdniv(rs.getString("idniv"));
                    retardataires.add(etudiant);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des retardataires", e);
        }

        logger.info("Trouvé {} retardataires pour {} (délai de 3 semaines dépassé)", retardataires.size(), mois);
        return retardataires;
    }

    /**
     * Vérifie si un étudiant a déjà payé pour un mois donné
     */
    public boolean hasPaymentForMonth(String matricule, String anneeUniv, YearMonth mois) {
        String sql = """
            SELECT COUNT(*) as count FROM PAYER 
            WHERE matricule = ? AND annee_univ = ? 
            AND YEAR(date) = ? AND MONTH(date) = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, matricule);
            pstmt.setString(2, anneeUniv);
            pstmt.setInt(3, mois.getYear());
            pstmt.setInt(4, mois.getMonthValue());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la vérification du paiement", e);
        }
        return false;
    }

    /**
     * Récupère tous les retardataires sans vérification de délai
     * (pour l'affichage administratif)
     */
    public List<Etudiant> findRetardatairesSansDelai(YearMonth mois) {
        List<Etudiant> retardataires = new ArrayList<>();

        String sql = """
            SELECT DISTINCT e.matricule, e.annee_univ, e.nom, e.sexe, e.datenais, e.institution, e.mail, e.idniv
            FROM ETUDIANT e
            WHERE NOT EXISTS (
                SELECT 1 FROM PAYER p 
                WHERE p.matricule = e.matricule 
                AND p.annee_univ = e.annee_univ 
                AND YEAR(p.date) = ? 
                AND MONTH(p.date) = ?
            )
            ORDER BY e.nom
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mois.getYear());
            pstmt.setInt(2, mois.getMonthValue());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Etudiant etudiant = new Etudiant();
                    etudiant.setMatricule(rs.getString("matricule"));
                    etudiant.setAnneeUniv(rs.getString("annee_univ"));
                    etudiant.setNom(rs.getString("nom"));
                    etudiant.setSexe(rs.getString("sexe"));
                    etudiant.setDatenais(rs.getDate("datenais").toLocalDate());
                    etudiant.setInstitution(rs.getString("institution"));
                    etudiant.setMail(rs.getString("mail"));
                    etudiant.setIdniv(rs.getString("idniv"));
                    retardataires.add(etudiant);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des retardataires", e);
        }
        return retardataires;
    }

    private Payer mapResultSetToPayer(ResultSet rs) throws SQLException {
        Payer payer = new Payer();
        payer.setIdpaye(rs.getString("idpaye"));
        payer.setMatricule(rs.getString("matricule"));
        payer.setAnneeUniv(rs.getString("annee_univ"));
        payer.setDate(rs.getTimestamp("date").toLocalDateTime());
        payer.setNbrMois(rs.getInt("nbr_mois"));
        return payer;
    }
}