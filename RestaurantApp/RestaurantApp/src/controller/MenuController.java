package controller;

import dao.MenuDAO;
import model.Menu;

import java.sql.SQLException;
import java.util.List;

public class MenuController {
    private final MenuDAO dao = new MenuDAO();

    public List<Menu> getTousLesMenus() throws SQLException {
        return dao.findAll();
    }

    public boolean ajouter(String nom, String description) throws SQLException {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Le nom est obligatoire.");
        Menu m = new Menu(0, nom.trim(), description);
        return dao.ajouter(m);
    }

    public boolean modifier(Menu m) throws SQLException {
        if (m.getNom() == null || m.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        return dao.modifier(m);
    }

    public boolean supprimer(int id) throws SQLException {
        return dao.supprimer(id);
    }
}
