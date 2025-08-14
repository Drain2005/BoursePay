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
                return;
            }
            properties.load(input);
            logger.info("Configuration chargée depuis database.properties");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la configuration", e);
        }
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
        return getProperty("db.url", "jdbc:mysql://localhost:3306/gestion_bourse?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
    }
    
    public static String getDatabaseUsername() {
        return getProperty("db.username", "root");
    }
    
    public static String getDatabasePassword() {
        return getProperty("db.password", "");
    }
    
    public static String getDatabaseDriver() {
        return getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }
    
    // Méthodes spécifiques pour l'email
    public static String getEmailHost() {
        return getProperty("email.smtp.host", "smtp.gmail.com");
    }
    
    public static String getEmailPort() {
        return getProperty("email.smtp.port", "587");
    }
    
    public static String getEmailUsername() {
        return getProperty("email.username", "votre.email@gmail.com");
    }
    
    public static String getEmailPassword() {
        return getProperty("email.password", "votre_mot_de_passe_app");
    }
    
    public static String getEmailFromName() {
        return getProperty("email.from.name", "Service des Bourses");
    }
}