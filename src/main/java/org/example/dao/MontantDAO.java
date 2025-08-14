package org.example.dao;

import org.example.config.DatabaseConfig;
import org.example.model.Montant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MontantDAO {
    private static final Logger logger = LoggerFactory.getLogger(MontantDAO.class);
    
    public boolean create(Montant montant) {
        String sql = "INSERT INTO MONTANT (idniv, niveau, montant) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, montant.getIdniv());
            pstmt.setString(2, montant.getNiveau());
            pstmt.setInt(3, montant.getMontant());
            
            int result = pstmt.executeUpdate();
            logger.info("Montant créé: {}", montant.getIdniv());
            return result > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la création du montant", e);
            return false;
        }
    }
    
    public Montant findById(String idniv) {
        String sql = "SELECT * FROM MONTANT WHERE idniv = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, idniv);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMontant(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la recherche du montant", e);
        }
        return null;
    }
    
    public List<Montant> findAll() {
        List<Montant> montants = new ArrayList<>();
        String sql = "SELECT * FROM MONTANT ORDER BY niveau";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                montants.add(mapResultSetToMontant(rs));
            }
        } catch (SQLException e) {
            logger.error("Erreur lors de la récupération des montants", e);
        }
        return montants;
    }
    
    public boolean update(Montant montant) {
        String sql = "UPDATE MONTANT SET niveau = ?, montant = ? WHERE idniv = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, montant.getNiveau());
            pstmt.setInt(2, montant.getMontant());
            pstmt.setString(3, montant.getIdniv());
            
            int result = pstmt.executeUpdate();
            logger.info("Montant mis à jour: {}", montant.getIdniv());
            return result > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la mise à jour du montant", e);
            return false;
        }
    }
    
    public boolean delete(String idniv) {
        String sql = "DELETE FROM MONTANT WHERE idniv = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, idniv);
            
            int result = pstmt.executeUpdate();
            logger.info("Montant supprimé: {}", idniv);
            return result > 0;
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression du montant", e);
            return false;
        }
    }
    
    private Montant mapResultSetToMontant(ResultSet rs) throws SQLException {
        Montant montant = new Montant();
        montant.setIdniv(rs.getString("idniv"));
        montant.setNiveau(rs.getString("niveau"));
        montant.setMontant(rs.getInt("montant"));
        return montant;
    }
}