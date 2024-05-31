package com.example.truecaller.model;

import com.example.truecaller.exception.ContactsExceededException;
import com.example.truecaller.model.common.Contact;
import com.example.truecaller.model.common.PersonalInfo;
import static com.example.truecaller.model.common.Constant.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class User extends Account {

    @Override
    public void register(UserCategory userCategory, String userName, String password, String email,
                         String phoneNumber, String countryCode, String firstName) {
        setId(UUID.randomUUID().toString());
        setUserCategory(userCategory);
        setUserName(userName);
        setPassword(password);
        setContact(new Contact(phoneNumber, email, countryCode));
        setPersonalInfo(new PersonalInfo(firstName));
        init(userCategory);
        insertToTries(phoneNumber, firstName);
    }

    private void init(UserCategory userCategory) {
        switch (userCategory) {
            case FREE:
                setContacts(new HashMap<>(MAX_FREE_USER_CONTACTS));
            case GOLD:
                setContacts(new HashMap<>(MAX_GOLD_USER_CONTACTS));
            case PLATINUM:
                setContacts(new HashMap<>(MAX_PLATINUM_USER_CONTACTS));
        }
    }

    @Override
    public void addContact(User user) throws ContactsExceededException {
        checkAddUser();
        getContacts().putIfAbsent(user.getPhoneNumber(), user);
        insertToTries(user.getPhoneNumber(), user.getPersonalInfo().getFirstName());
    }

    private void insertToTries(String phoneNumber, String firstName) {
        //TBD
    }

    private void checkAddUser() throws ContactsExceededException {
        switch(this.getUserCategory()) {
            case FREE:
                if(this.getContacts().size() >= MAX_FREE_USER_CONTACTS) {
                    throw new ContactsExceededException("Default contact size exceeded");
                }
        }
    }

    @Override
    public void removeContract(String number) {

    }

    @Override
    public void blockNumber(String number) {

    }

    @Override
    public void unblockNumber(String number) {

    }

    @Override
    public void reportSpam(String number, String reason) {

    }

    @Override
    public void upgrade(UserCategory userCategory) {

    }

    @Override
    public boolean isBlocked(String number) {
        return false;
    }

    @Override
    public boolean canReceive(String number) {
        return false;
    }

    @Override
    public boolean importContracts(List<User> users) {
        return false;
    }
}
