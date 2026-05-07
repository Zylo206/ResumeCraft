package com.zylo.resumecraft.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.local.enabled", havingValue = "true")
public class DesktopLifecycleManager implements ApplicationListener<WebServerInitializedEvent> {

    private final ConfigurableApplicationContext applicationContext;
    private volatile long lastHeartbeatMillis = System.currentTimeMillis();
    private volatile int serverPort;
    private ScheduledExecutorService scheduler;
    private TrayIcon trayIcon;

    @Value("${app.local.heartbeat-timeout-seconds:30}")
    private int heartbeatTimeoutSeconds;

    @Value("${app.local.show-system-tray:false}")
    private boolean showSystemTray;

    public DesktopLifecycleManager(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        serverPort = event.getWebServer().getPort();
        if (isDesktopPackagedMode()) {
            startHeartbeatChecker();
            if (showSystemTray) {
                initSystemTray();
            }
        } else {
            log.info("Desktop lifecycle manager running in dev mode (heartbeat disabled)");
        }
    }

    private boolean isDesktopPackagedMode() {
        return "true".equals(System.getProperty("RESUMECRAFT_DESKTOP_APP"))
                || "true".equals(System.getenv("RESUMECRAFT_DESKTOP_APP"));
    }

    public void recordHeartbeat() {
        lastHeartbeatMillis = System.currentTimeMillis();
    }

    private void startHeartbeatChecker() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "desktop-heartbeat-checker");
            thread.setDaemon(true);
            return thread;
        });

        scheduler.scheduleAtFixedRate(() -> {
            try {
                long elapsed = System.currentTimeMillis() - lastHeartbeatMillis;
                if (elapsed > heartbeatTimeoutSeconds * 1000L) {
                    log.info("No heartbeat received for {} seconds, shutting down", heartbeatTimeoutSeconds);
                    shutdown();
                }
            } catch (Exception exception) {
                log.error("Heartbeat checker error", exception);
            }
        }, 10, 10, TimeUnit.SECONDS);

        log.info("Desktop heartbeat checker started (timeout: {}s)", heartbeatTimeoutSeconds);
    }

    private void initSystemTray() {
        if (!SystemTray.isSupported()) {
            log.debug("System tray is not supported on this platform");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = createTrayIcon();

            trayIcon = new TrayIcon(image, "ResumeCraft");
            trayIcon.setImageAutoSize(true);

            PopupMenu popup = new PopupMenu();

            java.awt.MenuItem openItem = new java.awt.MenuItem("Open ResumeCraft");
            openItem.addActionListener(event -> openBrowser());
            popup.add(openItem);

            popup.addSeparator();

            java.awt.MenuItem quitItem = new java.awt.MenuItem("Quit");
            quitItem.addActionListener(event -> shutdown());
            popup.add(quitItem);

            trayIcon.setPopupMenu(popup);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        openBrowser();
                    }
                }
            });

            tray.add(trayIcon);
            log.info("System tray icon initialized");
        } catch (AWTException exception) {
            log.warn("Failed to initialize system tray", exception);
        }
    }

    private Image createTrayIcon() {
        int size = 32;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setColor(new Color(59, 130, 246));
        graphics.fillRoundRect(2, 2, size - 4, size - 4, 8, 8);

        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 20));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        String text = "R";
        int x = (size - fontMetrics.stringWidth(text)) / 2;
        int y = (size - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
        graphics.drawString(text, x, y);

        graphics.dispose();
        return image;
    }

    private void openBrowser() {
        String url = "http://127.0.0.1:" + serverPort;
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                String os = System.getProperty("os.name", "").toLowerCase();
                if (os.contains("mac")) {
                    new ProcessBuilder("open", url).start();
                } else {
                    new ProcessBuilder("explorer.exe", url).start();
                }
            }
        } catch (Exception exception) {
            log.warn("Failed to open browser", exception);
        }
    }

    public void shutdown() {
        log.info("ResumeCraft desktop shutting down...");
        if (scheduler != null) {
            scheduler.shutdown();
        }
        if (trayIcon != null && SystemTray.isSupported()) {
            try {
                SystemTray.getSystemTray().remove(trayIcon);
            } catch (Exception exception) {
                log.debug("Failed to remove system tray icon cleanly", exception);
            }
        }
        LoadingWindow.close();
        SpringApplication.exit(applicationContext, () -> 0);
    }
}
