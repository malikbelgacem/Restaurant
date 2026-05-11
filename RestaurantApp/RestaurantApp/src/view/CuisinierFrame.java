package view;

import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CuisinierFrame extends JFrame {

    private final Utilisateur utilisateur;

    public CuisinierFrame(Utilisateur u) {
        this.utilisateur = u;
        setTitle("Restaurant – Cuisinier : " + u.getNomUtilisateur());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(31, 41, 55));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel logo = new JLabel("  Cuisinier");
        logo.setFont(UIUtils.HEADER);
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(0,0,20,0));
        sidebar.add(logo);

        JLabel lUser = new JLabel(utilisateur.getNomUtilisateur());
        lUser.setFont(UIUtils.SMALL);
        lUser.setForeground(new Color(156,163,175));
        lUser.setAlignmentX(CENTER_ALIGNMENT);
        sidebar.add(lUser);
        sidebar.add(Box.createVerticalStrut(30));

        
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(UIUtils.BODY);

        MenuPanel menuPanel = new MenuPanel();
        PlatPanel platPanel = new PlatPanel();
        CommandesCuisinierPanel cmdPanel = new CommandesCuisinierPanel();

        tabs.addTab(" Menus",     menuPanel);
        tabs.addTab("  Plats",    platPanel);
        tabs.addTab(" Commandes", cmdPanel);

      
        JButton btnDeco = UIUtils.bouton("⬅ Déconnexion", UIUtils.ROUGE);
        btnDeco.setAlignmentX(CENTER_ALIGNMENT);
        btnDeco.setMaximumSize(new Dimension(180, 38));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnDeco);
        btnDeco.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        JPanel root = new JPanel(new BorderLayout());
        root.add(sidebar, BorderLayout.WEST);
        root.add(tabs,    BorderLayout.CENTER);
        setContentPane(root);
    }
}
