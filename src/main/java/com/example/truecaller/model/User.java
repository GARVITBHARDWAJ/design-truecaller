package com.example.truecaller.model;

import com.example.truecaller.exception.BlockLimitExceededException;
import com.example.truecaller.exception.ContactDoesNotExistexception;
import com.example.truecaller.exception.ContactsExceededException;
import com.example.truecaller.model.common.Contact;
import com.example.truecaller.model.common.GlobalSpam;
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
        getContactTrie().insert(phoneNumber);
        getContactTrie().insert(firstName);
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
    public void removeContract(String number) throws ContactDoesNotExistexception {
        User contact = getContacts().get(number);
        if(contact == null) {
            throw new ContactDoesNotExistexception("Contact Does not exist");
        }
        getContacts().remove(number);
        getContactTrie().delete(number);
        getContactTrie().delete(getPersonalInfo().getFirstName());
    }

    @Override
    public void blockNumber(String number) throws BlockLimitExceededException {
        checkBlockUser();
        getBlockedContacts().add(number);
    }

    private void checkBlockUser() throws BlockLimitExceededException {
        switch(this.getUserCategory()) {
            case FREE:
                if(this.getContacts().size() >= MAX_FREE_USER_BLOCKED_CONTACTS) {
                    throw new BlockLimitExceededException("exceeded max contacts to be blocked");
                }
            case GOLD:
                if(this.getContacts().size() >= MAX_GOLD_USER_BLOCKED_CONTACTS) {
                    throw new BlockLimitExceededException("exceeded max contacts to be blocked");
                }
            case PLATINUM:
                if(this.getContacts().size() >= MAX_PLATINUM_USER_BLOCKED_CONTACTS) {
                    throw new BlockLimitExceededException("exceeded max contacts to be blocked");
                }
        }
    }

    @Override
    public void unblockNumber(String number) {
        getBlockedContacts().remove();
    }

    @Override
    public void reportSpam(String number, String reason) {
        getBlockedContacts().add(number);
        GlobalSpam.INSTANCE.reportSpam(number, this.getPhoneNumber(), reason);
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
