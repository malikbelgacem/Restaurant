package view;

import controller.CommandeController;
import model.Commande;
import model.Commande.LigneCommande;
import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MesCommandesPanel extends JPanel {

    private final CommandeController ctrl;
    private final Utilisateur client;
    private DefaultTableModel model;
    private JTable table;

    public MesCommandesPanel(Utilisateur client) {
        this.client = client;
        this.ctrl   = new CommandeController();
        setLayout(new BorderLayout(10,10));
        setBackground(UIUtils.FOND);
        setBorder(new EmptyBorder(10,10,10,10));
        initUI();
        charger();
        new Timer(15_000, e -> charger()).start();
    }

    private void initUI() {
        add(UIUtils.panelTitre("Mes Commandes"), BorderLayout.NORTH);

        model = new DefaultTableModel(
            new String[]{"#","Date","Montant (DT)","Statut"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        table.setFont(UIUtils.BODY); table.setRowHeight(28);
        table.getTableHeader().setFont(UIUtils.HEADER);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,12,8));
        btnPanel.setBackground(UIUtils.FOND);
        JButton btnDetail  = UIUtils.bouton(" Voir Détail",  UIUtils.BLEU);
        JButton btnRefresh = UIUtils.bouton(" Rafraîchir", UIUtils.GRIS);
        btnPanel.add(btnDetail); btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        btnDetail.addActionListener(e -> voirDetail());
        btnRefresh.addActionListener(e -> charger());
    }

    public void charger() {
        try {
            List<Commande> cmds = ctrl.getCommandesByClient(client.getIdUtilisateur());
            model.setRowCount(0);
            for (Commande c : cmds)
                model.addRow(new Object[]{
                    c.getIdCommande(),
                    c.getDateCommande()!=null?c.getDateCommande().toString().replace("T"," "):"",
                    c.getMontantTotal(), c.getStatut()
                });
        } catch (SQLException ex) { UIUtils.erreur(this,"Chargement : "+ex.getMessage()); }
    }

    private void voirDetail() {
        int row = table.getSelectedRow();
        if (row < 0) { UIUtils.erreur(this,"Sélectionnez une commande."); return; }
        int id = (int) model.getValueAt(row,0);
        try {
            List<LigneCommande> lignes = ctrl.getLignes(id);
            StringBuilder sb = new StringBuilder("Détail de la commande #").append(id).append("\n\n");
            double total = 0;
            for (LigneCommande l : lignes) {
                double st = l.getPrix().doubleValue()*l.getQuantite();
                sb.append(String.format("  %-25s x%d = %.2f DT\n", l.getNomPlat(), l.getQuantite(), st));
                total += st;
            }
            sb.append(String.format("\n  TOTAL : %.2f DT", total));
            JOptionPane.showMessageDialog(this, sb.toString(), "Détail commande #"+id, JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
    }
}
