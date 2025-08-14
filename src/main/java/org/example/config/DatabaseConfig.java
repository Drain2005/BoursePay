package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigUtil.getDatabaseUrl());
            config.setUsername(ConfigUtil.getDatabaseUsername());
            config.setPassword(ConfigUtil.getDatabasePassword());
            config.setDriverClassName(ConfigUtil.getDatabaseDriver());

            // Configuration du pool de connexions
            config.setMaximumPoolSize(ConfigUtil.getIntProperty("db.pool.maximumPoolSize", 10));
            config.setMinimumIdle(ConfigUtil.getIntProperty("db.pool.minimumIdle", 2));
            config.setConnectionTimeout(ConfigUtil.getIntProperty("db.pool.connectionTimeout", 30000));
            config.setIdleTimeout(ConfigUtil.getIntProperty("db.pool.idleTimeout", 600000));
            config.setMaxLifetime(ConfigUtil.getIntProperty("db.pool.maxLifetime", 1800000));

            dataSource = new HikariDataSource(config);

            // Créer la base de données et les tables si elles n'existent pas
            initializeDatabase();

            logger.info("Base de données initialisée avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de la base de données", e);
            throw new RuntimeException("Impossible d'initialiser la base de données", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Créer la table MONTANT en premier (référencée par ETUDIANT)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS MONTANT (
                    idniv VARCHAR(50) PRIMARY KEY,
                    niveau VARCHAR(100) NOT NULL,
                    montant INT NOT NULL
                )
            """);

            // Créer la table ETUDIANT avec clé composée selon le sujet
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS ETUDIANT (
                    matricule VARCHAR(50) NOT NULL,
                    annee_univ VARCHAR(20) NOT NULL,
                    nom VARCHAR(100) NOT NULL,
                    sexe VARCHAR(10) NOT NULL,
                    datenais DATE NOT NULL,
                    institution VARCHAR(100) NOT NULL,
                    mail VARCHAR(100) NOT NULL,
                    idniv VARCHAR(50) NOT NULL,
                    PRIMARY KEY (matricule, annee_univ),
                    FOREIGN KEY (idniv) REFERENCES MONTANT(idniv) ON DELETE RESTRICT
                )
            """);

            // Créer la table PAYER avec clé composée selon le sujet
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS PAYER (
                    idpaye VARCHAR(50) PRIMARY KEY,
                    matricule VARCHAR(50) NOT NULL,
                    annee_univ VARCHAR(20) NOT NULL,
                    date DATETIME NOT NULL,
                    nbr_mois INT NOT NULL,
                    FOREIGN KEY (matricule, annee_univ) REFERENCES ETUDIANT(matricule, annee_univ) ON DELETE CASCADE
                )
            """);

            // Insérer des données de test pour les montants si la table est vide
            stmt.executeUpdate("""
                INSERT IGNORE INTO MONTANT (idniv, niveau, montant) VALUES
                ('L1', 'Licence 1', 23000),
                ('L2', 'Licence 2', 23000),
                ('L3', 'Licence 3', 25000),
                ('M1', 'Master 1', 30000),
                ('M2', 'Master 2', 30000),
                ('EQUIP', 'Equipement', 110000)
            """);

            logger.info("Tables créées avec succès");
        }
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}