package com.zylo.resumecraft;

import com.zylo.resumecraft.config.DotenvConfig;
import com.zylo.resumecraft.config.LoadingWindow;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
@MapperScan("com.zylo.resumecraft.mapper")
public class ResumeCraftApplication {

    public static void main(String[] args) {
        LoadingWindow.show();
        try {
            configureLocalDataDir();
            SpringApplication app = new SpringApplication(ResumeCraftApplication.class);
            app.addInitializers(new DotenvConfig());
            app.run(args);
        } catch (Exception exception) {
            LoadingWindow.close();
            showErrorDialog(exception);
            System.exit(1);
        }
    }

    private static void showErrorDialog(Exception exception) {
        String raw = exception.getMessage();
        final String message = (raw == null || raw.isBlank())
                ? exception.getClass().getSimpleName()
                : raw;

        try {
            javax.swing.SwingUtilities.invokeAndWait(() -> {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "ResumeCraft 启动失败：\n" + message,
                        "ResumeCraft",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            });
        } catch (Exception ignored) {
            System.err.println("ResumeCraft startup failed: " + message);
            exception.printStackTrace();
        }
    }

    private static void configureLocalDataDir() {
        String resumeCraftDataDir = System.getProperty("resumecraft.data-dir");
        if (resumeCraftDataDir != null && !resumeCraftDataDir.isBlank()) {
            System.setProperty("resumecraft.data-dir", resumeCraftDataDir);
            return;
        }

        String configuredDataDir = System.getenv("RESUMECRAFT_DATA_DIR");
        if (configuredDataDir != null && !configuredDataDir.isBlank()) {
            useFirstWritableDataDir(Path.of(configuredDataDir));
            return;
        }

        String localAppData = System.getenv("LOCALAPPDATA");
        String userHome = System.getProperty("user.home");
        String userDir = System.getProperty("user.dir");

        useFirstWritableDataDir(
                userDir == null || userDir.isBlank() ? null : Path.of(userDir, ".resumecraft-data"),
                localAppData == null || localAppData.isBlank() ? null : Path.of(localAppData, "ResumeCraft"),
                userHome == null || userHome.isBlank() ? null : Path.of(userHome, "ResumeCraft")
        );
    }

    private static void useFirstWritableDataDir(Path... candidates) {
        IOException lastIoException = null;
        RuntimeException lastRuntimeException = null;

        for (Path candidate : candidates) {
            if (candidate == null) {
                continue;
            }

            try {
                Files.createDirectories(candidate.resolve("data"));
                System.setProperty("resumecraft.data-dir", candidate.toString());
                return;
            } catch (IOException exception) {
                lastIoException = exception;
            } catch (RuntimeException exception) {
                lastRuntimeException = exception;
            }
        }

        if (lastIoException != null) {
            throw new IllegalStateException("Failed to create ResumeCraft local data directory", lastIoException);
        }
        throw new IllegalStateException("Failed to create ResumeCraft local data directory", lastRuntimeException);
    }
}

