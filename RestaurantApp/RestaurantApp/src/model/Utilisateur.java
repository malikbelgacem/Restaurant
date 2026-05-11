package model;

public class Utilisateur {
    public enum Role { client, serveuse, cuisinier }

    private int    idUtilisateur;
    private String nomUtilisateur;
    private String motDePasse;
    private Role   role;

    public Utilisateur() {}

    public Utilisateur(int id, String nom, String mdp, Role role) {
        this.idUtilisateur  = id;
        this.nomUtilisateur = nom;
        this.motDePasse     = mdp;
        this.role           = role;
    }

    public int    getIdUtilisateur()  { return idUtilisateur; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public String getMotDePasse()     { return motDePasse; }
    public Role   getRole()           { return role; }

    public void setIdUtilisateur(int id)      { this.idUtilisateur  = id; }
    public void setNomUtilisateur(String nom) { this.nomUtilisateur = nom; }
    public void setMotDePasse(String mdp)     { this.motDePasse     = mdp; }
    public void setRole(Role role)            { this.role           = role; }

    @Override public String toString() { return nomUtilisateur + " (" + role + ")"; }
}
