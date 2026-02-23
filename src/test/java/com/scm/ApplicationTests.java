package com.scm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.scm.services.EmailService;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private EmailService service;   // ✅ FIX

    @Test
    void contextLoads() {
    }

    @Test
    void sendEmailTest(){

        service.sendEmail(
            "pawarbhupendra189@gmail.com",
            "Just Testing Email",
            "This is scm project working on email service"
        );

        System.out.println("Email sent successfully");

    }
}
