package org.example.dao;

import org.example.config.DatabaseConfig;
import org.example.model.Etudiant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class EtudiantDAO {
    private static final Logger logger = LoggerFactory.getLogger(EtudiantDAO.class);
    
    public boolean create(Etudiant etudiant) {
        String sql = """
            INSERT INTO ETUDIANT (matricule, annee_univ, nom, sexe, datenais, institution, mail, idniv)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, etudiant.getMatricule());
            pstmt.setString(2, etudiant.getAnneeUniv());
            pstmt.setString(3, etudiant.getNom());
            pstmt.setString(4, etudiant.getSexe());
            pstmt.setDate(5, Date.valueOf(etudiant.getDatenais()));
            pstmt.setString(6, etudiant.getInstitution());
            pstmt.setString(7, etudiant.getMail());
            pstmt.setString(8, etudiant.getIdniv());
            
            int result = pstmt.executeUpdate();
            logger.info("Étudiant créé: {}", etudiant.getMatricule());
            return result > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la création de l'étudiant", e);
            return false;
        }
    }
    
    public Etudiant findById(String matricule, String anneeUniv) {
        String sql = """
            SELECT * FROM ETUDIANT 
            WHERE matricule = ? AND annee_univ = ?
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matricule);
            pstmt.setString(2, anneeUniv);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEtudiant(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche de l'étudiant", e);
        }
        return null;
    }
    
    public List<Etudiant> findAll() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM ETUDIANT ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                etudiants.add(mapResultSetToEtudiant(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des étudiants", e);
        }
        return etudiants;
    }
    
    public boolean update(Etudiant etudiant) {
        String sql = """
            UPDATE ETUDIANT 
            SET nom = ?, sexe = ?, datenais = ?, institution = ?, mail = ?, idniv = ?
            WHERE matricule = ? AND annee_univ = ?
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, etudiant.getNom());
            pstmt.setString(2, etudiant.getSexe());
            pstmt.setDate(3, Date.valueOf(etudiant.getDatenais()));
            pstmt.setString(4, etudiant.getInstitution());
            pstmt.setString(5, etudiant.getMail());
            pstmt.setString(6, etudiant.getIdniv());
            pstmt.setString(7, etudiant.getMatricule());
            pstmt.setString(8, etudiant.getAnneeUniv());
            
            int result = pstmt.executeUpdate();
            logger.info("Étudiant mis à jour: {}", etudiant.getMatricule());
            return result > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour de l'étudiant", e);
            return false;
        }
    }
    
    public boolean delete(String matricule, String anneeUniv) {
        String sql = "DELETE FROM ETUDIANT WHERE matricule = ? AND annee_univ = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, matricule);
            pstmt.setString(2, anneeUniv);
            
            int result = pstmt.executeUpdate();
            logger.info("Étudiant supprimé: {}", matricule);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de l'étudiant", e);
            return false;
        }
    }
    
    public List<Etudiant> searchByName(String nom) {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM ETUDIANT WHERE nom LIKE ? ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    etudiants.add(mapResultSetToEtudiant(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche d'étudiants", e);
        }
        return etudiants;
    }
    
    public Map<String, List<Etudiant>> findByNiveauAndInstitution() {
        Map<String, List<Etudiant>> result = new HashMap<>();
        String sql = """
            SELECT e.*, m.niveau 
            FROM ETUDIANT e 
            JOIN MONTANT m ON e.idniv = m.idniv 
            ORDER BY e.institution, m.niveau, e.nom
        """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Etudiant etudiant = mapResultSetToEtudiant(rs);
                String niveau = rs.getString("niveau");
                String key = etudiant.getInstitution() + " - " + niveau;
                
                result.computeIfAbsent(key, k -> new ArrayList<>()).add(etudiant);
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche par niveau et institution", e);
        }
        return result;
    }
    
    public List<Etudiant> findMineurs() {
        List<Etudiant> mineurs = new ArrayList<>();
        String sql = "SELECT * FROM ETUDIANT WHERE DATEDIFF(CURDATE(), datenais) < (18 * 365) ORDER BY nom";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                mineurs.add(mapResultSetToEtudiant(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche des mineurs", e);
        }
        return mineurs;
    }
    
    private Etudiant mapResultSetToEtudiant(ResultSet rs) throws SQLException {
        Etudiant etudiant = new Etudiant();
        etudiant.setMatricule(rs.getString("matricule"));
        etudiant.setAnneeUniv(rs.getString("annee_univ"));
        etudiant.setNom(rs.getString("nom"));
        etudiant.setSexe(rs.getString("sexe"));
        etudiant.setDatenais(rs.getDate("datenais").toLocalDate());
        etudiant.setInstitution(rs.getString("institution"));
        etudiant.setMail(rs.getString("mail"));
        etudiant.setIdniv(rs.getString("idniv"));
        return etudiant;
    }
}