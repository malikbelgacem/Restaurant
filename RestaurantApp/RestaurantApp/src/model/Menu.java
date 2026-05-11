package model;

public class Menu {
    private int    idMenu;
    private String nom;
    private String description;

    public Menu() {}

    public Menu(int idMenu, String nom, String description) {
        this.idMenu      = idMenu;
        this.nom         = nom;
        this.description = description;
    }

    public int    getIdMenu()      { return idMenu; }
    public String getNom()         { return nom; }
    public String getDescription() { return description; }

    public void setIdMenu(int id)           { this.idMenu      = id; }
    public void setNom(String nom)          { this.nom         = nom; }
    public void setDescription(String desc) { this.description = desc; }

    @Override public String toString() { return nom; }
}
