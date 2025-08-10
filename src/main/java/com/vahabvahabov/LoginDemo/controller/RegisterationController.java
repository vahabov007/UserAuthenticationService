package com.vahabvahabov.LoginDemo.controller;

import com.vahabvahabov.LoginDemo.model.User;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface RegisterationController {

    public String showRegisterationForm(Model model);

}
