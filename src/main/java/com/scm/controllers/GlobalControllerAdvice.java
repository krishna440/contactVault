package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.helpers.helper;
import com.scm.services.ContactService;
import com.scm.services.UserService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactService contactService;


    @ModelAttribute("contactCount")
    public int contactCount(Authentication authentication) {

        if(authentication == null)
            return 0;

        String email =
                helper.getEmailOfLoggedinUser(authentication);

        User user =
                userService.getUserByEmail(email);

        if(user == null)
            return 0;

        return contactService.getContactsByUser(user).size();
    }
}