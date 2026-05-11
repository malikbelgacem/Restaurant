package controller;

import dao.UtilisateurDAO;
import model.Utilisateur;

import java.sql.SQLException;

public class AuthController {
    private final UtilisateurDAO dao = new UtilisateurDAO();
    private Utilisateur utilisateurConnecte;

    public Utilisateur seConnecter(String nom, String mdp) throws SQLException {
        utilisateurConnecte = dao.authentifier(nom, mdp);
        return utilisateurConnecte;
    }

    public boolean inscrire(String nom, String mdp, String mdpConfirm,
                            Utilisateur.Role role) throws SQLException {
        if (nom == null || nom.isBlank())
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire.");
        if (nom.length() < 3)
            throw new IllegalArgumentException("Le nom doit contenir au moins 3 caractères.");
        if (mdp == null || mdp.length() < 4)
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caractères.");
        if (!mdp.equals(mdpConfirm))
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        if (dao.nomExiste(nom.trim()))
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");

        Utilisateur u = new Utilisateur(0, nom.trim(), mdp, role);
        return dao.ajouter(u);
    }

    public Utilisateur getUtilisateurConnecte() { return utilisateurConnecte; }

    public void seDeconnecter() { utilisateurConnecte = null; }
}