// src/main/java/com/vahabvahabov/LoginDemo/controller/imp/RegisterationControllerImpl.java

package com.vahabvahabov.LoginDemo.controller.imp;

import com.vahabvahabov.LoginDemo.controller.RegisterationController;
import com.vahabvahabov.LoginDemo.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterationControllerImpl implements RegisterationController {

    @Override
    @GetMapping("/register")
    public String showRegisterationForm(Model model) {
        model.addAttribute("user", new User());
        return "create-account";
    }


}
