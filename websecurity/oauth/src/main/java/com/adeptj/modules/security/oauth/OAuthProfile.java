package com.adeptj.modules.security.oauth;

import java.util.HashMap;

/**
 * OAuthProfile.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class OAuthProfile extends HashMap<String, String> {

    private String firstName;

    private String lastName;

    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addAttribute(String attrKey, String attrVal) {
        this.put(attrKey, attrVal);
    }
}
