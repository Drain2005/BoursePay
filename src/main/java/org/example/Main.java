package org.example;

import org.example.config.DatabaseConfig;
import org.example.ui.ConsoleUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Démarrage de l'application de gestion de bourse");

        try {
            // Vérifier la connexion à la base de données
            DatabaseConfig.getConnection().close();
            logger.info("Connexion à la base de données établie");

            // Démarrer l'interface utilisateur
            ConsoleUI ui = new ConsoleUI();
            ui.demarrer();

        } catch (Exception e) {
            logger.error("Erreur fatale lors du démarrage de l'application", e);
            System.err.println("Impossible de démarrer l'application. Vérifiez la configuration de la base de données.");
            System.err.println("Erreur: " + e.getMessage());
        } finally {
            // Fermer proprement la source de données
            DatabaseConfig.closeDataSource();
            logger.info("Application fermée");
        }
    }
}