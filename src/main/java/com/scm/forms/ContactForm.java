package com.scm.forms;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContactForm {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email address [example@gmail.com]")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "address is required")
    private String address;

    private String description;

    // ✅ NEW FIELD
    private String notes;

    private boolean favorite;

    private String webSiteLink;

    private String linkedInLink;

    private MultipartFile contactImage;

    private String picture;
}
