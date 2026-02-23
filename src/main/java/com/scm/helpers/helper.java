package com.scm.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class helper {

    public static String getEmailOfLoggedinUser(Authentication authentication) {

        if (authentication == null) return null;

        // ✅ OAuth Login (Google/Github)
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {

            String clientId = oauthToken.getAuthorizedClientRegistrationId();
            OAuth2User oauth2User = oauthToken.getPrincipal();

            String email = null;

            if (clientId.equalsIgnoreCase("google")) {
                System.out.println("Getting Email From Google");
                email = oauth2User.getAttribute("email");
            }
            else if (clientId.equalsIgnoreCase("github")) {
                System.out.println("Getting Email From Github");

                email = oauth2User.getAttribute("email");
                if (email == null) {
                    String login = oauth2User.getAttribute("login");
                    if (login != null) {
                        email = login + "@gmail.com";
                    }
                }
            }

            return email;
        }

        // ✅ Normal Form Login
        System.out.println("Getting Data From Local DB");
        return authentication.getName();
    }

    public static String getLinkForEmailVerification(String emailToken){
        String link = "http://localhost:8081/auth/verify-email?token=" + emailToken;
        
        return link; 
    }

}
