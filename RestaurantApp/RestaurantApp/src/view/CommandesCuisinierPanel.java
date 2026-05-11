package view;

import controller.CommandeController;
import model.Commande;
import model.Commande.LigneCommande;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel cuisinier : voir commandes en attente / en traitement / prêtes.
 * Peut commencer / marquer prête / annuler une commande.
 */
public class CommandesCuisinierPanel extends JPanel {

    private final CommandeController ctrl = new CommandeController();
    private DefaultTableModel modelAttente, modelTraitement, modelPrete;
    private JTable tblAttente, tblTraitement, tblPrete;

    public CommandesCuisinierPanel() {
        setLayout(new BorderLayout(8,8));
        setBackground(UIUtils.FOND);
        setBorder(new EmptyBorder(10,10,10,10));
        initUI();
        charger();
        // Rafraîchir toutes les 10 s
        new Timer(10_000, e -> charger()).start();
    }

    private void initUI() {
        add(UIUtils.panelTitre("Gestion des Commandes – Cuisinier"), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.HEADER);

        tblAttente    = creerTable();
        tblTraitement = creerTable();
        tblPrete      = creerTable();
        modelAttente    = (DefaultTableModel) tblAttente.getModel();
        modelTraitement = (DefaultTableModel) tblTraitement.getModel();
        modelPrete      = (DefaultTableModel) tblPrete.getModel();

        tabs.addTab(" En attente",     panelOnglet(tblAttente));
        tabs.addTab(" En traitement",  panelOnglet(tblTraitement));
        tabs.addTab(" Prêtes",         panelOnglet(tblPrete));

        add(tabs, BorderLayout.CENTER);

        // Boutons action
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btnPanel.setBackground(UIUtils.FOND);

        JButton btnCommencer = UIUtils.bouton(" Commencer traitement", UIUtils.ORANGE);
        JButton btnPrete     = UIUtils.bouton(" Marquer Prête",        UIUtils.VERT);
        JButton btnAnnuler   = UIUtils.bouton(" Annuler",              UIUtils.ROUGE);
        JButton btnDetail    = UIUtils.bouton(" Détail commande",      UIUtils.BLEU);
        JButton btnRefresh   = UIUtils.bouton(" Rafraîchir",          UIUtils.GRIS);

        btnPanel.add(btnCommencer); btnPanel.add(btnPrete);
        btnPanel.add(btnAnnuler);   btnPanel.add(btnDetail); btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        btnCommencer.addActionListener(e -> {
            int id = idSelectionne(tblAttente);
            if (id < 0) return;
            try { ctrl.commencerTraitement(id); charger(); UIUtils.succes(this,"Traitement commencé."); }
            catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
        });

        btnPrete.addActionListener(e -> {
            int id = idSelectionne(tblTraitement);
            if (id < 0) return;
            try { ctrl.marquerPrete(id); charger(); UIUtils.succes(this,"Commande marquée Prête — serveuse notifiée !"); }
            catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
        });

        btnAnnuler.addActionListener(e -> {
            int id = idActifSelectionne(tabs, tblAttente, tblTraitement, tblPrete);
            if (id < 0) { UIUtils.erreur(this,"Sélectionnez une commande."); return; }
            if (!UIUtils.confirmer(this,"Annuler la commande #"+id+" ?")) return;
            try { ctrl.annuler(id); charger(); }
            catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
        });

        btnDetail.addActionListener(e -> {
            int id = idActifSelectionne(tabs, tblAttente, tblTraitement, tblPrete);
            if (id < 0) { UIUtils.erreur(this,"Sélectionnez une commande."); return; }
            afficherDetail(id);
        });

        btnRefresh.addActionListener(e -> charger());
    }

    private JScrollPane panelOnglet(JTable t) { return new JScrollPane(t); }

    private JTable creerTable() {
        DefaultTableModel m = new DefaultTableModel(
            new String[]{"#","Client","Date","Montant (DT)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(m);
        t.setFont(UIUtils.BODY);
        t.setRowHeight(28);
        t.getTableHeader().setFont(UIUtils.HEADER);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getColumnModel().getColumn(0).setMaxWidth(50);
        return t;
    }

    private void remplir(DefaultTableModel m, List<Commande> list) {
        m.setRowCount(0);
        for (Commande c : list)
            m.addRow(new Object[]{
                c.getIdCommande(), c.getNomClient(),
                c.getDateCommande() != null ? c.getDateCommande().toString().replace("T"," ") : "",
                c.getMontantTotal()
            });
    }

    public void charger() {
        try {
            remplir(modelAttente,    ctrl.getCommandesByStatut(Commande.Statut.en_attente));
            remplir(modelTraitement, ctrl.getCommandesByStatut(Commande.Statut.en_traitement));
            remplir(modelPrete,      ctrl.getCommandesByStatut(Commande.Statut.pretee));
        } catch (SQLException ex) { UIUtils.erreur(this,"Chargement : "+ex.getMessage()); }
    }

    private int idSelectionne(JTable t) {
        int row = t.getSelectedRow();
        if (row < 0) { UIUtils.erreur(this,"Sélectionnez une commande dans cet onglet."); return -1; }
        return (int) t.getModel().getValueAt(row,0);
    }

    private int idActifSelectionne(JTabbedPane tabs, JTable... tables) {
        int idx = tabs.getSelectedIndex();
        int row = tables[idx].getSelectedRow();
        if (row < 0) return -1;
        return (int) tables[idx].getModel().getValueAt(row,0);
    }

    private void afficherDetail(int idCommande) {
        try {
            List<LigneCommande> lignes = ctrl.getLignes(idCommande);
            StringBuilder sb = new StringBuilder("Commande #").append(idCommande).append("\n\n");
            double total = 0;
            for (LigneCommande l : lignes) {
                double st = l.getPrix().doubleValue() * l.getQuantite();
                sb.append(String.format("  %-25s x%d = %.2f DT\n", l.getNomPlat(), l.getQuantite(), st));
                total += st;
            }
            sb.append(String.format("\n  TOTAL : %.2f DT", total));
            JOptionPane.showMessageDialog(this, sb.toString(), "Détail Commande #"+idCommande, JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
    }
}
