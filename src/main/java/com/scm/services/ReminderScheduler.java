package com.scm.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.scm.entities.Reminder;
import com.scm.repositories.ReminderRepo;

@Service
public class ReminderScheduler {

    @Autowired
    private ReminderRepo reminderRepo;

    @Autowired
    private EmailService emailService;


    @Scheduled(fixedRate = 60000) // runs every 1 minute
    public void sendReminderEmails() {

        List<Reminder> reminders =
                reminderRepo.findByEmailSentFalse();

        LocalDateTime now = LocalDateTime.now();

        for(Reminder reminder : reminders) {

            if(reminder.getReminderDateTime().isBefore(now)
            || reminder.getReminderDateTime().isEqual(now)) {

                String to =
                        reminder.getUser().getEmail();

                String subject =
                        "Reminder: " + reminder.getMessage();

                String body =
                        "Hello " + reminder.getUser().getName()
                        + "\n\nReminder:\n"
                        + reminder.getMessage()
                        + "\nContact: "
                        + reminder.getContact().getName()
                        + "\nTime: "
                        + reminder.getReminderDateTime();

                emailService.sendEmail(to, subject, body);

                reminder.setEmailSent(true);

                reminderRepo.save(reminder);

            }

        }

    }

}