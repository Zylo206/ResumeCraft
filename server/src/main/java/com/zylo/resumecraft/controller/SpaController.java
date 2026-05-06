package com.zylo.resumecraft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
    @GetMapping({
            "/dashboard",
            "/editor/{id}",
            "/editor/{id}/modules/{moduleId}/field-optimize",
            "/preview/{id}"
    })
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}

