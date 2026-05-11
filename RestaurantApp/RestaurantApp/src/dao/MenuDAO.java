package dao;

import model.Menu;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

    public List<Menu> findAll() throws SQLException {
        List<Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM menu ORDER BY nom";
        try (Connection con = DBConnection.getInstance();
             Statement  st  = con.createStatement();
             ResultSet  rs  = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public Menu findById(int id) throws SQLException {
        String sql = "SELECT * FROM menu WHERE idMenu=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        }
        return null;
    }

    public boolean ajouter(Menu m) throws SQLException {
        String sql = "INSERT INTO menu(nom,description) VALUES(?,?)";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) m.setIdMenu(keys.getInt(1));
            }
            return rows > 0;
        }
    }

    public boolean modifier(Menu m) throws SQLException {
        String sql = "UPDATE menu SET nom=?,description=? WHERE idMenu=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getDescription());
            ps.setInt(3, m.getIdMenu());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean supprimer(int id) throws SQLException {
        String sql = "DELETE FROM menu WHERE idMenu=?";
        try (Connection con = DBConnection.getInstance();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Menu map(ResultSet rs) throws SQLException {
        return new Menu(rs.getInt("idMenu"), rs.getString("nom"), rs.getString("description"));
    }
}
