package dao;

import model.Commande;
import model.Commande.LigneCommande;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {

        public boolean creerCommande(Commande c) throws SQLException {
        Connection con = DBConnection.getInstance();
        con.setAutoCommit(false);
        try {
            
            String sql1 = "INSERT INTO commande(idClient,statut,montantTotal) VALUES(?,?,?)";
            PreparedStatement ps1 = con.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, c.getIdClient());
            ps1.setString(2, Commande.Statut.en_attente.name());
            ps1.setBigDecimal(3, c.getMontantTotal());
            ps1.executeUpdate();
            ResultSet keys = ps1.getGeneratedKeys();
            if (!keys.next()) { con.rollback(); return false; }
            int newId = keys.getInt(1);
            c.setIdCommande(newId);

            
            String sql2 = "INSERT INTO commandeplat(idCommande,idPlat,quantite) VALUES(?,?,?)";
            PreparedStatement ps2 = con.prepareStatement(sql2);
            for (LigneCommande l : c.getLignes()) {
                ps2.setInt(1, newId);
                ps2.setInt(2, l.getIdPlat());
                ps2.setInt(3, l.getQuantite());
                ps2.addBatch();
            }
            ps2.executeBatch();
            con.commit();
            return true;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    public List<Commande> findAll() throws SQLException {
        return findByStatut(null);
    }

    public List<Commande> findByStatut(Commande.Statut statut) throws SQLException {
        List<Commande> list = new ArrayList<>();
        String sql = "SELECT c.*, u.nomUtilisateur FROM commande c " +
                     "JOIN utilisateur u ON c.idClient=u.idUtilisateur" +
                     (statut != null ? " WHERE c.statut=?" : "") +
                     " ORDER BY c.dateCommande DESC";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (statut != null) ps.setString(1, statut.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapCommande(rs));
        }
        return list;
    }

    public List<Commande> findByClient(int idClient) throws SQLException {
        List<Commande> list = new ArrayList<>();
        String sql = "SELECT c.*, u.nomUtilisateur FROM commande c " +
                     "JOIN utilisateur u ON c.idClient=u.idUtilisateur " +
                     "WHERE c.idClient=? ORDER BY c.dateCommande DESC";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idClient);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapCommande(rs));
        }
        return list;
    }

 
    public List<LigneCommande> findLignes(int idCommande) throws SQLException {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT cp.*, p.nom AS nomPlat, p.prix FROM commandeplat cp " +
                     "JOIN plat p ON cp.idPlat=p.idPlat WHERE cp.idCommande=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LigneCommande l = new LigneCommande();
                l.setIdCommande(rs.getInt("idCommande"));
                l.setIdPlat(rs.getInt("idPlat"));
                l.setNomPlat(rs.getString("nomPlat"));
                l.setQuantite(rs.getInt("quantite"));
                l.setPrix(rs.getBigDecimal("prix"));
                lignes.add(l);
            }
        }
        return lignes;
    }

    public boolean changerStatut(int idCommande, Commande.Statut statut) throws SQLException {
        String sql = "UPDATE commande SET statut=? WHERE idCommande=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setInt(2, idCommande);
            return ps.executeUpdate() > 0;
        }
    }

    private Commande mapCommande(ResultSet rs) throws SQLException {
        Commande c = new Commande();
        c.setIdCommande(rs.getInt("idCommande"));
        c.setIdClient(rs.getInt("idClient"));
        c.setNomClient(rs.getString("nomUtilisateur"));
        Timestamp ts = rs.getTimestamp("dateCommande");
        if (ts != null) c.setDateCommande(ts.toLocalDateTime());
        c.setStatut(Commande.Statut.valueOf(rs.getString("statut")));
        c.setMontantTotal(rs.getBigDecimal("montantTotal"));
        return c;
    }
}
