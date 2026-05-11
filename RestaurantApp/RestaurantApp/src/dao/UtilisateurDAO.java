package dao;

import model.Utilisateur;
import util.DBConnection;

import java.sql.*;

public class UtilisateurDAO {

    /** Authentification : retourne l'utilisateur ou null */
    public Utilisateur authentifier(String nom, String mdp) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE nomUtilisateur=? AND motDePasse=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nom);
            ps.setString(2, mdp);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    /** Ajouter un utilisateur – retourne l'ID généré, lève exception si username pris */
    public boolean ajouter(Utilisateur u) throws SQLException {
        String sql = "INSERT INTO utilisateur(nomUtilisateur,motDePasse,role) VALUES(?,?,?)";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNomUtilisateur());
            ps.setString(2, u.getMotDePasse());
            ps.setString(3, u.getRole().name());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) u.setIdUtilisateur(keys.getInt(1));
            }
            return rows > 0;
        }
    }

    /** Vérifie si un nom d'utilisateur est déjà pris */
    public boolean nomExiste(String nomUtilisateur) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE nomUtilisateur=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nomUtilisateur);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private Utilisateur map(ResultSet rs) throws SQLException {
        Utilisateur u = new Utilisateur();
        u.setIdUtilisateur(rs.getInt("idUtilisateur"));
        u.setNomUtilisateur(rs.getString("nomUtilisateur"));
        u.setMotDePasse(rs.getString("motDePasse"));
        u.setRole(Utilisateur.Role.valueOf(rs.getString("role")));
        return u;
    }
}