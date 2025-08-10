package com.vahabvahabov.LoginDemo.controller.imp;

import com.vahabvahabov.LoginDemo.controller.HomeController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerImpl implements HomeController {

    @Override
    @GetMapping("/home")
    public String getHomeAfterLogin() {
        return "home"; //
    }

    @Override
    @GetMapping("/")
    public String getHomePage() {
        return "home";
    }

    @Override
    @GetMapping("/my-login")
    public String getLoginPage() {
        return "my-login";
    }

    @Override
    @GetMapping("/forgot-password")
    public String getForgotPasswordPage() {
        return "forgot-password";
    }
}