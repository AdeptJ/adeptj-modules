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
import com.adeptj.modules.data.jpa.query.NamedParam;
import com.adeptj.modules.data.jpa.query.PositionalParam;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adeptj.modules.data.jpa.JpaConstants.SYS_PROP_ENABLE_EXCEPTION_HANDLER_LOGGING;
import static com.adeptj.modules.data.jpa.query.QueryType.JPA;
import static com.adeptj.modules.data.jpa.query.QueryType.NATIVE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SESSION_CUSTOMIZER;

/**
 * JpaCrudRepositoryTest
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Disabled
public class UserRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static UserRepository repository;

    static {
        System.setProperty(SYS_PROP_ENABLE_EXCEPTION_HANDLER_LOGGING, "true");
    }

    //@BeforeAll
    public static void initHibernateEntityManagerFactoryPostgres() {
        String unitName = "AdeptJ_PU_Postgres_Hibernate";
        repository = new UserRepository();
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.nonJtaDataSource", getDataSource());
        EntityManagerFactory entityManagerFactory = null;
        try {
            entityManagerFactory = new HibernatePersistenceProvider().createEntityManagerFactory(unitName, properties);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        repository.setEntityManagerFactory(entityManagerFactory);
        LOGGER.info("EntityManagerFactory created!!");
    }

    public static void initEclipseLinkEntityManagerFactoryMySQL() {
        String unitName = "AdeptJ_PU_MySQL_EclipseLink";
        repository = new UserRepository();
        EntityManagerFactory entityManagerFactory = null;
        try {
            entityManagerFactory = new PersistenceProvider().createEntityManagerFactory(unitName, new HashMap<>());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        repository.setEntityManagerFactory(entityManagerFactory);
        LOGGER.info("EntityManagerFactory created!!");
    }

    @BeforeAll
    public static void initEclipseLinkEntityManagerFactoryPostgres() {
        String unitName = "AdeptJ_PU_Postgres_EclipseLink";
        repository = new UserRepository();
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.nonJtaDataSource", getDataSource());
        properties.put(SESSION_CUSTOMIZER, new PostgresCustomizer());
        EntityManagerFactory entityManagerFactory = null;
        try {
            entityManagerFactory = new PersistenceProvider().createEntityManagerFactory(unitName, properties);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        repository.setEntityManagerFactory(entityManagerFactory);
        LOGGER.info("EntityManagerFactory created!!");
    }

    @NotNull
    private static PGSimpleDataSource getDataSource() {
        PGSimpleDataSource ds = new MyPGSimpleDataSource(false);
        ds.setServerNames(null);
        ds.setPortNumbers(null);
        ds.setUser("postgres");
        ds.setPassword("postgres");
        ds.setDatabaseName("JPA");
        return ds;
    }

    @AfterAll
    public static void destroy() {
        repository.closeEntityManagerFactory();
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
        User user = repository.insert(usr);
        LOGGER.info("User ID: {}", user.getId());
    }

    @Test
    public void testBatchInsert() {
        List<User> users = new ArrayList<>();
        for (int i = 51; i < 101; i++) {
            User usr = new User();
            usr.setContact("1234567893" + (i + 50));
            usr.setFirstName("John" + i);
            usr.setLastName("Doe" + 1);
            usr.setEmail(i + "john.doe@johndoe.com");
            users.add(usr);
        }
        repository.batchInsert(users, 5);
    }

    @Test
    public void testExecuteInTransaction() {
        User user = repository.executeCallbackInTransaction(em -> {
            User usr = new User();
            usr.setContact("12345678915");
            usr.setFirstName("John");
            usr.setLastName("Doe");
            usr.setEmail("john.doe15@johndoe.com");
            em.persist(usr);
            return usr;
        });
        LOGGER.info("User ID: {}", user.getId());
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setId(7L);
        user.setContact("1234567890");
        user.setFirstName("John_Updated_Again_");
        user.setLastName("Doe_Updated");
        user.setEmail("john.doe1@johndoe.com");
        user = repository.update(user);
        LOGGER.info("User's Contact No is: {}", user.getContact());
    }

    @Test
    public void testUpdateByCriteria() {
        int rowsUpdated = repository.updateByCriteria(UpdateCriteria.builder(User.class)
                .addCriteriaAttribute("firstName", "John")
                .addUpdateAttribute("contact", "1234567891")
                .build());
        LOGGER.info("Rows updated: {}", rowsUpdated);
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
        LOGGER.info("Rows deleted: {}", rows);
    }

    @Test
    public void testDeleteByJpaNamedQuery() {
        int rows = repository.deleteByJpaNamedQuery(CrudDTO.builder(User.class)
                .namedQueryName("User.deleteUserByContact.JPA")
                .queryParam(new PositionalParam(1, "1234567890"))
                .build());
        LOGGER.info("Rows deleted: {}", rows);
    }

    @Test
    public void testFindByCriteria() {
        List<User> users = repository.findByCriteria(ReadCriteria.builder(User.class)
                .addCriteriaAttribute("contact", "1234567893")
                .build());
        users.get(0).getAddresses().forEach(address -> LOGGER.info(address.toString()));
        LOGGER.info("Rows found: {}", users.size());
    }

    @Test
    public void testFindById() {
        User user = repository.findById(User.class, 9L);
        user.getAddresses().forEach(address -> LOGGER.info(address.toString()));
    }

    @Test
    public void testFindByTupleCriteria() {
        repository.findByTupleCriteria(TupleCriteria.builder(User.class)
                .addSelections("firstName", "lastName")
                .addCriteriaAttribute("contact", "1234567892")
                .build())
                .forEach(tuple -> {
                    LOGGER.info("FirstName: {}", tuple.get(0));
                    LOGGER.info("FirstName: {}", tuple.get(1));
                });
    }

    @Test
    public void testFindByNamedQueryAsUser() {
        repository.findByNamedQuery(User.class, "User.findUserByContact.JPA.User",
                new PositionalParam(1, "1234567895"))
                .forEach(user -> {
                    LOGGER.info("FirstName: {}", user.getFirstName());
                    LOGGER.info("LastName: {}", user.getLastName());
                });
    }

    @Test
    public void testFindByNamedQueryAsObjectArray() {
        repository.findByNamedQuery(Object[].class, "User.findUserByContact.NATIVE",
                new PositionalParam(1, "1234567892"))
                .forEach(objectArray -> {
                    LOGGER.info("FirstName: {}", objectArray[0]);
                    LOGGER.info("LastName: {}", objectArray[1]);
                });
    }

    @Test
    public void testFindPaginatedRecords() {
        Long count = repository.countByCriteria(User.class);
        LOGGER.info("Total no of users: {}", count);
        int pageSize = count.intValue() / 3;
        this.paginate(0, pageSize);
        this.paginate(pageSize, pageSize);
        this.paginate(pageSize * 2, pageSize);
    }

    private void paginate(int startPos, int pageSize) {
        List<User> users = repository.findWithPagination(User.class, startPos, pageSize);
        users.forEach(user -> {
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());

        });
    }

    @Test
    public void testFindByJpaQuery() {
        List<User> users = repository.findByJpaQuery(CrudDTO.builder(User.class)
                .jpaQuery("SELECT u FROM  User u WHERE u.firstName = :firstName and u.contact = :contact")
                .queryParams(new NamedParam("firstName", "John3"),
                        new NamedParam("contact", "1234567892"))
                .build());
        users.forEach(user -> {
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());
        });
    }

    @Test
    public void testFindAll() {
        List<User> users = repository.findAllUsers();
        users.forEach(user -> {
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());
        });
    }

    @Test
    public void testFindByNativeQuery() {
        List<User> users = repository.findByNativeQuery(User.class,
                "SELECT u.ID, u.FIRST_NAME, u.LAST_NAME FROM Users u WHERE MOBILE_NO = ?1",
                new PositionalParam(1, "1234567892"));
        users.forEach(user -> {
            System.out.printf("User ID: %s", user.getId());
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());
        });
    }

    @Test
    public void testFindAndMapResultSet() {
        List<User> users = repository.findByNativeQuery(User.class, ResultSetMappingDTO.builder()
                .nativeQuery("SELECT * FROM  Users u WHERE FIRST_NAME = ?1")
                .resultSetMapping("User.findUserByContact.EntityMapping")
                .queryParam(new PositionalParam(1, "John"))
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
        repository.findByJpaQueryWithDTOProjection(UserDTO.class, jpaQuery,
                new PositionalParam(1, "1234567892"))
                .forEach(user -> {
                    LOGGER.info("User ID: {}", user.getId());
                    LOGGER.info("FirstName: {}", user.getFirstName());
                    LOGGER.info("LastName: {}", user.getLastName());
                });
    }

    @Test
    public void testFindAndMapConstructorByCriteria() {
        List<UserDTO> list = repository.findByCriteriaWithDTOProjection(ConstructorCriteria.builder(User.class, UserDTO.class)
                .addSelections("id", "firstName", "lastName", "email")
                .addCriteriaAttribute("contact", "1234567892")
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
        Long count = repository.countByQuery("SELECT count(ID) FROM USERS", NATIVE);
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testCountByJpaQuery() {
        Long count = repository.countByQuery("SELECT count(u.id) FROM User u", JPA);
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testCountByNamedJpaQuery() {
        Long count = repository.countByNamedQuery("Count.NamedJpaQuery");
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testCountByNamedNativeQuery() {
        Long count = repository.countByNamedQuery("Count.NamedNativeQuery");
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testFindSingleResultByJpaQuery() {
        String query = "SELECT u.email FROM User u where u.id= ?1";
        String user = repository.findSingleResultByJpaQuery(String.class, query,
                new PositionalParam(1, 8L));
        LOGGER.info("User: {}", user);
    }

    @Test
    public void testFindSingleResultByNamedJpaQuery() {
        User user = repository.findSingleResultByNamedQuery(User.class, "User.ScalarResult.NamedJpaQuery",
                new PositionalParam(1, 8L));
        LOGGER.info("User: {}", user);
    }

    @Test
    public void testFindSingleResultByNamedNativeQuery() {
        String user = repository.findSingleResultByNamedQuery(String.class, "User.ScalarResult.NamedNativeQuery",
                new PositionalParam(1, 8L));
        LOGGER.info("User: {}", user);
    }

    @Test
    public void testNamedStoredProcedure() {
        List<User> users = repository.findByNamedStoredProcedure("allUsers");
        users.forEach(user -> LOGGER.info("Users: {}", user.getEmail()));
    }

    @Test
    public void testStoredProcedure() {
        List<User> users = repository.findByStoredProcedure(User.class, "fetchAllUsers");
        users.forEach(user -> LOGGER.info("Users: {}", user.getEmail()));
    }
}
