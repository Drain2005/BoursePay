package org.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                logger.warn("Fichier database.properties introuvable, utilisation des valeurs par défaut");
                setDefaultProperties();
                return;
            }
            properties.load(input);
            logger.info("Configuration chargée depuis database.properties");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la configuration", e);
            setDefaultProperties();
        }
    }

    private static void setDefaultProperties() {
        // Configuration par défaut pour MySQL (vous pouvez aussi utiliser PostgreSQL)
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/gestion_bourse?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        // Configuration des pools de connexions
        properties.setProperty("db.pool.maximumPoolSize", "10");
        properties.setProperty("db.pool.minimumIdle", "2");
        properties.setProperty("db.pool.connectionTimeout", "30000");
        properties.setProperty("db.pool.idleTimeout", "600000");
        properties.setProperty("db.pool.maxLifetime", "1800000");

        // Configuration email
        properties.setProperty("email.smtp.host", "smtp.gmail.com");
        properties.setProperty("email.smtp.port", "587");
        properties.setProperty("email.username", "votre.email@gmail.com");
        properties.setProperty("email.password", "votre_mot_de_passe_app");
        properties.setProperty("email.from.name", "Service des Bourses Étudiantes");
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Valeur invalide pour la propriété {}: {}, utilisation de la valeur par défaut: {}",
                    key, properties.getProperty(key), defaultValue);
            return defaultValue;
        }
    }

    // Méthodes spécifiques pour la base de données
    public static String getDatabaseUrl() {
        return getProperty("db.url");
    }

    public static String getDatabaseUsername() {
        return getProperty("db.username");
    }

    public static String getDatabasePassword() {
        return getProperty("db.password");
    }

    public static String getDatabaseDriver() {
        return getProperty("db.driver");
    }

    // Méthodes spécifiques pour l'email
    public static String getEmailHost() {
        return getProperty("email.smtp.host");
    }

    public static String getEmailPort() {
        return getProperty("email.smtp.port");
    }

    public static String getEmailUsername() {
        return getProperty("derainarakotoasimbol@gmail.com");
    }

    public static String getEmailPassword() {
        return getProperty("xsjg fjuz yhaq fliq");
    }

    public static String getEmailFromName() {
        return getProperty("email.from.name");
    }
}