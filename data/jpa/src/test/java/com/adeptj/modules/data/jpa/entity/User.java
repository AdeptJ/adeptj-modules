/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.data.jpa.entity;

import com.adeptj.modules.data.jpa.BaseEntity;
import com.adeptj.modules.data.jpa.UserDTO;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
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

        @NamedQuery(name = "User.findUserByContact.JPA.Scalar",
                query = "SELECT u FROM  User u WHERE u.contact = ?1"),

        @NamedQuery(name = "User.findUserByContact.JPA.User",
                query = "SELECT u FROM  User u WHERE u.contact = ?1"),

        @NamedQuery(name = "User.findUserByContact.JPA.ObjectArray",
                query = "SELECT u.firstName, u.lastName FROM  User u WHERE u.contact = ?1"),

        @NamedQuery(name = "User.deleteUserByContact.JPA",
                query = "DELETE FROM  User u WHERE u.contact = ?1")
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "User.findUserByContact.NATIVE",
                query = "SELECT u.FIRST_NAME, u.LAST_NAME FROM  Users u WHERE MOBILE_NO = ?1")
})
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "User.findUserByContact.EntityMapping",
                entities = {
                        @EntityResult(
                                entityClass = User.class,
                                fields = {
                                        @FieldResult(name = "id", column = "ID"),
                                        @FieldResult(name = "firstName", column = "FIRST_NAME"),
                                        @FieldResult(name = "lastName", column = "LAST_NAME"),
                                        @FieldResult(name = "email", column = "EMAIL"),
                                        @FieldResult(name = "contact", column = "MOBILE_NO"),
                                })
                }
        ),
        @SqlResultSetMapping(
                name = "User.findUserByContact.ConstructorMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = UserDTO.class,
                                columns = {
                                        @ColumnResult(name = "ID"),
                                        @ColumnResult(name = "FIRST_NAME"),
                                        @ColumnResult(name = "LAST_NAME"),
                                        @ColumnResult(name = "EMAIL"),
                                        @ColumnResult(name = "MOBILE_NO"),
                                })
                }
        )
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
}
