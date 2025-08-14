package org.example.model;

public class Montant {
    private String idniv;
    private String niveau;
    private int montant;
    
    // Constructeurs
    public Montant() {}
    
    public Montant(String idniv, String niveau, int montant) {
        this.idniv = idniv;
        this.niveau = niveau;
        this.montant = montant;
    }
    
    // Getters et Setters
    public String getIdniv() {
        return idniv;
    }
    
    public void setIdniv(String idniv) {
        this.idniv = idniv;
    }
    
    public String getNiveau() {
        return niveau;
    }
    
    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }
    
    public int getMontant() {
        return montant;
    }
    
    public void setMontant(int montant) {
        this.montant = montant;
    }
    
    @Override
    public String toString() {
        return "Montant{" +
                "idniv='" + idniv + '\'' +
                ", niveau='" + niveau + '\'' +
                ", montant=" + montant +
                '}';
    }
}