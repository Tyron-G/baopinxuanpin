package com.oneaix.selection.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/** 后端根路径入口，避免误访问 / 产生静态资源异常日志 2026-06-05 */
@Controller
public class HomeController {

    @GetMapping("/")
    public RedirectView index() {
        return new RedirectView("/swagger-ui.html");
    }
}
