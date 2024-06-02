package com.example.truecaller.exception;

public class ContactDoesNotExistexception extends Throwable {
    public ContactDoesNotExistexception(String contactDoesNotExist) {
        super(contactDoesNotExist);
    }
}
