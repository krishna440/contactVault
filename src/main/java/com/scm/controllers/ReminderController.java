package com.scm.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.scm.entities.Contact;
import com.scm.entities.Reminder;
import com.scm.entities.User;
import com.scm.helpers.helper;
import com.scm.services.ContactService;
import com.scm.services.ReminderService;
import com.scm.services.UserService;

@Controller
@RequestMapping("/user/reminders")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;


    // SHOW REMINDERS PAGE
    @GetMapping
    public String viewReminders(Model model, Authentication authentication) {

        String email =
                helper.getEmailOfLoggedinUser(authentication);

        User user =
                userService.getUserByEmail(email);

        List<Reminder> reminders =
                reminderService.getByUser(user);

        model.addAttribute("reminders", reminders);

        return "user/reminders";
    }


    // ADD REMINDER
    @PostMapping("/add")
    public String addReminder(
            @RequestParam String contactId,
            @RequestParam String message,
            @RequestParam String dateTime,
            Authentication authentication
    ) {

        String email =
                helper.getEmailOfLoggedinUser(authentication);

        User user =
                userService.getUserByEmail(email);

        Contact contact =
                contactService.getById(contactId);

        Reminder reminder =
                new Reminder();

        reminder.setMessage(message);

        reminder.setReminderDateTime(
                LocalDateTime.parse(dateTime)
        );

        reminder.setContact(contact);

        reminder.setUser(user);

        reminderService.save(reminder);

        return "redirect:/user/reminders";
    }


    // DELETE REMINDER
    @GetMapping("/delete/{id}")
    public String deleteReminder(@PathVariable String id) {

        reminderService.delete(id);

        return "redirect:/user/reminders";
    }

}