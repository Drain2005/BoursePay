package org.example.model;

import java.time.LocalDateTime;

public class Payer {
    private String idpaye;
    private String matricule;
    private String anneeUniv;
    private LocalDateTime date;
    private int nbrMois;
    
    // Constructeurs
    public Payer() {}
    
    public Payer(String idpaye, String matricule, String anneeUniv, LocalDateTime date, int nbrMois) {
        this.idpaye = idpaye;
        this.matricule = matricule;
        this.anneeUniv = anneeUniv;
        this.date = date;
        this.nbrMois = nbrMois;
    }
    
    // Getters et Setters
    public String getIdpaye() {
        return idpaye;
    }
    
    public void setIdpaye(String idpaye) {
        this.idpaye = idpaye;
    }
    
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
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public int getNbrMois() {
        return nbrMois;
    }
    
    public void setNbrMois(int nbrMois) {
        this.nbrMois = nbrMois;
    }
    
    @Override
    public String toString() {
        return "Payer{" +
                "idpaye='" + idpaye + '\'' +
                ", matricule='" + matricule + '\'' +
                ", anneeUniv='" + anneeUniv + '\'' +
                ", date=" + date +
                ", nbrMois=" + nbrMois +
                '}';
    }
}