package view;

import controller.AuthController;
import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final AuthController auth = new AuthController();

    // ── Connexion ─────────────────────────────────────────────
    private JTextField     tfNom;
    private JPasswordField pfMdp;

    // ── Inscription ───────────────────────────────────────────
    private JTextField     tfInscNom;
    private JPasswordField pfInscMdp;
    private JPasswordField pfInscConfirm;
    private JComboBox<RoleItem> cbRole;
    private JLabel         lblForce;

    public LoginFrame() {
        setTitle("Restaurant – Connexion / Inscription");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    // ════════════════════════════════════════════════════════
    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.FOND);

        // ── En-tête ───────────────────────────────────────────
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(UIUtils.ROUGE);
        header.setBorder(new EmptyBorder(22, 20, 22, 20));
        JLabel logo  = new JLabel("Welcome", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.PLAIN, 46));
        JLabel titre = new JLabel("Restaurant Manager", SwingConstants.CENTER);
        titre.setFont(UIUtils.TITLE);
        titre.setForeground(Color.WHITE);
        JLabel sous  = new JLabel("Bienvenue dans votre espace", SwingConstants.CENTER);
        sous.setFont(UIUtils.SMALL);
        sous.setForeground(new Color(252, 165, 165));
        JPanel col = new JPanel(new GridLayout(3, 1, 0, 4));
        col.setOpaque(false);
        col.add(logo); col.add(titre); col.add(sous);
        header.add(col);
        root.add(header, BorderLayout.NORTH);

        // ── Onglets ───────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.HEADER);
        tabs.setBackground(UIUtils.BLANC);
        tabs.addTab("  Connexion",    buildConnexionPanel());
        tabs.addTab("  Inscription",  buildInscriptionPanel());
        root.add(tabs, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ════════════════════════════════════════════════════════
    //  ONGLET CONNEXION
    // ════════════════════════════════════════════════════════
    private JPanel buildConnexionPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.BLANC);
        form.setBorder(new EmptyBorder(28, 40, 28, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.weightx = 1;

        // Nom
        g.gridy = 0; g.insets = new Insets(0, 0, 4, 0);
        form.add(label("Nom d'utilisateur"), g);
        g.gridy = 1; g.insets = new Insets(0, 0, 14, 0);
        tfNom = UIUtils.champ(20);
        form.add(tfNom, g);

        // Mot de passe
        g.gridy = 2; g.insets = new Insets(0, 0, 4, 0);
        form.add(label("Mot de passe"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 24, 0);
        pfMdp = UIUtils.champMdp(20);
        form.add(pfMdp, g);

        // Bouton
        g.gridy = 4; g.insets = new Insets(0, 0, 0, 0);
        JButton btnLogin = UIUtils.bouton("Se connecter", UIUtils.ROUGE);
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.setFont(UIUtils.HEADER);
        form.add(btnLogin, g);

        // Hint
        g.gridy = 5; g.insets = new Insets(18, 0, 0, 0);
        JLabel hint = new JLabel("Comptes test : cuisinier1 | serveuse1 | client1  (pass123)",
                SwingConstants.CENTER);
        hint.setFont(UIUtils.SMALL);
        hint.setForeground(UIUtils.GRIS);
        form.add(hint, g);

        btnLogin.addActionListener(e -> tenterConnexion());
        pfMdp.addActionListener(e -> tenterConnexion());
        return form;
    }

    // ════════════════════════════════════════════════════════
    //  ONGLET INSCRIPTION
    // ════════════════════════════════════════════════════════
    private JPanel buildInscriptionPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.BLANC);
        form.setBorder(new EmptyBorder(22, 40, 22, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.weightx = 1;

        // Nom
        g.gridy = 0; g.insets = new Insets(0, 0, 4, 0);
        form.add(label("Nom d'utilisateur  (min. 3 caractères)"), g);
        g.gridy = 1; g.insets = new Insets(0, 0, 12, 0);
        tfInscNom = UIUtils.champ(20);
        form.add(tfInscNom, g);

        // Rôle
        g.gridy = 2; g.insets = new Insets(0, 0, 4, 0);
        form.add(label("Rôle"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 12, 0);
        cbRole = new JComboBox<>(new RoleItem[]{
            new RoleItem(Utilisateur.Role.client,    "  Client"),
            new RoleItem(Utilisateur.Role.serveuse,  "  Serveuse"),
            new RoleItem(Utilisateur.Role.cuisinier, "  Cuisinier")
        });
        cbRole.setFont(UIUtils.BODY);
        cbRole.setPreferredSize(new Dimension(0, 34));
        form.add(cbRole, g);

        // Mot de passe
        g.gridy = 4; g.insets = new Insets(0, 0, 4, 0);
        form.add(label("Mot de passe  (min. 4 caractères)"), g);
        g.gridy = 5; g.insets = new Insets(0, 0, 4, 0);
        pfInscMdp = UIUtils.champMdp(20);
        form.add(pfInscMdp, g);

        // Jauge de force
        g.gridy = 6; g.insets = new Insets(0, 0, 10, 0);
        lblForce = new JLabel(" ");
        lblForce.setFont(UIUtils.SMALL);
        form.add(lblForce, g);

        // Confirmation
        g.gridy = 7; g.insets = new Insets(0, 0, 4, 0);
        form.add(label("Confirmer le mot de passe"), g);
        g.gridy = 8; g.insets = new Insets(0, 0, 20, 0);
        pfInscConfirm = UIUtils.champMdp(20);
        form.add(pfInscConfirm, g);

        // Bouton
        g.gridy = 9; g.insets = new Insets(0, 0, 0, 0);
        JButton btnInscrire = UIUtils.bouton("Créer mon compte", UIUtils.VERT);
        btnInscrire.setPreferredSize(new Dimension(0, 42));
        btnInscrire.setFont(UIUtils.HEADER);
        form.add(btnInscrire, g);

        // Listener force mdp
        pfInscMdp.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { majForce(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { majForce(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { majForce(); }
        });

        btnInscrire.addActionListener(e -> tenterInscription());
        return form;
    }

    // ════════════════════════════════════════════════════════
    //  LOGIQUE
    // ════════════════════════════════════════════════════════
    private void tenterConnexion() {
        String nom = tfNom.getText().trim();
        String mdp = new String(pfMdp.getPassword());
        if (nom.isEmpty() || mdp.isEmpty()) {
            UIUtils.erreur(this, "Veuillez remplir tous les champs.");
            return;
        }
        try {
            Utilisateur u = auth.seConnecter(nom, mdp);
            if (u == null) {
                UIUtils.erreur(this, "Identifiants incorrects.");
                pfMdp.setText("");
            } else {
                ouvrirTableauDeBord(u);
            }
        } catch (SQLException ex) {
            UIUtils.erreur(this, "Erreur base de données : " + ex.getMessage());
        }
    }

    private void tenterInscription() {
        String nom     = tfInscNom.getText().trim();
        String mdp     = new String(pfInscMdp.getPassword());
        String confirm = new String(pfInscConfirm.getPassword());
        RoleItem role  = (RoleItem) cbRole.getSelectedItem();

        try {
            boolean ok = auth.inscrire(nom, mdp, confirm, role.role);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                    "Compte créé avec succès !\n" +
                    "Rôle : " + role.libelle + "\n" +
                    "Vous pouvez maintenant vous connecter.",
                    "Inscription réussie", JOptionPane.INFORMATION_MESSAGE);
                // Pré-remplir l'onglet connexion et basculer
                tfNom.setText(nom);
                pfMdp.setText("");
                tfInscNom.setText(""); pfInscMdp.setText(""); pfInscConfirm.setText("");
                lblForce.setText(" ");
                JTabbedPane tabs = (JTabbedPane) SwingUtilities.getAncestorOfClass(
                        JTabbedPane.class, tfInscNom);
                if (tabs != null) tabs.setSelectedIndex(0);
            }
        } catch (IllegalArgumentException ex) {
            UIUtils.erreur(this, ex.getMessage());
        } catch (SQLException ex) {
            UIUtils.erreur(this, "Erreur base de données : " + ex.getMessage());
        }
    }

    /** Indicateur de force du mot de passe */
    private void majForce() {
        String mdp = new String(pfInscMdp.getPassword());
        if (mdp.isEmpty()) { lblForce.setText(" "); return; }
        int score = 0;
        if (mdp.length() >= 6)  score++;
        if (mdp.length() >= 10) score++;
        if (mdp.matches(".*[A-Z].*")) score++;
        if (mdp.matches(".*[0-9].*")) score++;
        if (mdp.matches(".*[^a-zA-Z0-9].*")) score++;
        String[] niveaux  = {"Très faible", "Faible", "Moyen", "Bon", "Fort", "Très fort"};
        Color[]  couleurs = {Color.RED, Color.RED, Color.ORANGE, Color.ORANGE, UIUtils.VERT, UIUtils.VERT};
        String barres = "|".repeat(score + 1) + ".".repeat(5 - score);
        lblForce.setText("Force : [" + barres + "]  " + niveaux[score]);
        lblForce.setForeground(couleurs[score]);
    }

    private void ouvrirTableauDeBord(Utilisateur u) {
        dispose();
        switch (u.getRole()) {
            case cuisinier -> new CuisinierFrame(u).setVisible(true);
            case serveuse  -> new ServeuseFrame(u).setVisible(true);
            case client    -> new ClientFrame(u).setVisible(true);
        }
    }

    private JLabel label(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(UIUtils.HEADER);
        return l;
    }

    // ── Inner class pour JComboBox ────────────────────────────
    private static class RoleItem {
        final Utilisateur.Role role;
        final String libelle;
        RoleItem(Utilisateur.Role role, String libelle) {
            this.role    = role;
            this.libelle = libelle;
        }
        @Override public String toString() { return libelle; }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}