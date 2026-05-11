package controller;

import dao.CommandeDAO;
import dao.FactureDAO;
import model.Commande;
import model.Commande.LigneCommande;
import model.Facture;

import java.sql.SQLException;
import java.util.List;

public class CommandeController {
    private final CommandeDAO commandeDAO = new CommandeDAO();
    private final FactureDAO  factureDAO  = new FactureDAO();

    public boolean passerCommande(Commande c) throws SQLException {
        c.calculerTotal();
        return commandeDAO.creerCommande(c);
    }

    public List<Commande> getToutesCommandes() throws SQLException {
        return commandeDAO.findAll();
    }

    public List<Commande> getCommandesByStatut(Commande.Statut statut) throws SQLException {
        return commandeDAO.findByStatut(statut);
    }

    public List<Commande> getCommandesByClient(int idClient) throws SQLException {
        return commandeDAO.findByClient(idClient);
    }

    public List<LigneCommande> getLignes(int idCommande) throws SQLException {
        return commandeDAO.findLignes(idCommande);
    }

    public boolean commencerTraitement(int idCommande) throws SQLException {
        return commandeDAO.changerStatut(idCommande, Commande.Statut.en_traitement);
    }

    public boolean marquerPrete(int idCommande) throws SQLException {
        return commandeDAO.changerStatut(idCommande, Commande.Statut.pretee);
    }

    public boolean marquerServie(int idCommande) throws SQLException {
        return commandeDAO.changerStatut(idCommande, Commande.Statut.terminee);
    }

    public boolean annuler(int idCommande) throws SQLException {
        return commandeDAO.changerStatut(idCommande, Commande.Statut.annulee);
    }


    public Facture genererFacture(Commande c) throws SQLException {
        Facture f = new Facture();
        f.setIdCommande(c.getIdCommande());
        f.setMontantTotal(c.getMontantTotal());
        factureDAO.generer(f);
        return f;
    }

    public Facture getFactureByCommande(int idCommande) throws SQLException {
        return factureDAO.findByCommande(idCommande);
    }
}
