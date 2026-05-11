package view;

import controller.MenuController;
import model.Menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MenuPanel extends JPanel {

    private final MenuController controller = new MenuController();
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField tfNom;
    private JTextArea  taDesc;

    public MenuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIUtils.FOND);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initUI();
        charger();
    }

    private void initUI() {
        add(UIUtils.panelTitre("Gestion des Menus"), BorderLayout.NORTH);
        tableModel = new DefaultTableModel(new String[]{"ID","Nom","Description"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(UIUtils.BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(UIUtils.HEADER);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 280));
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.BLANC);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229,231,235)),
            new EmptyBorder(14,14,14,14)));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.fill   = GridBagConstraints.HORIZONTAL;

        g.gridy=0; g.gridx=0; g.weightx=0;
        form.add(new JLabel("Nom :"), g);
        g.gridx=1; g.weightx=1;
        tfNom = UIUtils.champ(20);
        form.add(tfNom, g);

        g.gridy=1; g.gridx=0; g.weightx=0;
        form.add(new JLabel("Description :"), g);
        g.gridx=1; g.weightx=1;
        taDesc = new JTextArea(3,20);
        taDesc.setFont(UIUtils.BODY);
        taDesc.setLineWrap(true);
        form.add(new JScrollPane(taDesc), g);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(UIUtils.FOND);

        JButton btnAjouter    = UIUtils.bouton("Ajouter",     UIUtils.VERT);
        JButton btnModifier   = UIUtils.bouton("Modifier",    UIUtils.BLEU);
        JButton btnSupprimer  = UIUtils.bouton("Supprimer",   UIUtils.ROUGE);
        JButton btnEnregistrer= UIUtils.bouton("Enregistrer", UIUtils.ORANGE);
        JButton btnFermer     = UIUtils.bouton("Fermer",      UIUtils.GRIS);

        btnPanel.add(btnAjouter);
        btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer);
        btnPanel.add(btnEnregistrer);
        btnPanel.add(btnFermer);

        JPanel centre = new JPanel(new BorderLayout(8,8));
        centre.setBackground(UIUtils.FOND);
        centre.add(scroll, BorderLayout.CENTER);
        centre.add(form,   BorderLayout.SOUTH);
        add(centre,   BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                tfNom.setText(tableModel.getValueAt(row,1).toString());
                Object desc = tableModel.getValueAt(row,2);
                taDesc.setText(desc != null ? desc.toString() : "");
            }
        });

        btnAjouter.addActionListener(e -> {
            table.clearSelection();
            tfNom.setText(""); taDesc.setText(""); tfNom.requestFocus();
        });

        btnEnregistrer.addActionListener(e -> enregistrer());

        btnModifier.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { UIUtils.erreur(this,"Sélectionnez un menu à modifier."); return; }
            enregistrer();
        });

        btnSupprimer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { UIUtils.erreur(this,"Sélectionnez un menu à supprimer."); return; }
            if (!UIUtils.confirmer(this,"Supprimer ce menu ?")) return;
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                controller.supprimer(id);
                charger();
                UIUtils.succes(this,"Menu supprimé.");
            } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
        });

        btnFermer.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null) w.dispose();
        });
    }

    private void enregistrer() {
        String nom  = tfNom.getText().trim();
        String desc = taDesc.getText().trim();
        if (nom.isEmpty()) { UIUtils.erreur(this,"Le nom est obligatoire."); return; }
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                // Ajout
                controller.ajouter(nom, desc);
                UIUtils.succes(this,"Menu ajouté !");
            } else {
                // Modification
                int id = (int) tableModel.getValueAt(row, 0);
                Menu m = new Menu(id, nom, desc);
                controller.modifier(m);
                UIUtils.succes(this,"Menu modifié !");
            }
            charger();
            tfNom.setText(""); taDesc.setText("");
            table.clearSelection();
        } catch (Exception ex) { UIUtils.erreur(this, ex.getMessage()); }
    }

    public void charger() {
        try {
            List<Menu> menus = controller.getTousLesMenus();
            tableModel.setRowCount(0);
            for (Menu m : menus)
                tableModel.addRow(new Object[]{m.getIdMenu(), m.getNom(), m.getDescription()});
        } catch (SQLException ex) { UIUtils.erreur(this, "Chargement : " + ex.getMessage()); }
    }
}
