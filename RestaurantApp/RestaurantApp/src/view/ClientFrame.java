package view;

import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClientFrame extends JFrame {

    private final Utilisateur utilisateur;

    public ClientFrame(Utilisateur u) {
        this.utilisateur = u;
        setTitle("Restaurant – Client : " + u.getNomUtilisateur());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIUtils.BODY);

        PasserCommandePanel cmdPanel  = new PasserCommandePanel(utilisateur);
        MesCommandesPanel   mesPanel  = new MesCommandesPanel(utilisateur);

        tabs.addTab("  Commander",    cmdPanel);
        tabs.addTab(" Mes Commandes", mesPanel);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.ROUGE);
        header.setBorder(new EmptyBorder(10,16,10,16));
        JLabel titre = new JLabel("  Restaurant – Espace Client  |  " + utilisateur.getNomUtilisateur());
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
}
