package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Facture {
    private int           idFacture;
    private int           idCommande;
    private LocalDateTime dateFacture;
    private BigDecimal    montantTotal;

    public Facture() {}

    public Facture(int idFacture, int idCommande, LocalDateTime dateFacture, BigDecimal montantTotal) {
        this.idFacture    = idFacture;
        this.idCommande   = idCommande;
        this.dateFacture  = dateFacture;
        this.montantTotal = montantTotal;
    }

    public int           getIdFacture()    { return idFacture; }
    public int           getIdCommande()   { return idCommande; }
    public LocalDateTime getDateFacture()  { return dateFacture; }
    public BigDecimal    getMontantTotal() { return montantTotal; }

    public void setIdFacture(int v)             { this.idFacture    = v; }
    public void setIdCommande(int v)            { this.idCommande   = v; }
    public void setDateFacture(LocalDateTime v) { this.dateFacture  = v; }
    public void setMontantTotal(BigDecimal v)   { this.montantTotal = v; }

    @Override public String toString() {
        return "Facture #" + idFacture + " / Commande #" + idCommande + " = " + montantTotal + " DT";
    }
}
