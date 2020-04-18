package com.adeptj.modules.jaxrs.consumer;

/**
 * UserDTO
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class UserDTO {

    private final Long id;

    private final String firstName;

    private final String lastName;

    private final String email;

    public UserDTO(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
