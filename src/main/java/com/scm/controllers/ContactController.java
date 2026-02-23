package com.scm.controllers;

import java.util.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.services.ContactService;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.helpers.helper;
import com.scm.services.ImageService;
import com.scm.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/user/contacts")
public class ContactController {


    private Logger logger = org.slf4j.LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    // Add contact Page Handler

    @RequestMapping("/add")
    public String addContactView(Model model){

        ContactForm contactForm = new ContactForm();
        contactForm.setFavorite(true);
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session) {

        // process the form data

        // 1 validate form

        if (result.hasErrors()) {

            result.getAllErrors().forEach(error -> logger.info(error.toString()));

            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }
        String  username = helper.getEmailOfLoggedinUser(authentication);

        User user = userService.getUserByEmail(username);
        // 2 process the contact picture

        // image process

        // uplod karne ka code
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavorite(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setNotes(contactForm.getNotes());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebSiteLink(contact.getWebSiteLink() );

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);

        }
        contactService.save(contact);
        System.out.println(contactForm);

        // 3 set the contact picture url

        // 4 `set message to be displayed on the view

        session.setAttribute("message",
                Message.builder()
                        .content("You have successfully added a new contact")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts/add";

    }


    // view contacts

    @RequestMapping()
    public String viewContactsModel(
        @RequestParam(value =  "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
        @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
        @RequestParam(value = "direction", defaultValue = "asc") String direction,
        Model model ,Authentication authentication){

        String username = helper.getEmailOfLoggedinUser(authentication);

        User user = userService.getUserByEmail(username);

        Page<Contact> pageContact= contactService.getByUser(user,page,size,sortBy,direction);

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        model.addAttribute("contactSearchForm", new ContactSearchForm());
        return "user/contacts";

    }

    //search Handler

    @RequestMapping("/search")
    public String searchHandler(
        @ModelAttribute ContactSearchForm contactSearchForm,
        @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
        @RequestParam(value = "direction", defaultValue = "asc") String direction,
        Model model,
        Authentication authentication
    ){

        logger.info("field {} keyword {}", contactSearchForm.getField(), contactSearchForm.getValue());

        var user = userService.getUserByEmail(helper.getEmailOfLoggedinUser(authentication));

        Page<Contact> pageContact = null;
        if(contactSearchForm.getField().equalsIgnoreCase("name")){
            pageContact = contactService.searchByName(contactSearchForm.getValue(),size,page,sortBy, direction, user);
        }

        else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(contactSearchForm.getValue(),size,page,sortBy,direction, user);
        }

        else if (contactSearchForm.getField().equalsIgnoreCase("phoneNumber")) {
            pageContact = contactService.searchByPhoneNumber(contactSearchForm.getValue(),size,page,sortBy,direction, user);
        }

        logger.info("pageContact {}", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        return "user/search";
    } 

    // delete Contact
    @RequestMapping("/delete/{contactId}")
    public String deleteContact( @PathVariable("contactId") String contactId,
            HttpSession session
        ){

        contactService.delete(contactId);
        logger.info("contactId  {} deleted", contactId);

        session.setAttribute("message",
            Message.builder()
            .content("Contact Deleted Successfully !!")
            .type(MessageType.green)
            .build()
        );
        return "redirect:/user/contacts";
    }

    // update contact form view

    @GetMapping("/view/{contactId}")
    public String updateContactFormView(
        @PathVariable("contactId") String contactId, Model model){
            var contact = contactService.getById(contactId);

            ContactForm contactForm =  new ContactForm();
            contactForm.setName(contact.getName());
            contactForm.setEmail(contact.getEmail());
            contactForm.setPhoneNumber(contact.getPhoneNumber());
            contactForm.setAddress(contact.getAddress());
            contactForm.setDescription(contact.getDescription());
            contactForm.setNotes(contact.getNotes());
            contactForm.setFavorite(contact.isFavorite());
            contactForm.setWebSiteLink(contact.getWebSiteLink());
            contactForm.setLinkedInLink(contact.getLinkedInLink());
            contactForm.setPicture(contact.getPicture());

            model.addAttribute("contactForm", contactForm);
            model.addAttribute("contactId", contactId);

    return "user/update_contact_view";
    }

    @PostMapping("/share/{contactId}")
public String shareContact(
    @PathVariable String contactId,
    @RequestParam String email,
    HttpSession session
) {

    User userToShare = userService.getUserByEmail(email);

    contactService.shareContact(contactId, userToShare);

    session.setAttribute("message",
        Message.builder()
            .content("Contact shared successfully!")
            .type(MessageType.green)
            .build());

    return "redirect:/user/contacts";
}


    @RequestMapping(value = "/update/{contactId}",method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId,@Valid @ModelAttribute ContactForm contactForm,
    BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()) {
            return "user/update_contact_view";
        }

        var con = contactService.getById(contactId);


        con.setId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getDescription());
        con.setNotes(contactForm.getNotes());
        con.setFavorite(contactForm.isFavorite());
        con.setWebSiteLink(con.getWebSiteLink());
        con.setLinkedInLink(contactForm.getLinkedInLink());
        
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            logger.info("file is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            con.setCloudinaryImagePublicId(fileName);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);

        } else {
            logger.info("file is empty");
        }


        var updateCon = contactService.update(con);
        logger.info("updated contact {}", updateCon);
        model.addAttribute("message", Message.builder().content("Contact Updated !!").type(MessageType.green).build());
        return "redirect:/user/contacts/view/" + contactId;
    }

    // SHOW SHARED CONTACTS
@GetMapping("/shared")
public String viewSharedContacts(
        Authentication authentication,
        Model model
) {

    String username =
            helper.getEmailOfLoggedinUser(authentication);

    User user =
            userService.getUserByEmail(username);

    List<Contact> sharedContacts =
            contactService.getSharedContacts(user);

    model.addAttribute("sharedContacts", sharedContacts);

    return "user/shared_contacts";
}

}

