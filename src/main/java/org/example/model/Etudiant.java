package org.example.model;

import java.time.LocalDate;
import java.time.Period;

public class Etudiant {
    private String matricule;
    private String anneeUniv;
    private String nom;
    private String sexe;
    private LocalDate datenais;
    private String institution;
    private String mail;
    private String idniv;
    
    // Constructeurs
    public Etudiant() {}
    
    public Etudiant(String matricule, String anneeUniv, String nom, String sexe, 
                   LocalDate datenais, String institution, String mail, String idniv) {
        this.matricule = matricule;
        this.anneeUniv = anneeUniv;
        this.nom = nom;
        this.sexe = sexe;
        this.datenais = datenais;
        this.institution = institution;
        this.mail = mail;
        this.idniv = idniv;
    }
    
    // Getters et Setters
    public String getMatricule() {
        return matricule;
    }
    
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }
    
    public String getAnneeUniv() {
        return anneeUniv;
    }
    
    public void setAnneeUniv(String anneeUniv) {
        this.anneeUniv = anneeUniv;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getSexe() {
        return sexe;
    }
    
    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
    
    public LocalDate getDatenais() {
        return datenais;
    }
    
    public void setDatenais(LocalDate datenais) {
        this.datenais = datenais;
    }
    
    public String getInstitution() {
        return institution;
    }
    
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    public String getMail() {
        return mail;
    }
    
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public String getIdniv() {
        return idniv;
    }
    
    public void setIdniv(String idniv) {
        this.idniv = idniv;
    }
    
    // Méthode utilitaire pour calculer l'âge
    public int getAge() {
        if (datenais == null) return 0;
        return Period.between(datenais, LocalDate.now()).getYears();
    }
    
    // Méthode pour vérifier si l'étudiant est mineur
    public boolean isMineur() {
        return getAge() < 18;
    }
    
    @Override
    public String toString() {
        return "Etudiant{" +
                "matricule='" + matricule + '\'' +
                ", anneeUniv='" + anneeUniv + '\'' +
                ", nom='" + nom + '\'' +
                ", sexe='" + sexe + '\'' +
                ", datenais=" + datenais +
                ", institution='" + institution + '\'' +
                ", mail='" + mail + '\'' +
                ", idniv='" + idniv + '\'' +
                '}';
    }
}