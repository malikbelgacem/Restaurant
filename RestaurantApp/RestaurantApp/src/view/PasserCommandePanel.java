package view;

import controller.CommandeController;
import controller.MenuController;
import controller.PlatController;
import model.Commande;
import model.Commande.LigneCommande;
import model.Menu;
import model.Plat;
import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PasserCommandePanel extends JPanel {

    private final CommandeController cmdCtrl  = new CommandeController();
    private final PlatController     platCtrl = new PlatController();
    private final MenuController     menuCtrl = new MenuController();
    private final Utilisateur        client;

    private JComboBox<Menu> cbMenu;
    private DefaultTableModel modelMenu, modelPanier;
    private JTable tblMenu, tblPanier;
    private JLabel lblTotal;
    private final List<LigneCommande> panier = new ArrayList<>();

    public PasserCommandePanel(Utilisateur client) {
        this.client = client;
        setLayout(new BorderLayout(10,10));
        setBackground(UIUtils.FOND);
        setBorder(new EmptyBorder(10,10,10,10));
        initUI();
        chargerMenus();
    }

    private void initUI() {
        add(UIUtils.panelTitre("Passer une Commande"), BorderLayout.NORTH);
        JPanel gauche = new JPanel(new BorderLayout(6,6));
        gauche.setBackground(UIUtils.FOND);

        JPanel topGauche = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topGauche.setBackground(UIUtils.FOND);
        topGauche.add(new JLabel("Menu :"));
        cbMenu = new JComboBox<>();
        cbMenu.setFont(UIUtils.BODY);
        cbMenu.setPreferredSize(new Dimension(200, 30));
        topGauche.add(cbMenu);
        JButton btnFiltrer = UIUtils.bouton("Voir plats", UIUtils.BLEU);
        topGauche.add(btnFiltrer);
        gauche.add(topGauche, BorderLayout.NORTH);

        modelMenu = new DefaultTableModel(new String[]{"ID","Plat","Prix (DT)","Description"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        tblMenu = new JTable(modelMenu);
        tblMenu.setFont(UIUtils.BODY); tblMenu.setRowHeight(28);
        tblMenu.getTableHeader().setFont(UIUtils.HEADER);
        tblMenu.getColumnModel().getColumn(0).setMaxWidth(40);
        tblMenu.getColumnModel().getColumn(2).setMaxWidth(80);
        gauche.add(new JScrollPane(tblMenu), BorderLayout.CENTER);

        JPanel btnAjout = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAjout.setBackground(UIUtils.FOND);
        JSpinner spinQte = new JSpinner(new SpinnerNumberModel(1,1,99,1));
        btnAjout.add(new JLabel("Qté :"));
        btnAjout.add(spinQte);
        JButton btnAdd = UIUtils.bouton("➕ Ajouter au panier", UIUtils.VERT);
        btnAjout.add(btnAdd);
        gauche.add(btnAjout, BorderLayout.SOUTH);


        
        
        
        JPanel droite = new JPanel(new BorderLayout(6,6));
        droite.setBackground(UIUtils.FOND);
        JLabel lblPanier = new JLabel("  Panier");
        lblPanier.setFont(UIUtils.HEADER);
        droite.add(lblPanier, BorderLayout.NORTH);

        modelPanier = new DefaultTableModel(new String[]{"Plat","Qté","S/Total (DT)"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        tblPanier = new JTable(modelPanier);
        tblPanier.setFont(UIUtils.BODY); tblPanier.setRowHeight(28);
        tblPanier.getTableHeader().setFont(UIUtils.HEADER);
        droite.add(new JScrollPane(tblPanier), BorderLayout.CENTER);

        JPanel panierSud = new JPanel(new GridLayout(3,1,0,4));
        panierSud.setBackground(UIUtils.FOND);
        lblTotal = new JLabel("Total : 0.00 DT", SwingConstants.RIGHT);
        lblTotal.setFont(UIUtils.HEADER);
        JButton btnRetirer  = UIUtils.bouton("- Retirer ligne", UIUtils.ORANGE);
        JButton btnCommander= UIUtils.bouton("+ Passer la commande", UIUtils.ROUGE);
        btnCommander.setPreferredSize(new Dimension(0,40));
        panierSud.add(lblTotal);
        panierSud.add(btnRetirer);
        panierSud.add(btnCommander);
        droite.add(panierSud, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gauche, droite);
        split.setDividerLocation(480);
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);


        btnFiltrer.addActionListener(e -> chargerPlats());

        btnAdd.addActionListener(e -> {
            int row = tblMenu.getSelectedRow();
            if (row < 0) { UIUtils.erreur(this,"Sélectionnez un plat."); return; }
            int idPlat  = (int) modelMenu.getValueAt(row,0);
            String nomP = (String) modelMenu.getValueAt(row,1);
            BigDecimal prix = (BigDecimal) modelMenu.getValueAt(row,2);
            int qte = (int) spinQte.getValue();
            for (LigneCommande l : panier) {
                if (l.getIdPlat() == idPlat) { l.setQuantite(l.getQuantite()+qte); majPanier(); return; }
            }
            LigneCommande l = new LigneCommande(0, idPlat, nomP, qte, prix);
            panier.add(l);
            majPanier();
        });

        btnRetirer.addActionListener(e -> {
            int row = tblPanier.getSelectedRow();
            if (row < 0) { UIUtils.erreur(this,"Sélectionnez une ligne du panier."); return; }
            panier.remove(row);
            majPanier();
        });

        btnCommander.addActionListener(e -> passerCommande());
    }

    private void chargerMenus() {
        try {
            List<Menu> menus = menuCtrl.getTousLesMenus();
            cbMenu.removeAllItems();
            for (Menu m : menus) cbMenu.addItem(m);
            chargerPlats();
        } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
    }

    private void chargerPlats() {
        try {
            Menu m = (Menu) cbMenu.getSelectedItem();
            List<Plat> plats = (m != null)
                ? platCtrl.getPlatsByMenu(m.getIdMenu())
                : platCtrl.getTousLesPlats();
            modelMenu.setRowCount(0);
            for (Plat p : plats)
                modelMenu.addRow(new Object[]{p.getIdPlat(), p.getNom(), p.getPrix(), p.getDescription()});
        } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
    }

    private void majPanier() {
        modelPanier.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (LigneCommande l : panier) {
            BigDecimal st = l.getPrix().multiply(BigDecimal.valueOf(l.getQuantite()));
            modelPanier.addRow(new Object[]{l.getNomPlat(), l.getQuantite(), st});
            total = total.add(st);
        }
        lblTotal.setText("Total : " + total.setScale(2, java.math.RoundingMode.HALF_UP) + " DT");
    }

    private void passerCommande() {
        if (panier.isEmpty()) { UIUtils.erreur(this,"Le panier est vide."); return; }
        Commande c = new Commande();
        c.setIdClient(client.getIdUtilisateur());
        c.setLignes(new ArrayList<>(panier));
        c.calculerTotal();
        try {
            cmdCtrl.passerCommande(c);
            UIUtils.succes(this, "Commande #" + c.getIdCommande() + " passée avec succès !\nTotal : " + c.getMontantTotal() + " DT");
            panier.clear();
            majPanier();
        } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
    }
}
