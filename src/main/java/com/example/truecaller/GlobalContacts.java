package com.example.truecaller;

import com.example.truecaller.model.common.GlobalSpam;
import com.example.truecaller.model.tries.ContactTrie;
import lombok.Getter;

public class GlobalContacts {
    private GlobalContacts() {}
    public static GlobalContacts INSTANCE = new GlobalContacts();
    @Getter
    private ContactTrie contactTrie = new ContactTrie();
}
