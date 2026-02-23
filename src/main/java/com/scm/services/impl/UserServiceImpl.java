package com.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.helpers.helper;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private Logger logger =
            LoggerFactory.getLogger(this.getClass());


    // SAVE USER
    @Override
    public User saveUser(User user) {

        String userId =
                UUID.randomUUID().toString();

        user.setUserId(userId);

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        user.setRoleList(
                List.of(AppConstants.ROLE_USER)
        );

        String emailToken =
                UUID.randomUUID().toString();

        user.setEmailToken(emailToken);

        User savedUser =
                userRepo.save(user);

        String emailLink =
                helper.getLinkForEmailVerification(emailToken);

        emailService.sendEmail(
                savedUser.getEmail(),
                "Verify Account : Smart Contact Manager",
                emailLink
        );

        return savedUser;
    }


    // GET USER BY ID
    @Override
    public Optional<User> getUserById(String id) {

        return userRepo.findById(id);

    }


    // UPDATE USER
    @Override
    public Optional<User> updateUser(User user) {

        User existingUser =
                userRepo.findById(user.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Not Found")
                );

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setAbout(user.getAbout());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setProfilePic(user.getProfilePic());
        existingUser.setEnabled(user.isEnabled());
        existingUser.setEmailVerified(user.isEmailVerified());
        existingUser.setPhoneVerified(user.isPhoneVerified());
        existingUser.setProvider(user.getProvider());
        existingUser.setProviderUserId(user.getProviderUserId());
        existingUser.setEmailToken(user.getEmailToken());

        User savedUser =
                userRepo.save(existingUser);

        return Optional.of(savedUser);
    }


    // DELETE USER
    @Override
    public void deleteUser(String id) {

        User user =
                userRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User Not Found")
                );

        userRepo.delete(user);
    }


    // CHECK USER EXIST
    @Override
    public boolean isUserExist(String userId) {

        return userRepo.findById(userId).isPresent();

    }


    // CHECK USER EXIST BY EMAIL
    @Override
    public boolean isUserExistByEmail(String email) {

        return userRepo.findByEmail(email).isPresent();

    }


    // GET ALL USERS
    @Override
    public List<User> getAllUsers() {

        return userRepo.findAll();

    }


    // GET USER BY EMAIL
    @Override
    public User getUserByEmail(String email) {

        return userRepo.findByEmail(email).orElse(null);

    }


    // REQUIRED FOR RESET PASSWORD
    @Override
    public User getUserByEmailToken(String token) {

        return userRepo.findByEmailToken(token).orElse(null);

    }

}