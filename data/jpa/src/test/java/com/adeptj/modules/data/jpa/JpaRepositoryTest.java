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

package com.adeptj.modules.data.jpa;

import com.adeptj.modules.data.jpa.criteria.ConstructorCriteria;
import com.adeptj.modules.data.jpa.criteria.DeleteCriteria;
import com.adeptj.modules.data.jpa.criteria.ReadCriteria;
import com.adeptj.modules.data.jpa.criteria.TupleQueryCriteria;
import com.adeptj.modules.data.jpa.criteria.UpdateCriteria;
import com.adeptj.modules.data.jpa.dto.CrudDTO;
import com.adeptj.modules.data.jpa.dto.ResultSetMappingDTO;
import com.adeptj.modules.data.jpa.entity.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

/**
 * JpaCrudRepositoryTest
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Disabled
public class JpaRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;

    private static EclipseLinkRepository repository;

    @BeforeAll
    public static void init() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("AdeptJ_PU");
        repository = new EclipseLinkRepository();
        repository.setEntityManagerFactory(entityManagerFactory);
    }

    @AfterAll
    public static void destroy() {
        entityManagerFactory.close();
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setContact("1234567890");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@johndoe.com");
        user = repository.insert(user);
        System.out.println("User ID: " + user.getId());
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setId(1L);
        user.setContact("1234567890");
        user.setFirstName("John_Updated");
        user.setLastName("Doe");
        user.setEmail("john.doe@johndoe.com");
        user = repository.update(user);
        System.out.println("User's Contact No is: " + user.getContact());
    }

    @Test
    public void testUpdateByCriteria() {
        int rowsUpdated = repository.updateByCriteria(UpdateCriteria.builder(User.class)
                .addCriteriaAttribute("firstName", "John")
                .addUpdateAttribute("contact", "1234567891")
                .build());
        System.out.println("Rows updated: " + rowsUpdated);
    }

    @Test
    public void testDelete() {
        repository.delete(User.class, 19L);
    }

    @Test
    public void testDeleteByCriteria() {
        int rows = repository.deleteByCriteria(DeleteCriteria.builder(User.class)
                .addCriteriaAttribute("contact", "1234567890")
                .build());
        System.out.println("Rows deleted: " + rows);
    }

    @Test
    public void testDeleteByJpaNamedQuery() {
        int rows = repository.deleteByJpaNamedQuery(CrudDTO.builder(User.class)
                .namedQuery("User.deleteUserByContact.JPA")
                .addPosParam("1234567890")
                .build());
        System.out.println("Rows deleted: " + rows);
    }

    @Test
    public void testFindByCriteria() {
        List<User> users = repository.findByCriteria(ReadCriteria.builder(User.class)
                .addCriteriaAttribute("contact", "1234567890")
                .build());
        System.out.println("Rows found: " + users.size());
    }

    @Test
    public void testFindByTupleQuery() {
        repository.findByTupleQuery(TupleQueryCriteria.builder(User.class)
                .addSelections("firstName", "lastName")
                .addCriteriaAttribute("contact", "1234567890")
                .build()).forEach(tuple -> {
            System.out.println("FirstName: " + tuple.get(0));
            System.out.println("LastName: " + tuple.get(1));
        });
    }

    @Test
    public void testFindByJpaNamedQueryAsUser() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("1234567890");
        repository.findByJpaNamedQuery(User.class, "User.findUserByContact.JPA.User", posParams)
                .forEach(user -> {
                    System.out.println("FirstName: " + user.getFirstName());
                    System.out.println("LastName: " + user.getLastName());
                });
    }

    @Test
    public void testFindByJpaNamedQueryAsObjectArray() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("1234567890");
        repository.findByJpaNamedQuery(Object[].class, "User.findUserByContact.JPA.ObjectArray", posParams)
                .forEach(objectArray -> {
                    System.out.println("FirstName: " + objectArray[0]);
                    System.out.println("LastName: " + objectArray[1]);
                });
    }

    @Test
    public void testFindByJPQLNamedQuery() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("1234567890");
        repository.findByNamedQuery("User.findUserByContact.JPA.User", posParams)
                .forEach(object -> {
                    if (object instanceof User) {
                        System.out.println("User!!");
                        User user = (User) object;
                        System.out.println("FirstName: " + user.getFirstName());
                        System.out.println("LastName: " + user.getLastName());
                    } else if (object instanceof Object[]) {
                        System.out.println("Object[]!!");
                        Object[] objectArray = (Object[]) object;
                        System.out.println("FirstName: " + objectArray[0]);
                        System.out.println("LastName: " + objectArray[1]);
                    }
                });
    }

    @Test
    public void testFindByNativeNamedQuery() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("1234567890");
        repository.findByNamedQuery("User.findUserByContact.NATIVE", posParams)
                .forEach(object -> {
                    if (object instanceof Object[]) {
                        Object[] objectArray = (Object[]) object;
                        System.out.println("FirstName: " + objectArray[0]);
                        System.out.println("LastName: " + objectArray[1]);
                    }
                });
    }

    @Test
    public void testFindPaginatedRecords() {
        Long count = repository.count(User.class);
        System.out.println("Total no of users: " + count);
        int pageSize = count.intValue() / 3;
        this.paginate(0, pageSize);
        this.paginate(pageSize, pageSize);
        this.paginate(pageSize * 2, pageSize);
    }

    private void paginate(int startPos, int pageSize) {
        List<User> users = repository.findPaginatedRecords(User.class, startPos, pageSize);
        users.forEach(user -> {
            System.out.println("FirstName: " + user.getFirstName());
            System.out.println("LastName: " + user.getLastName());

        });
    }

    @Test
    public void testFindByJpaQuery() {
        List<User> users = repository.findByJpaQuery(CrudDTO.builder(User.class)
                .jpaQuery("SELECT u FROM  User u WHERE u.firstName = ?1 and u.contact = ?2")
                .addPosParams("John", "1234567890")
                .build());
        users.forEach(user -> {
            System.out.println("FirstName: " + user.getFirstName());
            System.out.println("LastName: " + user.getLastName());

        });
    }

    @Test
    public void testGetTypedScalarResultByNamedQueryAndPosParams() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("123456789167");
        String firstName = repository
                .getScalarResultOfType(String.class, "User.findUserFirstNameByContact.JPA.Scalar", posParams);
        if (firstName != null) {
            System.out.println("FirstName: " + firstName);
        }
    }

    @Test
    public void testGetScalarResultByNamedQueryAndPosParams() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("123456789167");
        String firstName = repository
                .getScalarResultOfType(String.class, "User.findUserFirstNameByContact.JPA.Scalar", posParams);
        if (firstName != null) {
            System.out.println("FirstName: " + firstName);
        }
    }

    @Test
    public void testFindAndMapResultSet() {
        List<User> users = repository.findByQueryAndMapResultSet(User.class, ResultSetMappingDTO.builder()
                .nativeQuery("SELECT * FROM  Users u WHERE FIRST_NAME = ?1")
                .resultSetMapping("User.findUserByContact.EntityMapping")
                .addPosParam("John")
                .build());
        users.forEach(user -> {
            System.out.println("User ID: " + user.getId());
            System.out.println("FirstName: " + user.getFirstName());
            System.out.println("LastName: " + user.getLastName());
        });
    }

    @Test
    public void testFindAndMapConstructorByQuery() {
        String jpaQuery = "SELECT NEW com.adeptj.modules.data.jpa.UserDTO(u.id, u.firstName, u.lastName, u.email) " +
                "FROM User u WHERE u.contact = ?1";
        List<Object> posParams = new ArrayList<>();
        posParams.add("1234567890");
        repository.findByQueryAndMapConstructor(UserDTO.class, jpaQuery, posParams)
                .forEach(user -> {
                    System.out.println("User ID: " + user.getId());
                    System.out.println("FirstName: " + user.getFirstName());
                    System.out.println("LastName: " + user.getLastName());

                });
    }

    @Test
    public void testFindAndMapConstructorByCriteria() {
        List<UserDTO> list = repository.findByCriteriaAndMapConstructor(ConstructorCriteria.builder(User.class, UserDTO.class)
                .addSelections("id", "firstName", "lastName", "email")
                .addCriteriaAttribute("contact", "1234567890")
                .build());
        list.forEach(dto -> {
            System.out.println("User ID: " + dto.getId());
            System.out.println("FirstName: " + dto.getFirstName());
            System.out.println("LastName: " + dto.getLastName());

        });
    }
}
