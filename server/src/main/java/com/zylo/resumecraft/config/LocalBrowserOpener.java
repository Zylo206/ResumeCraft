package com.zylo.resumecraft.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.local.enabled", havingValue = "true")
public class LocalBrowserOpener implements ApplicationListener<WebServerInitializedEvent> {
    @Value("${app.local.open-browser:true}")
    private boolean openBrowser;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        String url = "http://127.0.0.1:" + port;
        log.info("ResumeCraft local web app is ready at {}", url);

        LoadingWindow.close();

        if (!openBrowser) {
            return;
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
                return;
            }
            String os = System.getProperty("os.name", "").toLowerCase();
            if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else {
                new ProcessBuilder("cmd", "/c", "start", "", url).start();
            }
        } catch (Exception exception) {
            log.warn("Failed to open browser automatically. Please open {}", url, exception);
        }
    }
}

