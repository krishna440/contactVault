package com.scm.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scm.entities.Reminder;
import com.scm.entities.User;

@Repository
public interface ReminderRepo extends JpaRepository<Reminder, String> {

    List<Reminder> findByUser(User user);

    List<Reminder> findByUserOrderByReminderDateTimeAsc(User user);

    List<Reminder> findByEmailSentFalse();

}