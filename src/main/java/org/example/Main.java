package org.example;

import org.example.config.DatabaseConfig;
import org.example.ui.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // NE METTEZ PAS de UIManager.setLookAndFeel ici
        // Laissez les couleurs par défaut de Swing

        SwingUtilities.invokeLater(() -> {
            try {
                logger.info("Démarrage de l'application de gestion de bourse");

                // Testez la connexion à la base de données
                DatabaseConfig.getConnection().close();
                logger.info("Connexion à la base de données établie");

                // Créez et affichez la fenêtre principale
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);

            } catch (Exception e) {
                logger.error("Erreur fatale lors du démarrage de l'application", e);

                // Message d'erreur plus détaillé
                String errorMessage = "Impossible de démarrer l'application.\n\n";
                errorMessage += "Vérifiez que:\n";
                errorMessage += "1. MySQL est démarré\n";
                errorMessage += "2. La base 'gestion_bourse' existe\n";
                errorMessage += "3. Les identifiants dans database.properties sont corrects\n\n";
                errorMessage += "Erreur technique: " + e.getMessage();

                JOptionPane.showMessageDialog(null, errorMessage,
                        "Erreur de démarrage", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}