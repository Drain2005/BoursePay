-- Création de la base de données
CREATE DATABASE scholarship;

-- Connexion à la base de données
\c scholarship

-- Création de la table etudiant
CREATE TABLE IF NOT EXISTS etudiant (
    matricule VARCHAR(50) PRIMARY KEY,
    annee_univ VARCHAR(10) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    sexe VARCHAR(10) CHECK (sexe IN ('Masculin', 'Féminin')),
    date_naissance DATE,
    institution VARCHAR(100),
    mail VARCHAR(100) UNIQUE,
    idniv VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table montant
CREATE TABLE IF NOT EXISTS montant (
    idniv VARCHAR(50) PRIMARY KEY,
    niveau VARCHAR(50) NOT NULL,
    montant INTEGER NOT NULL CHECK (montant > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Création de la table paiement (nom correct selon le sujet)
CREATE TABLE IF NOT EXISTS paiement (
    idpaye VARCHAR(50) PRIMARY KEY,
    matricule VARCHAR(50) NOT NULL,
    annee_univ VARCHAR(10) NOT NULL,
    date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nbr_mois INTEGER NOT NULL DEFAULT 1 CHECK (nbr_mois > 0),
    montant INTEGER NOT NULL CHECK (montant > 0),
    mois VARCHAR(2) NOT NULL,
    annee INTEGER NOT NULL,
    type_paiement VARCHAR(20) DEFAULT 'MENSUEL' CHECK (type_paiement IN ('MENSUEL', 'EQUIPEMENT')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (matricule) REFERENCES etudiant(matricule) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX idx_etudiant_nom ON etudiant(nom);
CREATE INDEX idx_etudiant_idniv ON etudiant(idniv);
CREATE INDEX idx_paiement_matricule ON paiement(matricule);
CREATE INDEX idx_paiement_date ON paiement(date_paiement);
CREATE INDEX idx_paiement_mois_annee ON paiement(mois, annee);

-- Trigger pour mettre à jour updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_etudiant_updated_at BEFORE UPDATE ON etudiant
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_montant_updated_at BEFORE UPDATE ON montant
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

