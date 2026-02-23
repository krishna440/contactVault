package com.scm.controllers;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.helpers.helper;
import com.scm.services.UserService;

@ControllerAdvice
public class RootController {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {

        if (authentication == null) {
            return;
        }

        System.out.println("Adding LoggedIn Information to the model");

        String username = helper.getEmailOfLoggedinUser(authentication);

        if (username == null) {
            logger.warn("⚠️ Logged in username/email is null");
            return;
        }

        logger.info("User Logged in: {}", username);

        User user = userService.getUserByEmail(username);

        if (user == null) {
            logger.warn(" No user found in DB for email: {}", username);
            return;
        }

        // ✅ Now safe
        System.out.println(user.getEmail());
        System.out.println(user.getName());

        model.addAttribute("loggedInUser", user);
    }
}
