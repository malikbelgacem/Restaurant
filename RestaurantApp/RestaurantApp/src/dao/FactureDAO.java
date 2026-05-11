package dao;

import model.Facture;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {

    public boolean generer(Facture f) throws SQLException {
        String sql = "INSERT INTO facture(idCommande,montantTotal) VALUES(?,?)";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, f.getIdCommande());
            ps.setBigDecimal(2, f.getMontantTotal());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) f.setIdFacture(keys.getInt(1));
            }
            return rows > 0;
        }
    }

    public Facture findByCommande(int idCommande) throws SQLException {
        String sql = "SELECT * FROM facture WHERE idCommande=? ORDER BY idFacture DESC LIMIT 1";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public List<Facture> findAll() throws SQLException {
        List<Facture> list = new ArrayList<>();
        String sql = "SELECT * FROM facture ORDER BY dateFacture DESC";
        try (Connection con = DBConnection.getInstance();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    private Facture map(ResultSet rs) throws SQLException {
        Facture f = new Facture();
        f.setIdFacture(rs.getInt("idFacture"));
        f.setIdCommande(rs.getInt("idCommande"));
        Timestamp ts = rs.getTimestamp("dateFacture");
        if (ts != null) f.setDateFacture(ts.toLocalDateTime());
        f.setMontantTotal(rs.getBigDecimal("montantTotal"));
        return f;
    }
}
