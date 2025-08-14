package org.example.exception;

public class BourseException extends Exception {
    public BourseException(String message) {
        super(message);
    }
    
    public BourseException(String message, Throwable cause) {
        super(message, cause);
    }
}

class EtudiantNotFoundException extends BourseException {
    public EtudiantNotFoundException(String matricule) {
        super("Étudiant avec le matricule " + matricule + " non trouvé");
    }
}

class MontantNotFoundException extends BourseException {
    public MontantNotFoundException(String idniv) {
        super("Montant avec l'ID " + idniv + " non trouvé");
    }
}

class PaiementNotFoundException extends BourseException {
    public PaiementNotFoundException(String idpaye) {
        super("Paiement avec l'ID " + idpaye + " non trouvé");
    }
}

class DatabaseException extends BourseException {
    public DatabaseException(String message, Throwable cause) {
        super("Erreur de base de données: " + message, cause);
    }
}

class PDFGenerationException extends BourseException {
    public PDFGenerationException(String message, Throwable cause) {
        super("Erreur lors de la génération du PDF: " + message, cause);
    }
}

class EmailException extends BourseException {
    public EmailException(String message, Throwable cause) {
        super("Erreur lors de l'envoi de l'email: " + message, cause);
    }
}