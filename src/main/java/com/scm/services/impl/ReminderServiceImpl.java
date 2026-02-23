package com.scm.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scm.entities.Reminder;
import com.scm.entities.User;
import com.scm.repositories.ReminderRepo;
import com.scm.services.ReminderService;

@Service
public class ReminderServiceImpl implements ReminderService {

    @Autowired
    private ReminderRepo reminderRepo;

    @Override
    public Reminder save(Reminder reminder) {

        return reminderRepo.save(reminder);

    }

    @Override
    public List<Reminder> getByUser(User user) {

        return reminderRepo.findByUser(user);

    }

    @Override
    public List<Reminder> getUpcomingReminders(User user) {

        return reminderRepo.findByUserOrderByReminderDateTimeAsc(user);

    }

    @Override
    public void delete(String reminderId) {

        reminderRepo.deleteById(reminderId);

    }

}