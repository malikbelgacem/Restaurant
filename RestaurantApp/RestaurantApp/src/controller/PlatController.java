package controller;

import dao.PlatDAO;
import model.Plat;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PlatController {
    private final PlatDAO dao = new PlatDAO();

    public List<Plat> getTousLesPlats() throws SQLException {
        return dao.findAll();
    }

    public List<Plat> getPlatsByMenu(int idMenu) throws SQLException {
        return dao.findByMenu(idMenu);
    }

    public boolean ajouter(String nom, String desc, String prixStr, int idMenu, byte[] image)
            throws SQLException {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("Nom obligatoire.");
        BigDecimal prix;
        try { prix = new BigDecimal(prixStr.replace(",",".")); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Prix invalide."); }
        if (prix.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Prix doit être positif.");
        Plat p = new Plat(0, nom.trim(), desc, prix, idMenu);
        p.setImagePlat(image);
        return dao.ajouter(p);
    }

    public boolean modifier(Plat p) throws SQLException {
        return dao.modifier(p);
    }

    public boolean supprimer(int id) throws SQLException {
        return dao.supprimer(id);
    }
}
