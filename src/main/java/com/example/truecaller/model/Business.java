package com.example.truecaller.model;

import com.example.truecaller.model.common.Contact;
import com.example.truecaller.model.common.PersonalInfo;
import com.example.truecaller.model.common.SocialInfo;
import com.example.truecaller.model.common.Tag;

import java.util.HashMap;
import java.util.Map;

public class Business {
    private String businessName;
    private String businessDescription;
    private Tag tag;
    private BusinessSize businessSize;
    Map<Days,OperatingHours> map = new HashMap<>();
    private Contact contact;
    private PersonalInfo personalInfo;
    private SocialInfo socialInfo;

    public Business(String name, Tag tag) {
        this.businessName = name;
        this.tag = tag;
    }
}
