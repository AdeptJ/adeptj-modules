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
import com.adeptj.modules.data.jpa.criteria.TupleCriteria;
import com.adeptj.modules.data.jpa.criteria.UpdateCriteria;
import com.adeptj.modules.data.jpa.dto.CrudDTO;
import com.adeptj.modules.data.jpa.dto.ResultSetMappingDTO;
import com.adeptj.modules.data.jpa.entity.Address;
import com.adeptj.modules.data.jpa.entity.User;
import com.adeptj.modules.data.jpa.query.PositionalParam;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Persistence;
import java.lang.invoke.MethodHandles;
import java.util.List;

import static com.adeptj.modules.data.jpa.QueryType.JPA;
import static com.adeptj.modules.data.jpa.QueryType.NATIVE;

/**
 * JpaCrudRepositoryTest
 *
 * @author Rakesh.Kumar, AdeptJ
 */
//@Disabled
public class JpaRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String UNIT_NAME = "AdeptJ_PU";

    private static EclipseLinkRepository jpaRepository;

    @BeforeAll
    public static void init() {
        jpaRepository = new EclipseLinkRepository();
        jpaRepository.setEntityManagerFactory(Persistence.createEntityManagerFactory(UNIT_NAME));
        LOGGER.info("EntityManagerFactory created!!");
    }

    @AfterAll
    public static void destroy() {
        jpaRepository.closeEntityManagerFactory();
    }

    @Test
    public void testInsert() {
        User usr = new User();
        usr.setContact("1234567893");
        usr.setFirstName("John4");
        usr.setLastName("Doe4");
        usr.setEmail("john.doe4@johndoe.com");
        Address address1 = new Address();
        address1.setCity("Gurugram");
        address1.setState("Haryana");
        address1.setCountry("India");
        address1.setPin("122001");
        Address address2 = new Address();
        address2.setCity("New Delhi ");
        address2.setState("Delhi");
        address2.setCountry("India");
        address2.setPin("110018");
        usr.setAddresses(List.of(address1, address2));
        User user = jpaRepository.insert(usr);
        LOGGER.info("User ID: {}", user.getId());
    }

    @Test
    public void testExecuteInTransaction() {
        User user = jpaRepository.executeInTransaction(em -> {
            User usr = new User();
            usr.setContact("1234567890");
            usr.setFirstName("John");
            usr.setLastName("Doe");
            usr.setEmail("john.doe@johndoe.com");
            em.persist(usr);
            return usr;
        });
        LOGGER.info("User ID: {}", user.getId());
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setId(1L);
        user.setContact("1234567890");
        user.setFirstName("John_Updated_Again");
        user.setLastName("Doe_Updated");
        user.setEmail("john.doe@johndoe.com");
        user = jpaRepository.update(user);
        LOGGER.info("User's Contact No is: {}", user.getContact());
    }

    @Test
    public void testUpdateByCriteria() {
        int rowsUpdated = jpaRepository.updateByCriteria(UpdateCriteria.builder(User.class)
                .addCriteriaAttribute("firstName", "John")
                .addUpdateAttribute("contact", "1234567891")
                .build());
        LOGGER.info("Rows updated: {}", rowsUpdated);
    }

    @Test
    public void testDelete() {
        jpaRepository.delete(User.class, 19L);
    }

    @Test
    public void testDeleteByCriteria() {
        int rows = jpaRepository.deleteByCriteria(DeleteCriteria.builder(User.class)
                .addCriteriaAttribute("contact", "1234567890")
                .build());
        LOGGER.info("Rows deleted: {}", rows);
    }

    @Test
    public void testDeleteByJpaNamedQuery() {
        int rows = jpaRepository.deleteByJpaNamedQuery(CrudDTO.builder(User.class)
                .namedQuery("User.deleteUserByContact.JPA")
                .addQueryParam(new PositionalParam(1, "1234567890"))
                .build());
        LOGGER.info("Rows deleted: {}", rows);
    }

    @Test
    public void testFindByCriteria() {
        List<User> users = jpaRepository.findByCriteria(ReadCriteria.builder(User.class)
                .addCriteriaAttribute("contact", "1234567893")
                .build());
        users.get(0).getAddresses().forEach(address -> LOGGER.info(address.toString()));
        LOGGER.info("Rows found: {}", users.size());
    }

    @Test
    public void testFindById() {
        User user = jpaRepository.findById(User.class, 9L);
        user.getAddresses().forEach(address -> LOGGER.info(address.toString()));
    }

    @Test
    public void testFindByTupleCriteria() {
        jpaRepository.findByTupleCriteria(TupleCriteria.builder(User.class)
                .addSelections("firstName", "lastName")
                .addCriteriaAttribute("contact", "1234567891")
                .build())
                .forEach(tuple -> {
                    LOGGER.info("FirstName: {}", tuple.get(0));
                    LOGGER.info("FirstName: {}", tuple.get(1));
                });
    }

    @Test
    public void testFindByJpaNamedQueryAsUser() {
        jpaRepository.findByJpaNamedQuery(User.class, "User.findUserByContact.JPA.User",
                new PositionalParam(1, "1234567890"))
                .forEach(user -> {
                    LOGGER.info("FirstName: {}", user.getFirstName());
                    LOGGER.info("LastName: {}", user.getLastName());
                });
    }

    @Test
    public void testFindByJpaNamedQueryAsObjectArray() {
        jpaRepository.findByJpaNamedQuery(Object[].class, "User.findUserByContact.JPA.ObjectArray",
                new PositionalParam(1, "1234567890"))
                .forEach(objectArray -> {
                    LOGGER.info("FirstName: {}", objectArray[0]);
                    LOGGER.info("LastName: {}", objectArray[1]);
                });
    }

    @Test
    public void testFindByJPQLNamedQuery() {
        jpaRepository.findByNamedQuery("User.findUserByContact.JPA.User",
                new PositionalParam(1, "1234567890"))
                .forEach(object -> {
                    if (object instanceof User) {
                        LOGGER.info("User!!");
                        User user = (User) object;
                        LOGGER.info("FirstName: {}", user.getFirstName());
                        LOGGER.info("LastName: {}", user.getLastName());
                    } else if (object instanceof Object[]) {
                        LOGGER.info("Object[]!!");
                        Object[] objectArray = (Object[]) object;
                        LOGGER.info("FirstName: {}", objectArray[0]);
                        LOGGER.info("LastName: {}", objectArray[1]);
                    }
                });
    }

    @Test
    public void testFindByNativeNamedQuery() {
        jpaRepository.findByNamedQuery("User.findUserByContact.NATIVE",
                new PositionalParam(1, "1234567890"))
                .forEach(object -> {
                    if (object instanceof Object[]) {
                        Object[] objectArray = (Object[]) object;
                        LOGGER.info("FirstName: {}", objectArray[0]);
                        LOGGER.info("LastName: {}", objectArray[1]);
                    }
                });
    }

    @Test
    public void testFindPaginatedRecords() {
        Long count = jpaRepository.count(User.class);
        LOGGER.info("Total no of users: {}", count);
        int pageSize = count.intValue() / 3;
        this.paginate(0, pageSize);
        this.paginate(pageSize, pageSize);
        this.paginate(pageSize * 2, pageSize);
    }

    private void paginate(int startPos, int pageSize) {
        List<User> users = jpaRepository.findPaginatedRecords(User.class, startPos, pageSize);
        users.forEach(user -> {
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());

        });
    }

    @Test
    public void testFindByJpaQuery() {
        List<User> users = jpaRepository.findByJpaQuery(CrudDTO.builder(User.class)
                .jpaQuery("SELECT u FROM  User u WHERE u.firstName = ?1 and u.contact = ?2")
                .addQueryParams(new PositionalParam(1, "John"),
                        new PositionalParam(2, "1234567890"))
                .build());
        users.forEach(user -> {
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());

        });
    }

    @Test
    public void testGetTypedScalarResultByNamedQueryAndPosParams() {
        String firstName = jpaRepository
                .getScalarResultOfType(String.class, "User.findUserFirstNameByContact.JPA.Scalar",
                        new PositionalParam(1, "123456789167"));
        if (firstName != null) {
            LOGGER.info("FirstName: {}", firstName);
        }
    }

    @Test
    public void testGetScalarResultByNamedQueryAndPosParams() {
        String firstName = jpaRepository
                .getScalarResultOfType(String.class, "User.findUserFirstNameByContact.JPA.Scalar",
                        new PositionalParam(1, "123456789167"));
        if (firstName != null) {
            LOGGER.info("FirstName: {}", firstName);
        }
    }

    @Test
    public void testFindAndMapResultSet() {
        List<User> users = jpaRepository.findByQueryAndMapResultSet(User.class, ResultSetMappingDTO.builder()
                .nativeQuery("SELECT * FROM  Users u WHERE FIRST_NAME = ?1")
                .resultSetMapping("User.findUserByContact.EntityMapping")
                .addQueryParamParam(new PositionalParam(1, "John"))
                .build());
        users.forEach(user -> {
            System.out.printf("User ID: %s", user.getId());
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());
        });
    }

    @Test
    public void testFindAndMapConstructorByQuery() {
        String jpaQuery = "SELECT NEW com.adeptj.modules.data.jpa.UserDTO(u.id, u.firstName, u.lastName, u.email) " +
                "FROM User u WHERE u.contact = ?1";
        jpaRepository.findByQueryAndMapConstructor(UserDTO.class, jpaQuery,
                new PositionalParam(1, "1234567890"))
                .forEach(user -> {
                    LOGGER.info("User ID: {}", user.getId());
                    LOGGER.info("FirstName: {}", user.getFirstName());
                    LOGGER.info("LastName: {}", user.getLastName());
                });
    }

    @Test
    public void testFindAndMapConstructorByCriteria() {
        List<UserDTO> list = jpaRepository.findByCriteriaAndMapConstructor(ConstructorCriteria.builder(User.class, UserDTO.class)
                .addSelections("id", "firstName", "lastName", "email")
                .addCriteriaAttribute("contact", "1234567890")
                .build());
        list.forEach(dto -> {
            LOGGER.info("User ID: {}", dto.getId());
            LOGGER.info("FirstName: {}", dto.getFirstName());
            LOGGER.info("LastName: {}", dto.getLastName());
            LOGGER.info("Email: {}", dto.getEmail());
        });
    }

    @Test
    public void testCountByNativeQuery() {
        Long count = jpaRepository.count("SELECT count(ID) FROM adeptj.USERS", NATIVE);
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testCountByJpaQuery() {
        Long count = jpaRepository.count("SELECT count(u.id) FROM User u", JPA);
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testCountByNamedJpaQuery() {
        Long count = jpaRepository.count("Count.NamedJpaQuery");
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testCountByNamedNativeQuery() {
        Long count = jpaRepository.count("Count.NamedNativeQuery");
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testScalarResultOfTypeJpaQuery() {
        String query = "SELECT u.email FROM User u where u.id= ?1";
        String user = jpaRepository.getScalarResultOfType(String.class, JPA, query,
                new PositionalParam(1, 1L));
        LOGGER.info("User: {}", user);
    }

    @Test
    public void testNamedStoredProcedure() {
        List<User> users = jpaRepository.findByNamedStoredProcedure("allUsers");
        LOGGER.info("Users: {}", users);
    }

    @Test
    public void testStoredProcedure() {
        List<User> users = jpaRepository.findByStoredProcedure("fetchAllUsers", User.class);
        LOGGER.info("Users: {}", users);
    }
}
