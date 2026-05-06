package com.zylo.resumecraft.config;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class LoadingWindow {

    private static volatile JFrame frame;

    public static void show() {
        if (!"true".equals(System.getProperty("app.local.enabled"))
                && !"true".equals(System.getenv("APP_LOCAL_ENABLED"))
                && !"true".equals(System.getProperty("RESUMECRAFT_DESKTOP_APP"))
                && !"true".equals(System.getenv("RESUMECRAFT_DESKTOP_APP"))) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("ResumeCraft");
            frame.setUndecorated(true);
            frame.setSize(320, 160);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            JLabel title = new JLabel("ResumeCraft", SwingConstants.CENTER);
            title.setFont(new Font("SansSerif", Font.BOLD, 18));
            title.setForeground(new Color(59, 130, 246));
            title.setBorder(BorderFactory.createEmptyBorder(30, 0, 8, 0));

            JLabel subtitle = new JLabel("正在启动，请稍候...", SwingConstants.CENTER);
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
            subtitle.setForeground(new Color(120, 120, 120));

            panel.add(title, BorderLayout.NORTH);
            panel.add(subtitle, BorderLayout.CENTER);

            frame.setContentPane(panel);
            frame.setVisible(true);
        });
    }

    public static void close() {
        JFrame f = frame;
        if (f != null) {
            SwingUtilities.invokeLater(() -> {
                f.dispose();
                frame = null;
            });
        }
    }
}

