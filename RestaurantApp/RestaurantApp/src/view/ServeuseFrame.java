package view;

import controller.CommandeController;
import model.Commande;
import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ServeuseFrame extends JFrame {

    private final Utilisateur utilisateur;

    public ServeuseFrame(Utilisateur u) {
        this.utilisateur = u;
        setTitle("Restaurant – Serveuse : " + u.getNomUtilisateur());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.BODY);

        // Onglet 1 : Passer commande
        tabs.addTab("+ Passer Commande", new PasserCommandePanel(utilisateur));

        // Onglet 2 : Commandes en cours
        tabs.addTab(" Commandes en cours", buildCommandesPanel(Commande.Statut.en_attente, Commande.Statut.en_traitement));

        // Onglet 3 : Commandes reçues (prêtes)
        tabs.addTab(" Commandes reçues",  buildCommandesPanel(Commande.Statut.pretee));

        // Onglet 4 : Factures
        tabs.addTab(" Factures", new FacturePanel());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.ROUGE);
        header.setBorder(new EmptyBorder(10,16,10,16));
        JLabel titre = new JLabel("  Restaurant – Espace Serveuse  |  " + utilisateur.getNomUtilisateur());
        titre.setFont(UIUtils.HEADER);
        titre.setForeground(Color.WHITE);
        header.add(titre, BorderLayout.WEST);
        JButton btnDeco = UIUtils.bouton("Déconnexion", new Color(127,29,29));
        header.add(btnDeco, BorderLayout.EAST);
        btnDeco.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(tabs,   BorderLayout.CENTER);
        setContentPane(root);
    }

    /** Panel générique listant des commandes selon statuts */
    private JPanel buildCommandesPanel(Commande.Statut... statuts) {
        CommandeController ctrl = new CommandeController();
        JPanel panel = new JPanel(new BorderLayout(8,8));
        panel.setBackground(UIUtils.FOND);
        panel.setBorder(new EmptyBorder(10,10,10,10));

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"#","Client","Date","Montant","Statut"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);
        table.setFont(UIUtils.BODY); table.setRowHeight(28);
        table.getTableHeader().setFont(UIUtils.HEADER);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        Runnable charger = () -> {
            try {
                model.setRowCount(0);
                for (Commande.Statut s : statuts) {
                    for (Commande c : ctrl.getCommandesByStatut(s)) {
                        model.addRow(new Object[]{
                            c.getIdCommande(), c.getNomClient(),
                            c.getDateCommande()!=null?c.getDateCommande().toString().replace("T"," "):"",
                            c.getMontantTotal(), c.getStatut()
                        });
                    }
                }
            } catch (SQLException ex) { UIUtils.erreur(panel, ex.getMessage()); }
        };
        charger.run();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(UIUtils.FOND);
        JButton btnRefresh = UIUtils.bouton(" Rafraîchir", UIUtils.GRIS);
        btnPanel.add(btnRefresh);
        btnRefresh.addActionListener(e -> charger.run());
        panel.add(btnPanel, BorderLayout.SOUTH);

        new Timer(12_000, e -> charger.run()).start();
        return panel;
    }
}
