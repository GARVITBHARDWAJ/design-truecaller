package com.example.truecaller.model;

import com.example.truecaller.exception.ContactsExceededException;
import com.example.truecaller.model.common.Contact;
import com.example.truecaller.model.common.PersonalInfo;
import com.example.truecaller.model.common.SocialInfo;
import com.example.truecaller.model.common.Tag;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public abstract class Account {
    private String id;
    private String phoneNumber;
    private String userName;
    private String password;
    private LocalDateTime lastAccessed;
    private Tag tag;
    private Contact contact;
    private PersonalInfo personalInfo;
    private SocialInfo socialInfo;
    private Business business;
    private UserCategory userCategory;
    private Map<String, User> contacts;
    private CountingBloomFilter<String> blockedContacts;
    private Set<String> blockedSet;
    private ContactTrie contactTrie;

    public Account() {
    }

    public Account(String phoneNumber, String firstName) {
        this.phoneNumber = phoneNumber;
        this.personalInfo  = new PersonalInfo(firstName);
    }

    public Account(String phoneNumber, String firstName, String lastName) {
        this(phoneNumber, firstName);
        this.personalInfo = new PersonalInfo(lastName);
    }

    public abstract void register(UserCategory userCategory, String userName, String password,
                                  String email, String phoneNumber, String countryCode, String firstName);

    public abstract void addContact(User user) throws ContactsExceededException;
    public abstract void removeContract(String number);
    public abstract void blockNumber(String number);
    public abstract void unblockNumber(String number);
    public abstract void reportSpam(String number, String reason);
    public abstract void upgrade(UserCategory userCategory);
    public abstract boolean isBlocked(String number);
    public abstract boolean canReceive(String number);
    public abstract boolean importContracts(List<User> users);

}