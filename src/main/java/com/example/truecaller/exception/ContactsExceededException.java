package com.example.truecaller.exception;

public class ContactsExceededException extends Throwable {
    public ContactsExceededException(String defaultContactSizeExceeded) {
        super(defaultContactSizeExceeded);
    }
}
