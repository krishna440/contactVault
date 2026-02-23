package com.scm.services;

import java.util.List;

import com.scm.entities.Reminder;
import com.scm.entities.User;

public interface ReminderService {

    Reminder save(Reminder reminder);

    List<Reminder> getByUser(User user);

    List<Reminder> getUpcomingReminders(User user);

    void delete(String reminderId);

}