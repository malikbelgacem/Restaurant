package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UIUtils {

    // ── Palette ──────────────────────────────────────────────
    public static final Color ROUGE     = new Color(192, 38,  38);
    public static final Color ROUGE_SOM = new Color(239, 68,  68);
    public static final Color FOND      = new Color(248, 248, 248);
    public static final Color BLANC     = Color.WHITE;
    public static final Color GRIS      = new Color(107, 114, 128);
    public static final Color VERT      = new Color(22,  163, 74);
    public static final Color ORANGE    = new Color(234, 88,  12);
    public static final Color BLEU      = new Color(37,  99,  235);

    // ── Fonts ─────────────────────────────────────────────────
    public static final Font TITLE  = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font HEADER = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SMALL  = new Font("Segoe UI", Font.PLAIN, 11);

    // ── Bouton stylisé ────────────────────────────────────────
    public static JButton bouton(String label, Color bg) {
        JButton btn = new JButton(label);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(BODY);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    // ── Panel avec titre ──────────────────────────────────────
    public static JPanel panelTitre(String titre) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        p.setBackground(ROUGE);
        JLabel l = new JLabel("  " + titre);
        l.setFont(TITLE);
        l.setForeground(BLANC);
        p.add(l);
        return p;
    }

    // ── Champ texte ───────────────────────────────────────────
    public static JTextField champ(int colonnes) {
        JTextField tf = new JTextField(colonnes);
        tf.setFont(BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            new EmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    public static JPasswordField champMdp(int colonnes) {
        JPasswordField pf = new JPasswordField(colonnes);
        pf.setFont(BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            new EmptyBorder(6, 10, 6, 10)));
        return pf;
    }

    // ── Message d'erreur ─────────────────────────────────────
    public static void erreur(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void succes(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmer(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirmation",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
