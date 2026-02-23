package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(OAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

                logger.info("OAuthenticationSuccessHandler");


                // Identify the Provider

                var oauthAuthenticationToken = (OAuth2AuthenticationToken) authentication;

                String authorizedClientRegsitrationId= oauthAuthenticationToken.getAuthorizedClientRegistrationId();

                logger.info(authorizedClientRegsitrationId);
                
                var OauthUser = (DefaultOAuth2User)authentication.getPrincipal();

                OauthUser.getAttributes().forEach((key,value)->{
                    logger.info(key + " : " + value);
                });

                User user = new User();
                user.setUserId(UUID.randomUUID().toString());
                user.setRoleList(List.of(AppConstants.ROLE_USER));
                user.setEmailVerified(true);
                user.setEnabled(true);
                



                if(authorizedClientRegsitrationId.equalsIgnoreCase("google")){
                    // Google
                    // google attributes

                    String email = OauthUser.getAttribute("email") != null
                    ? OauthUser.getAttribute("email").toString()
                    : null;
                    if(email == null){
                        throw new RuntimeException("Email not found from OAuth Provider");
                    }
                    user.setEmail(email);

                    user.setProfilePic(OauthUser.getAttribute("picture").toString());
                    user.setName(OauthUser.getAttribute("name").toString());
                    user.setProviderUserId(OauthUser.getName());
                    user.setProvider(Providers.GOOGLE);
                    user.setPassword("admin");
                    user.setAbout("Account Is Created By GOOGLE");
                }

                else if (authorizedClientRegsitrationId.equalsIgnoreCase("github")) {
                    // Github
                    // github attributes

                    String email = OauthUser.getAttribute("email") !=null ? OauthUser.getAttribute("email").toString() :
                    OauthUser.getAttribute("login").toString() + "@gmail.com"; 
                    String picture = OauthUser.getAttribute("avatar_url").toString();

                    String name = OauthUser.getAttribute("login").toString();

                    String providerUserId = OauthUser.getName();

                    user.setEmail(email);
                    user.setProfilePic(picture);
                    user.setName(name);
                    user.setProviderUserId(providerUserId);
                    user.setProvider(Providers.GITHUB);
                    user.setPassword("admin");
                    user.setAbout("Account Is Created By GITHUB");
                }

                else if (authorizedClientRegsitrationId.equalsIgnoreCase("linkedin")) {
                    //linkedin
                }

                else{
                    logger.info("OAuthenticationSuccessHandler: Unknown Provider");
                }


                /* 

                DefaultOAuth2User user = (DefaultOAuth2User)authentication.getPrincipal();

                // user.getAttributes().forEach((key, value)->{
                //     logger.info("{} => {}", key, value);
                // });

                // logger.info(user.getAuthorities().toString());

                String email = user.getAttribute("email").toString();
                String name = user.getAttribute("name").toString();
                String picture = user.getAttribute("picture").toString();

                // create user and save in DB

                User user1 = new User();
                user1.setEmail(email);
                user1.setName(name);
                user1.setProfilePic(picture);
                user1.setPassword("password");
                user1.setUserId(UUID.randomUUID().toString());
                user1.setProvider(Providers.GOOGLE);
                user1.setEnabled(true);
                user1.setEmailVerified(true);
                user1.setProviderUserId(user.getName());
                user1.setRoleList(List.of(AppConstants.ROLE_USER));
                user1.setAbout("This Account is Created Using Google");
                logger.info("User Saved: " + email);
               }
               */


               User user2 = userRepo.findByEmail(user.getEmail()).orElse(null);
               if (user2 == null) {
                userRepo.save(user);
                logger.info("New user saved: " + user.getEmail());
            } else {
                logger.info("User already exists: " + user.getEmail());
            }
            new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
        }
}
