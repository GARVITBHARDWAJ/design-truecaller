package com.example.truecaller.model;

import com.example.truecaller.exception.BlockLimitExceededException;
import com.example.truecaller.exception.ContactDoesNotExistexception;
import com.example.truecaller.exception.ContactsExceededException;
import com.example.truecaller.model.common.Contact;
import com.example.truecaller.model.common.GlobalSpam;
import com.example.truecaller.model.common.PersonalInfo;
import orestes.bloomfilter.FilterBuilder;

import static com.example.truecaller.model.common.Constant.*;

import java.util.*;

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
        int count = 0;
        int blockedCount = 0;
        switch (userCategory) {
            case GOLD:
                count = MAX_FREE_USER_CONTACTS;
                blockedCount = MAX_GOLD_USER_BLOCKED_CONTACTS;
                break;
            case PLATINUM:
                count = MAX_PLATINUM_USER_CONTACTS;
                blockedCount = MAX_PLATINUM_USER_BLOCKED_CONTACTS;
                break;
        }
        upgradeContacts(count);
        upgradeBlockedContact(blockedCount);
    }

    private void upgradeBlockedContact(int blockedCount) {
        setBlockedContacts(new FilterBuilder(blockedCount, .01)
                .buildCountingBloomFilter());
        Set<String> upgradedSet = new HashSet<>();
        for (String blocked : getBlockedSet()) {
            upgradedSet.add(blocked);
            getBlockedContacts().add(blocked);
        }
    }

    private void upgradeContacts(int count) {
        Map<String,User> upgradedContacts = new HashMap<>(count);
        for (Map.Entry<String, User> entry : getContacts().entrySet()) {
            upgradedContacts.putIfAbsent(entry.getKey(), entry.getValue());
        }
        setContacts(upgradedContacts);
    }

    @Override
    public boolean isBlocked(String number) {
        return getBlockedContacts().contains(number);
    }

    @Override
    public boolean canReceive(String number) {
        return !isBlocked(number) && !GlobalSpam.INSTANCE.isGlobalSpam(number);
    }

    @Override
    public boolean importContracts(List<User> users) {
        for(User user : users) {
            try {
                addContact(user);
            } catch (ContactsExceededException cee) {
                System.out.println("Some of the contact could not be imported as limit exceeded");
                return false;
            }
        }
        return true;
    }
}
