package view;

import controller.MenuController;
import controller.PlatController;
import model.Menu;
import model.Plat;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

public class PlatPanel extends JPanel {

    private final PlatController  platCtrl = new PlatController();
    private final MenuController  menuCtrl = new MenuController();

    private DefaultTableModel tableModel;
    private JTable   table;
    private JTextField  tfNom, tfPrix;
    private JTextArea   taDesc;
    private JComboBox<Menu> cbMenu;
    private JLabel      lblImage;
    private byte[]      imageCourante;

    public PlatPanel() {
        setLayout(new BorderLayout(10,10));
        setBackground(UIUtils.FOND);
        setBorder(new EmptyBorder(10,10,10,10));
        initUI();
        charger();
    }

    private void initUI() {
        add(UIUtils.panelTitre("Gestion des Plats"), BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(
            new String[]{"ID","Nom","Prix (DT)","Menu","Description"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(UIUtils.BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(UIUtils.HEADER);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(2).setMaxWidth(90);
        JScrollPane scroll = new JScrollPane(table);

        // Formulaire
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.BLANC);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229,231,235)),
            new EmptyBorder(12,14,12,14)));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,5,4,5);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Nom
        g.gridy=0; g.gridx=0; g.weightx=0; form.add(new JLabel("Nom :"), g);
        g.gridx=1; g.weightx=1; tfNom = UIUtils.champ(18); form.add(tfNom, g);
        // Prix
        g.gridy=0; g.gridx=2; g.weightx=0; form.add(new JLabel("Prix :"), g);
        g.gridx=3; g.weightx=0.5; tfPrix = UIUtils.champ(8); form.add(tfPrix, g);
        // Menu
        g.gridy=1; g.gridx=0; g.weightx=0; form.add(new JLabel("Menu :"), g);
        g.gridx=1; g.weightx=1; cbMenu = new JComboBox<>(); cbMenu.setFont(UIUtils.BODY);
        form.add(cbMenu, g);
        // Description
        g.gridy=1; g.gridx=2; form.add(new JLabel("Description :"), g);
        g.gridx=3; g.weightx=1;
        taDesc = new JTextArea(2,14); taDesc.setFont(UIUtils.BODY); taDesc.setLineWrap(true);
        form.add(new JScrollPane(taDesc), g);
        // Image
        g.gridy=2; g.gridx=0; form.add(new JLabel("Image :"), g);
        g.gridx=1;
        lblImage = new JLabel("Aucune image", SwingConstants.CENTER);
        lblImage.setPreferredSize(new Dimension(100,70));
        lblImage.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        form.add(lblImage, g);
        g.gridx=2; g.gridwidth=2;
        JButton btnImg = UIUtils.bouton("Choisir image", UIUtils.GRIS);
        form.add(btnImg, g); g.gridwidth=1;

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnPanel.setBackground(UIUtils.FOND);
        JButton btnAjouter   = UIUtils.bouton("Ajouter",     UIUtils.VERT);
        JButton btnModifier  = UIUtils.bouton("Modifier",    UIUtils.BLEU);
        JButton btnSupprimer = UIUtils.bouton("Supprimer",   UIUtils.ROUGE);
        JButton btnSave      = UIUtils.bouton("Enregistrer", UIUtils.ORANGE);
        JButton btnFermer    = UIUtils.bouton("Fermer",      UIUtils.GRIS);
        btnPanel.add(btnAjouter); btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer); btnPanel.add(btnSave); btnPanel.add(btnFermer);

        JPanel centre = new JPanel(new BorderLayout(8,8));
        centre.setBackground(UIUtils.FOND);
        centre.add(scroll, BorderLayout.CENTER);
        centre.add(form,   BorderLayout.SOUTH);
        add(centre,   BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Actions
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                try {
                    int id = (int) tableModel.getValueAt(row, 0);
                    Plat p = platCtrl.getPlatsByMenu(0).stream()  // on cherche dans tous
                        .filter(pl -> pl.getIdPlat()==id).findFirst()
                        .orElse(platCtrl.getTousLesPlats().stream()
                            .filter(pl->pl.getIdPlat()==id).findFirst().orElse(null));
                    if (p == null) return;
                    tfNom.setText(p.getNom());
                    tfPrix.setText(p.getPrix().toPlainString());
                    taDesc.setText(p.getDescription() != null ? p.getDescription() : "");
                    // sélectionner menu
                    for (int i=0; i<cbMenu.getItemCount(); i++)
                        if (cbMenu.getItemAt(i).getIdMenu() == p.getIdMenu()) { cbMenu.setSelectedIndex(i); break; }
                    imageCourante = p.getImagePlat();
                    afficherImage(imageCourante);
                } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
            }
        });

        btnImg.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    imageCourante = Files.readAllBytes(f.toPath());
                    afficherImage(imageCourante);
                } catch (IOException ex) { UIUtils.erreur(this,"Impossible de lire l'image."); }
            }
        });

        btnAjouter.addActionListener(e -> { viderForm(); imageCourante=null; lblImage.setIcon(null); lblImage.setText("Aucune image"); });
        btnSave.addActionListener(e -> enregistrer());
        btnModifier.addActionListener(e -> enregistrer());
        btnSupprimer.addActionListener(e -> supprimer());
        btnFermer.addActionListener(e -> { Window w=SwingUtilities.getWindowAncestor(this); if(w!=null)w.dispose(); });
    }

    private void afficherImage(byte[] data) {
        if (data == null) { lblImage.setIcon(null); lblImage.setText("Aucune image"); return; }
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
            if (img == null) return;
            Image scaled = img.getScaledInstance(100, 70, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaled));
            lblImage.setText("");
        } catch (IOException ignored) {}
    }

    private void enregistrer() {
        String nom  = tfNom.getText().trim();
        String prix = tfPrix.getText().trim();
        String desc = taDesc.getText().trim();
        Menu menu   = (Menu) cbMenu.getSelectedItem();
        int idMenu  = (menu != null) ? menu.getIdMenu() : 0;
        int row     = table.getSelectedRow();
        try {
            if (row == -1) {
                platCtrl.ajouter(nom, desc, prix, idMenu, imageCourante);
                UIUtils.succes(this,"Plat ajouté !");
            } else {
                int id = (int) tableModel.getValueAt(row,0);
                Plat p = new Plat(id, nom, desc, new BigDecimal(prix.replace(",",".")), idMenu);
                p.setImagePlat(imageCourante);
                platCtrl.modifier(p);
                UIUtils.succes(this,"Plat modifié !");
            }
            charger(); viderForm(); table.clearSelection();
        } catch (Exception ex) { UIUtils.erreur(this, ex.getMessage()); }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row == -1) { UIUtils.erreur(this,"Sélectionnez un plat."); return; }
        if (!UIUtils.confirmer(this,"Supprimer ce plat ?")) return;
        try {
            platCtrl.supprimer((int) tableModel.getValueAt(row,0));
            charger(); viderForm(); UIUtils.succes(this,"Plat supprimé.");
        } catch (SQLException ex) { UIUtils.erreur(this, ex.getMessage()); }
    }

    private void viderForm() {
        tfNom.setText(""); tfPrix.setText(""); taDesc.setText("");
        if (cbMenu.getItemCount()>0) cbMenu.setSelectedIndex(0);
    }

    public void charger() {
        try {
            // Menus dans combobox
            List<Menu> menus = menuCtrl.getTousLesMenus();
            cbMenu.removeAllItems();
            for (Menu m : menus) cbMenu.addItem(m);

            // Plats dans table
            List<Plat> plats = platCtrl.getTousLesPlats();
            tableModel.setRowCount(0);
            for (Plat p : plats)
                tableModel.addRow(new Object[]{
                    p.getIdPlat(), p.getNom(), p.getPrix(), p.getNomMenu(), p.getDescription()
                });
        } catch (SQLException ex) { UIUtils.erreur(this,"Chargement : "+ex.getMessage()); }
    }
}
