package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Commande {
    public enum Statut { en_attente, en_traitement, pretee, annulee, terminee }

    private int           idCommande;
    private int           idClient;
    private String        nomClient;   
    private LocalDateTime dateCommande;
    private Statut        statut;
    private BigDecimal    montantTotal;

    private List<LigneCommande> lignes = new ArrayList<>();

    public Commande() {}

    public int           getIdCommande()   { return idCommande; }
    public int           getIdClient()     { return idClient; }
    public String        getNomClient()    { return nomClient; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public Statut        getStatut()       { return statut; }
    public BigDecimal    getMontantTotal() { return montantTotal; }
    public List<LigneCommande> getLignes() { return lignes; }

    public void setIdCommande(int id)              { this.idCommande   = id; }
    public void setIdClient(int id)                { this.idClient     = id; }
    public void setNomClient(String nom)           { this.nomClient    = nom; }
    public void setDateCommande(LocalDateTime d)   { this.dateCommande = d; }
    public void setStatut(Statut s)                { this.statut       = s; }
    public void setMontantTotal(BigDecimal m)      { this.montantTotal = m; }
    public void setLignes(List<LigneCommande> l)   { this.lignes       = l; }

   
    public void calculerTotal() {
        montantTotal = lignes.stream()
            .map(l -> l.getPrix().multiply(BigDecimal.valueOf(l.getQuantite())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        return "Commande #" + idCommande + " [" + statut + "] " + montantTotal + " DT";
    }

    
    public static class LigneCommande {
        private int        idCommande;
        private int        idPlat;
        private String     nomPlat;
        private int        quantite;
        private BigDecimal prix;

        public LigneCommande() {}
        public LigneCommande(int idCommande, int idPlat, String nomPlat,
                             int quantite, BigDecimal prix) {
            this.idCommande = idCommande;
            this.idPlat     = idPlat;
            this.nomPlat    = nomPlat;
            this.quantite   = quantite;
            this.prix       = prix;
        }

        public int        getIdCommande() { return idCommande; }
        public int        getIdPlat()     { return idPlat; }
        public String     getNomPlat()    { return nomPlat; }
        public int        getQuantite()   { return quantite; }
        public BigDecimal getPrix()       { return prix; }

        public void setIdCommande(int v) { this.idCommande = v; }
        public void setIdPlat(int v)     { this.idPlat = v; }
        public void setNomPlat(String v) { this.nomPlat = v; }
        public void setQuantite(int v)   { this.quantite = v; }
        public void setPrix(BigDecimal v){ this.prix = v; }
    }
}
