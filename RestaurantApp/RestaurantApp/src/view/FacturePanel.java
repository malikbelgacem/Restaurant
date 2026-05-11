package view;

import controller.CommandeController;
import model.Commande;
import model.Commande.LigneCommande;
import model.Facture;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FacturePanel extends JPanel {

    private final CommandeController ctrl = new CommandeController();
    private DefaultTableModel modelCommandes;
    private JTable tblCommandes;

    public FacturePanel() {
        setLayout(new BorderLayout(10,10));
        setBackground(UIUtils.FOND);
        setBorder(new EmptyBorder(10,10,10,10));
        initUI();
        charger();
    }

    private void initUI() {
        add(UIUtils.panelTitre("Gestion des Factures"), BorderLayout.NORTH);

        modelCommandes = new DefaultTableModel(
            new String[]{"#Cmd","Client","Date","Montant","Statut"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        tblCommandes = new JTable(modelCommandes);
        tblCommandes.setFont(UIUtils.BODY);
        tblCommandes.setRowHeight(28);
        tblCommandes.getTableHeader().setFont(UIUtils.HEADER);
        tblCommandes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCommandes.getColumnModel().getColumn(0).setMaxWidth(55);

        add(new JScrollPane(tblCommandes), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,12,8));
        btnPanel.setBackground(UIUtils.FOND);

        JButton btnFacture  = UIUtils.bouton("🧾 Générer Facture", UIUtils.VERT);
        JButton btnVoir     = UIUtils.bouton("👁 Voir Facture",    UIUtils.BLEU);
        JButton btnServie   = UIUtils.bouton("✔ Marquer Servie",  UIUtils.ORANGE);
        JButton btnRefresh  = UIUtils.bouton("🔄 Rafraîchir",     UIUtils.GRIS);

        btnPanel.add(btnFacture); btnPanel.add(btnVoir);
        btnPanel.add(btnServie);  btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        btnFacture.addActionListener(e -> genererFacture());
        btnVoir.addActionListener(e -> voirFacture());
        btnServie.addActionListener(e -> marquerServie());
        btnRefresh.addActionListener(e -> charger());
    }

    public void charger() {
        try {
            // On affiche les commandes prêtes + terminées
            List<Commande> pretees  = ctrl.getCommandesByStatut(Commande.Statut.pretee);
            List<Commande> termines = ctrl.getCommandesByStatut(Commande.Statut.terminee);
            modelCommandes.setRowCount(0);
            for (Commande c : pretees)  ajouterLigne(c);
            for (Commande c : termines) ajouterLigne(c);
        } catch (SQLException ex) { UIUtils.erreur(this,"Chargement : "+ex.getMessage()); }
    }

    private void ajouterLigne(Commande c) {
        modelCommandes.addRow(new Object[]{
            c.getIdCommande(), c.getNomClient(),
            c.getDateCommande()!=null?c.getDateCommande().toString().replace("T"," "):"",
            c.getMontantTotal(), c.getStatut()
        });
    }

    private Commande commandeSelectionnee() throws SQLException {
        int row = tblCommandes.getSelectedRow();
        if (row < 0) { UIUtils.erreur(this,"Sélectionnez une commande."); return null; }
        int id = (int) modelCommandes.getValueAt(row,0);
        List<Commande> all = ctrl.getToutesCommandes();
        return all.stream().filter(c->c.getIdCommande()==id).findFirst().orElse(null);
    }

    private void genererFacture() {
        try {
            Commande c = commandeSelectionnee();
            if (c == null) return;
            Facture existante = ctrl.getFactureByCommande(c.getIdCommande());
            if (existante != null) {
                UIUtils.erreur(this,"Une facture existe déjà (#"+existante.getIdFacture()+").");
                return;
            }
            Facture f = ctrl.genererFacture(c);
            UIUtils.succes(this,"Facture #"+f.getIdFacture()+" générée !\nMontant : "+f.getMontantTotal()+" DT");
            charger();
        } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
    }

    private void voirFacture() {
        try {
            Commande c = commandeSelectionnee();
            if (c == null) return;
            List<LigneCommande> lignes = ctrl.getLignes(c.getIdCommande());
            Facture f = ctrl.getFactureByCommande(c.getIdCommande());

            StringBuilder sb = new StringBuilder();
            sb.append("╔══════════════════════════════════╗\n");
            sb.append("║          FACTURE RESTAURANT       ║\n");
            sb.append("╚══════════════════════════════════╝\n\n");
            sb.append("Commande # : ").append(c.getIdCommande()).append("\n");
            sb.append("Client     : ").append(c.getNomClient()).append("\n");
            sb.append("Date       : ").append(c.getDateCommande() != null ? c.getDateCommande().toString().replace("T"," ") : "").append("\n");
            sb.append("─────────────────────────────────\n");
            for (LigneCommande l : lignes) {
                double st = l.getPrix().doubleValue() * l.getQuantite();
                sb.append(String.format("  %-22s x%2d  %6.2f DT\n", l.getNomPlat(), l.getQuantite(), st));
            }
            sb.append("─────────────────────────────────\n");
            sb.append(String.format("  TOTAL                      %6.2f DT\n", c.getMontantTotal().doubleValue()));
            if (f != null) sb.append("\n  Facture # : ").append(f.getIdFacture());

            JTextArea ta = new JTextArea(sb.toString());
            ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
            ta.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(ta),
                "Facture – Commande #" + c.getIdCommande(), JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
    }

    private void marquerServie() {
        try {
            Commande c = commandeSelectionnee();
            if (c == null) return;
            ctrl.marquerServie(c.getIdCommande());
            charger();
            UIUtils.succes(this,"Commande marquée servie.");
        } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
    }
}
