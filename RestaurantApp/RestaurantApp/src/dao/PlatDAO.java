package dao;

import model.Plat;
import util.DBConnection;

import java.math.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatDAO {

    public List<Plat> findAll() throws SQLException {
        List<Plat> list = new ArrayList<>();
        String sql = "SELECT p.*, m.nom AS nomMenu FROM plat p LEFT JOIN menu m ON p.idMenu=m.idMenu ORDER BY p.nom";
        try (Connection con = DBConnection.getInstance();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Plat> findByMenu(int idMenu) throws SQLException {
        List<Plat> list = new ArrayList<>();
        String sql = "SELECT p.*, m.nom AS nomMenu FROM plat p LEFT JOIN menu m ON p.idMenu=m.idMenu WHERE p.idMenu=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMenu);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Plat findById(int id) throws SQLException {
        String sql = "SELECT p.*, m.nom AS nomMenu FROM plat p LEFT JOIN menu m ON p.idMenu=m.idMenu WHERE p.idPlat=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public boolean ajouter(Plat p) throws SQLException {
        String sql = "INSERT INTO plat(nom,description,prix,idMenu,imagePlat) VALUES(?,?,?,?,?)";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrix());
            if (p.getIdMenu() == 0) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, p.getIdMenu());
            if (p.getImagePlat() != null) ps.setBytes(5, p.getImagePlat());
            else ps.setNull(5, Types.BLOB);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) p.setIdPlat(keys.getInt(1));
            }
            return rows > 0;
        }
    }

    public boolean modifier(Plat p) throws SQLException {
        String sql = "UPDATE plat SET nom=?,description=?,prix=?,idMenu=?,imagePlat=? WHERE idPlat=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrix());
            if (p.getIdMenu() == 0) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, p.getIdMenu());
            if (p.getImagePlat() != null) ps.setBytes(5, p.getImagePlat());
            else ps.setNull(5, Types.BLOB);
            ps.setInt(6, p.getIdPlat());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean supprimer(int id) throws SQLException {
        String sql = "DELETE FROM plat WHERE idPlat=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Plat map(ResultSet rs) throws SQLException {
        Plat p = new Plat();
        p.setIdPlat(rs.getInt("idPlat"));
        p.setNom(rs.getString("nom"));
        p.setDescription(rs.getString("description"));
        p.setPrix(rs.getBigDecimal("prix"));
        p.setIdMenu(rs.getInt("idMenu"));
        p.setNomMenu(rs.getString("nomMenu"));
        p.setImagePlat(rs.getBytes("imagePlat"));
        return p;
    }
}
