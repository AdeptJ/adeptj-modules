package com.adeptj.modules.data.jpa.entity;

import com.adeptj.modules.data.jpa.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * User Entity
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Entity
@Table(schema = "AdeptJ", name = "USERS")
@NamedQueries({
        @NamedQuery(name = "User.findUserByAadhaar.JPA",
                query = "SELECT u FROM  User u WHERE u.aadhaar = ?1"),
        @NamedQuery(name = "User.deleteUserByAadhaar.JPA",
                query = "DELETE FROM  User u WHERE u.aadhaar = ?1")
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "User.findUserByAadhaar.NATIVE",
                query = "SELECT * FROM  User WHERE AADHAAR_NUMBER = ?1")
})
public class User implements BaseEntity {

    private static final long serialVersionUID = 1809725039547865373L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FIRST_NAME", length = 25)
    private String firstName;

    @Column(name = "LAST_NAME", length = 25)
    private String lastName;

    @Column(name = "EMAIL", length = 25)
    private String email;

    @Column(name = "MOBILE_NO", length = 25)
    private String contact;

    @Column(name = "AADHAAR_NUMBER", length = 12)
    private String aadhaar;

    @Column(name = "PAN_NUMBER", length = 12)
    private String pan;

    @Override
    public Serializable getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }
}
