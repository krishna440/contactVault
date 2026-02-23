package com.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.User;
import com.scm.helpers.helper;
import com.scm.services.UserService;
import com.scm.services.ContactService;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger =
            LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;


    // ✅ This runs before every /user request
    @ModelAttribute
    public void addLoggedInUserInformation(
            Model model,
            Authentication authentication) {

        if(authentication == null)
            return;

        String email =
                helper.getEmailOfLoggedinUser(authentication);

        logger.info("Logged in user email: {}", email);

        User user =
                userService.getUserByEmail(email);

        // send loggedInUser to UI
        model.addAttribute("loggedInUser", user);


        // ✅ send contact count to sidebar
        long contactCount =
                contactService.countContacts(user);

        model.addAttribute("contactCount", contactCount);
    }


    // ✅ User Profile Page
    @RequestMapping("/profile")
    public String userProfile(Model model) {

        return "user/profile";
    }


    // ✅ Dashboard Page
    @RequestMapping("/dashboard")
    public String userDashboard(Model model) {

        return "user/dashboard";
    }

}