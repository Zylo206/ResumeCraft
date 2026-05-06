package com.zylo.resumecraft.controller;

import com.zylo.resumecraft.common.Result;
import com.zylo.resumecraft.dto.ModuleCreateDTO;
import com.zylo.resumecraft.dto.ModuleReorderDTO;
import com.zylo.resumecraft.dto.ModuleUpdateDTO;
import com.zylo.resumecraft.dto.ResumeCreateDTO;
import com.zylo.resumecraft.dto.ResumeExportRequestDTO;
import com.zylo.resumecraft.dto.ResumeUpdateDTO;
import com.zylo.resumecraft.entity.ResumeModule;
import com.zylo.resumecraft.service.ResumeExportService;
import com.zylo.resumecraft.service.ResumeModuleService;
import com.zylo.resumecraft.service.ResumeService;
import com.zylo.resumecraft.util.SecurityUtils;
import com.zylo.resumecraft.vo.ResumeListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Tag(name = "简历接口")
@RestController
@RequestMapping("/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeModuleService moduleService;
    private final ResumeExportService resumeExportService;

    public ResumeController(ResumeService resumeService, ResumeModuleService moduleService,
                            ResumeExportService resumeExportService) {
        this.resumeService = resumeService;
        this.moduleService = moduleService;
        this.resumeExportService = resumeExportService;
    }

    @Operation(summary = "获取简历列表")
    @GetMapping
    public Result<List<ResumeListVO>> list() {
        return Result.success(resumeService.listByUserId(getCurrentUserId()));
    }

    @Operation(summary = "创建简历")
    @PostMapping
    public Result<ResumeListVO> create(@RequestBody ResumeCreateDTO dto) {
        return Result.success(resumeService.create(getCurrentUserId(), dto));
    }

    @Operation(summary = "更新简历信息")
    @PutMapping("/{id}")
    public Result<ResumeListVO> update(@PathVariable Long id, @Valid @RequestBody ResumeUpdateDTO dto) {
        return Result.success(resumeService.update(getCurrentUserId(), id, dto));
    }

    @Operation(summary = "删除简历")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resumeService.delete(getCurrentUserId(), id);
        return Result.success();
    }

    @Operation(summary = "获取简历所有模块")
    @GetMapping("/{id}/modules")
    public Result<List<ResumeModule>> listModules(@PathVariable Long id) {
        return Result.success(moduleService.listByResumeId(id, getCurrentUserId()));
    }

    @Operation(summary = "新增模块")
    @PostMapping("/{id}/modules")
    public Result<ResumeModule> createModule(@PathVariable Long id, @Valid @RequestBody ModuleCreateDTO dto) {
        return Result.success(moduleService.create(id, getCurrentUserId(), dto));
    }

    @Operation(summary = "更新模块内容")
    @PostMapping("/{id}/modules/{mid}/update")
    public Result<ResumeModule> updateModule(@PathVariable Long id, @PathVariable Long mid,
                                             @Valid @RequestBody ModuleUpdateDTO dto) {
        return Result.success(moduleService.update(id, getCurrentUserId(), mid, dto));
    }

    @Operation(summary = "更新模块排序")
    @PutMapping("/{id}/modules/reorder")
    public Result<List<ResumeModule>> reorderModules(@PathVariable Long id, @Valid @RequestBody ModuleReorderDTO dto) {
        return Result.success(moduleService.reorder(id, getCurrentUserId(), dto));
    }

    @Operation(summary = "删除模块")
    @DeleteMapping("/{id}/modules/{mid}")
    public Result<Void> deleteModule(@PathVariable Long id, @PathVariable Long mid) {
        moduleService.delete(id, getCurrentUserId(), mid);
        return Result.success();
    }

    @Operation(summary = "导出简历 PDF")
    @PostMapping("/{id}/export-pdf")
    public ResponseEntity<ByteArrayResource> exportPdf(@PathVariable Long id,
                                                       @RequestBody(required = false) ResumeExportRequestDTO dto) {
        var exportedFile = resumeExportService.exportPdf(id, getCurrentUserId(), dto);
        String contentDisposition = ContentDisposition.attachment()
                .filename(exportedFile.fileName(), StandardCharsets.UTF_8)
                .build()
                .toString();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(exportedFile.content()));
    }

    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }
}

