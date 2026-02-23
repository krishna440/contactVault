package com.scm.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.entities.Reminder;
import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.helpers.helper;
import com.scm.services.EmailService;
import com.scm.services.ReminderService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home(Model model) {
        System.out.println("Home Page Handler");

        // Sending Data To View
        // model.addAttribute("name","Substring Tech");
        model.addAttribute("name", "Substring Tech");
        model.addAttribute("YoutubeChannel", "Learn With Durgesh");
        model.addAttribute("LinkedIN", "https://www.linkedin.com/in/bhupendrasing-pawar-05b258261");
        return "home";
    }

    // about route

    @RequestMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        System.out.println("About Page Loading");
        return "about";
    }

    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("Services Page");
        return "services";
    }

    @GetMapping("/contact")
    public String contact() {
        return new String("contact");
    }

    @GetMapping("/login")
    public String login() {
        return new String("login");
    }

    @GetMapping("/register")
    public String register(Model model) {

        UserForm userForm = new UserForm();
        // default data bhi daal skate hai
        userForm.setName("");
        model.addAttribute("userForm", userForm);
        return "register";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(Model model, Authentication authentication) {

        String email = helper.getEmailOfLoggedinUser(authentication);

        User user = userService.getUserByEmail(email);

        List<Reminder> reminders = reminderService.getUpcomingReminders(user);

        model.addAttribute("reminders", reminders);

        return "user/dashboard";

    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {

    return "forgot_password";

}

    @PostMapping("/forgot-password")
public String processForgotPassword(
        @RequestParam String email,
        HttpSession session) {

    User user = userService.getUserByEmail(email);

    if (user == null) {

        session.setAttribute("message",
                Message.builder()
                        .content("User not found")
                        .type(MessageType.red)
                        .build()
        );

        return "forgot_password";
    }

    String token = UUID.randomUUID().toString();

    user.setEmailToken(token);

    userService.updateUser(user);

    String resetLink =
            "http://localhost:8081/reset-password?token=" + token;

    emailService.sendEmail(
            email,
            "Password Reset",
            "Click this link to reset password:\n" + resetLink
    );

    session.setAttribute("message",
            Message.builder()
                    .content("Reset link sent to your email")
                    .type(MessageType.green)
                    .build()
    );

    return "forgot_password";
}

    @GetMapping("/reset-password")
    public String resetPasswordPage(
        @RequestParam String token,
        Model model
) {

    model.addAttribute("token", token);

    return "reset_password";

}

@PostMapping("/reset-password")
public String processResetPassword(
        @RequestParam String token,
        @RequestParam String password,
        HttpSession session
) {

    User user =
            userService.getUserByEmailToken(token);

    if (user == null) {

        session.setAttribute("message",
                Message.builder()
                        .content("Invalid or expired token")
                        .type(MessageType.red)
                        .build()
        );

        return "reset_password";
    }

     // encoder should handle inside service
    user.setPassword(passwordEncoder.encode(password));
    user.setEmailToken(null);

    userService.updateUser(user);

    session.setAttribute("message",
            Message.builder()
                    .content("Password reset successful")
                    .type(MessageType.green)
                    .build()
    );

    return "redirect:/login";
}

    // Processing Register

    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
            HttpSession session) {
        System.out.println("Processing Registration");

        // Fetch form data
        // UserForm
        System.out.println(userForm);

        // validate form data
        if (rBindingResult.hasErrors()) {
            return "register";
        }
        // TODO: validate userForm[Next Video]

        // Save to DB

        // User user = User.builder()
        // .name(userForm.getName())
        // .email(userForm.getEmail())
        // .password(userForm.getPassword())
        // .about(userForm.getAbout())
        // .PhoneNumber(userForm.getPhoneNumber())
        // .profilePic("https://www.vecteezy.com/vector-art/30504836-avatar-account-flat-vector-isolated-on-transparent-background-for-graphic-and-web-design-default-social-media-profile-photo-symbol-profile-and-people-silhouette-user-icon")
        // .build();

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setEnabled(false);
        user.setProfilePic(
                "https://www.vecteezy.com/vector-art/30504836-avatar-account-flat-vector-isolated-on-transparent-background-for-graphic-and-web-design-default-social-media-profile-photo-symbol-profile-and-people-silhouette-user-icon");

        User savdUser = userService.saveUser(user); // changes made change from savd to saved
        System.out.println("User Saved");
        // userService

        // message = "Registration Successful"

        // add the message

        Message message = Message.builder().content("Registration Successful").type(MessageType.green).build();
        session.setAttribute("message", message);

        // redirectTo login page

        return "redirect:/register";
    }

}
