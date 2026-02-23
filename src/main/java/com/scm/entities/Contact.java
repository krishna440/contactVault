package com.scm.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Id
    private String id;

    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    private String picture;

    @Column(length = 1000)
    private String description;

    // ✅ NEW FIELD: Notes
    @Column(length = 2000)
    private String notes;

    private boolean favorite = false;

    private String webSiteLink;

    private String linkedInLink;

    public String cloudinaryImagePublicId;

    @ManyToOne
    @JsonIgnore
    private User user;

    @OneToMany(
        mappedBy = "contact",
        cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        orphanRemoval = true
    )
    private List<SocialLink> links = new ArrayList<>();


    // ✅ NEW: Shared users list
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "contact_shared_with",
        joinColumns = @JoinColumn(name = "contact_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
)
private List<User> sharedWithUsers = new ArrayList<>();

@OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
private List<Reminder> reminders = new ArrayList<>();

}
