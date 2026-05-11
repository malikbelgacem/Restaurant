package model;

import java.math.BigDecimal;

public class Plat {
    private int        idPlat;
    private String     nom;
    private String     description;
    private BigDecimal prix;
    private int        idMenu;
    private String     nomMenu;   
    private byte[]     imagePlat;

    public Plat() {}

    public Plat(int idPlat, String nom, String description, BigDecimal prix, int idMenu) {
        this.idPlat      = idPlat;
        this.nom         = nom;
        this.description = description;
        this.prix        = prix;
        this.idMenu      = idMenu;
    }

    public int        getIdPlat()      { return idPlat; }
    public String     getNom()         { return nom; }
    public String     getDescription() { return description; }
    public BigDecimal getPrix()        { return prix; }
    public int        getIdMenu()      { return idMenu; }
    public String     getNomMenu()     { return nomMenu; }
    public byte[]     getImagePlat()   { return imagePlat; }

    public void setIdPlat(int id)           { this.idPlat      = id; }
    public void setNom(String nom)          { this.nom         = nom; }
    public void setDescription(String desc) { this.description = desc; }
    public void setPrix(BigDecimal prix)    { this.prix        = prix; }
    public void setIdMenu(int idMenu)       { this.idMenu      = idMenu; }
    public void setNomMenu(String nomMenu)  { this.nomMenu     = nomMenu; }
    public void setImagePlat(byte[] img)    { this.imagePlat   = img; }

    @Override public String toString() { return nom + " - " + prix + " DT"; }
}
