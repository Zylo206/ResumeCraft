package com.zylo.resumecraft.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zylo.resumecraft.common.BusinessException;
import com.zylo.resumecraft.common.ResultCode;
import com.zylo.resumecraft.dto.ResumeExportRequestDTO;
import com.zylo.resumecraft.entity.User;
import com.zylo.resumecraft.mapper.UserMapper;
import com.zylo.resumecraft.service.ResumeExportService;
import com.zylo.resumecraft.service.ResumeModuleService;
import com.zylo.resumecraft.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeExportServiceImpl implements ResumeExportService {
    private final ResumeService resumeService;
    private final ResumeModuleService resumeModuleService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Value("${app.project-root:}")
    private String configuredProjectRoot;

    @Value("${app.environment:development}")
    private String appEnvironment;

    @Override
    public ExportedResumeFile exportPdf(Long resumeId, Long userId, ResumeExportRequestDTO request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!isDevelopment() && !"ACTIVE".equals(user.getMembershipStatus())) {
            throw new BusinessException(ResultCode.MEMBERSHIP_REQUIRED);
        }

        var resume = resumeService.getByIdAndUserId(resumeId, userId);
        var modules = resumeModuleService.listByResumeId(resumeId, userId);

        Path tempFile = null;
        try {
            Path projectRoot = resolveProjectRoot();
            Path scriptPath = projectRoot.resolve("scripts/export-resume-pdf.ts");
            Path tsxPath = projectRoot.resolve("node_modules/.bin/tsx");

            if (!Files.exists(scriptPath) || !Files.exists(tsxPath)) {
                throw new BusinessException(ResultCode.EXPORT_FAILED.getCode(), "导出脚本未准备好");
            }

            tempFile = Files.createTempFile("resume-craft-export-", ".pdf");
            ProcessBuilder processBuilder = new ProcessBuilder(
                    tsxPath.toString(),
                    scriptPath.toString(),
                    "--output",
                    tempFile.toString()
            );
            processBuilder.directory(projectRoot.toFile());
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            try (OutputStream outputStream = process.getOutputStream()) {
                outputStream.write(buildPayload(modules, request, resume.getLanguage()).getBytes(StandardCharsets.UTF_8));
            }

            boolean finished = process.waitFor(2, TimeUnit.MINUTES);
            String processOutput = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (!finished) {
                process.destroyForcibly();
                throw new BusinessException(ResultCode.EXPORT_FAILED.getCode(), "导出超时");
            }
            if (process.exitValue() != 0) {
                log.error("Resume export worker failed: {}", processOutput);
                throw new BusinessException(ResultCode.EXPORT_FAILED);
            }

            byte[] content = Files.readAllBytes(tempFile);
            return new ExportedResumeFile(content, sanitizeFileName(resume.getTitle()) + ".pdf");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to export resume {}", resumeId, e);
            throw new BusinessException(ResultCode.EXPORT_FAILED);
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    log.warn("Failed to delete temp export file {}", tempFile, e);
                }
            }
        }
    }

    private String buildPayload(Object modules, ResumeExportRequestDTO request, String language) throws IOException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("modules", modules);

        Map<String, Object> options = new LinkedHashMap<>();
        options.put("pageMode", blankToNull(request == null ? null : request.getPageMode()));
        options.put("templateId", blankToNull(request == null ? null : request.getTemplateId()));
        options.put("density", blankToNull(request == null ? null : request.getDensity()));
        options.put("accentPreset", blankToNull(request == null ? null : request.getAccentPreset()));
        options.put("headingStyle", blankToNull(request == null ? null : request.getHeadingStyle()));
        options.put("language", "en-US".equals(language) ? "en-US" : "zh-CN");
        payload.put("options", options);

        return objectMapper.writeValueAsString(payload);
    }

    private Path resolveProjectRoot() {
        if (configuredProjectRoot != null && !configuredProjectRoot.isBlank()) {
            return Paths.get(configuredProjectRoot).toAbsolutePath().normalize();
        }

        Path current = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        if (current.getFileName() != null && "server".equals(current.getFileName().toString())) {
            return current.getParent();
        }
        return current;
    }

    private String sanitizeFileName(String title) {
        if (title == null || title.isBlank()) {
            return "resume";
        }
        return title.trim().replaceAll("[\\\\/:*?\"<>|]", "-");
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isDevelopment() {
        return "development".equalsIgnoreCase(appEnvironment);
    }
}

