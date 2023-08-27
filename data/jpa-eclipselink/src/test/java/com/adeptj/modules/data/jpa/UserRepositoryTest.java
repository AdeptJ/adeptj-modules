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
import com.adeptj.modules.data.jpa.entity.Address;
import com.adeptj.modules.data.jpa.entity.User;
import com.adeptj.modules.data.jpa.query.InParam;
import com.adeptj.modules.data.jpa.query.OutParam;
import com.adeptj.modules.data.jpa.query.PositionalParam;
import com.adeptj.modules.data.jpa.util.JpaUtil;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Tuple;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adeptj.modules.data.jpa.JpaConstants.SYS_PROP_ENABLE_EXCEPTION_HANDLER_LOGGING;
import static org.eclipse.persistence.config.PersistenceUnitProperties.SESSION_CUSTOMIZER;

/**
 * JpaCrudRepositoryTest
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Disabled
public class UserRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private UserRepository repository;

    private static EntityManagerFactory entityManagerFactory;

    static {
        System.setProperty(SYS_PROP_ENABLE_EXCEPTION_HANDLER_LOGGING, "true");
    }

    @BeforeAll
    public static void setup() {
        initEclipseLinkEntityManagerFactoryMySQL();
    }

    @AfterAll
    public static void destroy() {
        JpaUtil.closeEntityManagerFactory(entityManagerFactory);
    }

    @BeforeEach
    public void setupRepository() {
        this.repository = new UserRepository();
        this.repository.setEntityManagerFactory(entityManagerFactory);
    }

    private static void initHibernateEntityManagerFactoryPostgres() {
        String unitName = "AdeptJ_PU_Postgres_Hibernate";
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.nonJtaDataSource", getDataSource());
        entityManagerFactory = new HibernatePersistenceProvider().createEntityManagerFactory(unitName, properties);
        LOGGER.info("EntityManagerFactory created!!");
    }

    private static void initEclipseLinkEntityManagerFactoryMySQL() {
        String unitName = "AdeptJ_PU_MySQL_EclipseLink";
        entityManagerFactory = new PersistenceProvider().createEntityManagerFactory(unitName, new HashMap<>());
        LOGGER.info("EntityManagerFactory created!!");
    }

    private static void initEclipseLinkEntityManagerFactoryPostgres() {
        String unitName = "AdeptJ_PU_Postgres_EclipseLink";
        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.nonJtaDataSource", getDataSource());
        properties.put(SESSION_CUSTOMIZER, new PostgresCustomizer());
        entityManagerFactory = new PersistenceProvider().createEntityManagerFactory(unitName, properties);
        LOGGER.info("EntityManagerFactory created!!");
    }

    private static PGSimpleDataSource getDataSource() {
        PGSimpleDataSource ds = new MyPGSimpleDataSource(false);
        ds.setServerNames(null);
        ds.setPortNumbers(null);
        ds.setUser("postgres");
        ds.setPassword("postgres");
        ds.setDatabaseName("JPA");
        return ds;
    }

    // ------------------------------------------------ Test cases ------------------------------------------------

    @Test
    public void testInsert() {
        User usr = new User();
        usr.setContact("6560084680");
        usr.setFirstName("John");
        usr.setLastName("Doe");
        usr.setEmail("john.doe@johndoe.com");
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
        User user = this.repository.insert(usr);
        LOGGER.info("User ID: {}", user.getId());
    }

    @Test
    public void testBatchInsert() {
        List<User> users = new ArrayList<>();
        for (int i = 101; i < 201; i++) {
            User usr = new User();
            usr.setContact("1234567" + i);
            usr.setFirstName("John_" + i);
            usr.setLastName("Doe_" + i);
            usr.setEmail("john.doe+" + i + "@johndoe.com");
            users.add(usr);
        }
        this.repository.batchInsert(users, 20);
    }

    @Test
    public void testDoWithEntityManagerInTransaction() {
        User user = this.repository.doWithEntityManager(em -> {
            User usr = new User();
            usr.setContact("12345678915");
            usr.setFirstName("John");
            usr.setLastName("Doe");
            usr.setEmail("john.doe15@johndoe.com");
            em.persist(usr);
            return usr;
        }, true);
        LOGGER.info("User ID: {}", user.getId());
    }

    @Test
    public void testUpdateWithValidId() {
        User user = new User();
        user.setId(506L);
        user.setContact("1234567890");
        user.setFirstName("John_Updated_Again_");
        user.setLastName("Doe_Updated");
        user.setEmail("john.doe1@johndoe.com");
        user = this.repository.update(user);
        LOGGER.info("User's Contact No is: {}", user.getContact());
    }

    @Test
    public void testUpdateWithNoId() {
        IllegalStateException thrown = Assertions.assertThrows(IllegalStateException.class, () -> {
            User user = new User();
            user.setContact("1234567890");
            user.setFirstName("John_Updated_Again_Again");
            user.setLastName("Doe_Updated_Again");
            user.setEmail("u_john.doe1@johndoe.com");
            this.repository.update(user);
        });
        Assertions.assertEquals("Entity is not a detached entity, not merging it.", thrown.getMessage());
    }

    @Test
    public void testUpdateByCriteria() {
        int rowsUpdated = this.repository.updateByCriteria(UpdateCriteria.builder(User.class)
                .addCriteriaAttribute("firstName", "John")
                .addUpdateAttribute("contact", "1234567891")
                .build());
        LOGGER.info("Rows updated: {}", rowsUpdated);
    }

    @Test
    public void testDelete() {
        this.repository.delete(User.class, 19L);
    }

    @Test
    public void testDeleteByCriteria() {
        int rows = this.repository.deleteByCriteria(DeleteCriteria.builder(User.class)
                .addCriteriaAttribute("contact", "1234567890")
                .build());
        LOGGER.info("Rows deleted: {}", rows);
    }

    @Test
    public void testDeleteByJpaNamedQuery() {
        int rows = this.repository.deleteByJpaNamedQuery("User.DeleteUserByContact.JPA",
                new PositionalParam(1, "1234567890"));
        LOGGER.info("Rows deleted: {}", rows);
    }

    @Test
    public void testFindByCriteria() {
        List<User> users = this.repository.findByCriteria(ReadCriteria.builder(User.class)
                .addCriteriaAttribute("contact", "1234567893")
                .build());
        users.get(0).getAddresses().forEach(address -> LOGGER.info(address.toString()));
        LOGGER.info("Rows found: {}", users.size());
    }

    @Test
    public void testFindById() {
        User user = this.repository.findById(User.class, 9L);
        user.getAddresses().forEach(address -> LOGGER.info(address.toString()));
    }

    @Test
    public void testFindByTupleCriteria() {
        List<Tuple> tuples = this.repository.findByTupleCriteria(TupleCriteria.builder(User.class)
                .addSelection("firstName", "fname")
                .addSelection("contact", "mobile")
                .addCriteriaAttribute("contact", "1234567891")
                .build());
        tuples.forEach(tuple -> {
            LOGGER.info("FirstName by position: {}", tuple.get(0));
            LOGGER.info("Contact by position: {}", tuple.get(1));
            LOGGER.info("FirstName by alias: {}", tuple.get("fname"));
            LOGGER.info("Contact by alias: {}", tuple.get("mobile"));
        });
    }

    @Test
    public void testFindByNamedQueryAsString() {
        this.repository.findByNamedQuery(String.class, "User.findUserFirstNameByContact.JPA.Scalar",
                        new PositionalParam(1, "1234567891"))
                .forEach(firstName -> LOGGER.info("FirstName: {}", firstName));
    }

    @Test
    public void testFindByNamedQueryAsUser() {
        this.repository.findByNamedQuery(User.class, "User.findUserByContact.JPA",
                        new PositionalParam(1, "1234567891"))
                .forEach(user -> {
                    LOGGER.info("FirstName: {}", user.getFirstName());
                    LOGGER.info("LastName: {}", user.getLastName());
                });
    }

    @Test
    public void testFindByNamedQueryAsObjectArray_1() {
        this.repository.findByNamedQuery(Object[].class, "User.findUserByContact.NATIVE",
                        new PositionalParam(1, "1234567891"))
                .forEach(objectArray -> {
                    LOGGER.info("FirstName: {}", objectArray[0]);
                    LOGGER.info("LastName: {}", objectArray[1]);
                });
    }

    @Test
    public void testFindByNamedQueryAsObjectArray_2() {
        this.repository.findByNamedQuery(Object[].class, "User.findUserByContact.JPA.ObjectArray",
                        new PositionalParam(1, "1234567891"))
                .forEach(objectArray -> {
                    LOGGER.info("FirstName: {}", objectArray[0]);
                    LOGGER.info("LastName: {}", objectArray[1]);
                });
    }

    @Test
    public void testFindByNamedQueryWithDTOProjection() {
        this.repository.findByNamedQuery(UserDTO.class, "User.DtoProjection",
                        new PositionalParam(1, "1234567891"))
                .forEach(user -> {
                    LOGGER.info("User ID: {}", user.getId());
                    LOGGER.info("FirstName: {}", user.getFirstName());
                    LOGGER.info("LastName: {}", user.getLastName());
                });
    }

    @Test
    public void testFindByCriteriaWithDTOProjection() {
        List<UserDTO> list = this.repository.findByCriteriaWithDTOProjection(ConstructorCriteria.builder(User.class, UserDTO.class)
                .addSelections("id", "firstName", "lastName", "email")
                .addCriteriaAttribute("contact", "1234567891")
                .build());
        list.forEach(dto -> {
            LOGGER.info("User ID: {}", dto.getId());
            LOGGER.info("FirstName: {}", dto.getFirstName());
            LOGGER.info("LastName: {}", dto.getLastName());
            LOGGER.info("Email: {}", dto.getEmail());
        });
    }

    @Test
    public void testFindAll() {
        List<User> users = this.repository.findAllUsers();
        users.forEach(user -> {
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());
        });
    }

    @Test
    public void testFindAttributeValuesByCriteria() {
        List<String> emails = this.repository.findAttributeValuesByCriteria(User.class, "email", String.class);
        emails.forEach(email -> LOGGER.info("Email: {}", email));
    }

    @Test
    public void testFindMultiAttributeValuesByCriteria() {
        List<Object[]> values = this.repository.findMultiAttributeValuesByCriteria(User.class, "id", "email");
        values.forEach(objectArray -> {
            LOGGER.info("Id: {}", objectArray[0]);
            LOGGER.info("Email: {}", objectArray[1]);
        });
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    public void testFindByINOperator(boolean negation) {
        List<User> users = this.repository.findByINOperator(User.class, "id",
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                negation);
        users.forEach(user -> {
            LOGGER.info("Id: {}", user.getId());
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());
            LOGGER.info("Email: {}", user.getEmail());
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSqlResultSetMappingUsingEntityMapping() {
        List<User> users = this.repository.doWithEntityManager(em ->
                em.createNativeQuery("SELECT * FROM  Users u WHERE FIRST_NAME = ?1",
                                "User.findUserByContact.EntityMapping")
                        .setParameter(1, "John")
                        .getResultList(), false);
        users.forEach(user -> {
            LOGGER.info("User ID: {}", user.getId());
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());
            LOGGER.info("Email: {}", user.getEmail());
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSqlResultSetMappingUsingConstructorMapping() {
        List<UserDTO> users = this.repository.doWithEntityManager(em ->
                em.createNativeQuery("SELECT * FROM  Users u WHERE FIRST_NAME = ?1",
                                "User.findUserByContact.ConstructorMapping")
                        .setParameter(1, "John")
                        .getResultList(), false);
        users.forEach(user -> {
            LOGGER.info("User ID: {}", user.getId());
            LOGGER.info("FirstName: {}", user.getFirstName());
            LOGGER.info("LastName: {}", user.getLastName());
            LOGGER.info("Email: {}", user.getEmail());
        });
    }

    @Test
    public void testCountByCriteria() {
        Long count = this.repository.countByCriteria(User.class);
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testCountByNamedJpaQuery() {
        Long count = this.repository.countByNamedQuery("Count.NamedJpaQuery");
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testCountByNamedNativeQuery() {
        Long count = this.repository.countByNamedQuery("Count.NamedNativeQuery");
        LOGGER.info("Count: {}", count);
    }

    @Test
    public void testFindSingleResultByNamedJpaQuery_1() {
        User user = this.repository.findSingleResultByNamedQuery(User.class, "User.ScalarResult.NamedJpaQuery_1",
                new PositionalParam(1, 8L));
        LOGGER.info("User: {}", user);
    }

    @Test
    public void testFindSingleResultByNamedJpaQuery_2() {
        Object[] data = this.repository.findSingleResultByNamedQuery(Object[].class, "User.ScalarResult.NamedJpaQuery_2",
                new PositionalParam(1, 8L));
        for (Object o : data) {
            LOGGER.info("{}", o);
        }
    }

    @Test
    public void testFindSingleResultByNamedNativeQuery() {
        String email = this.repository.findSingleResultByNamedQuery(String.class, "User.ScalarResult.NamedNativeQuery",
                new PositionalParam(1, 8L));
        LOGGER.info("User email: {}", email);
    }

    @Test
    public void testFindByNamedStoredProcedure() {
        List<User> users = this.repository.findByNamedStoredProcedure("allUsers");
        users.forEach(user -> LOGGER.info("Users: {}", user.getEmail()));
    }

    @Test
    public void testFindByStoredProcedure() {
        List<User> users = this.repository.findByStoredProcedure(User.class, "fetchAllUsers");
        users.forEach(user -> LOGGER.info("Users: {}", user.getEmail()));
    }

    @Test
    public void testExecuteNamedStoredProcedure() {
        Object result = this.repository.executeNamedStoredProcedure("calculateSum",
                List.of(new InParam("n1", 1, Integer.class),
                        new InParam("n2", 1653, Integer.class)), "result");
        LOGGER.info("Result: {}", result);
    }

    @Test
    public void testExecuteStoredProcedure() {
        Object result = this.repository.executeStoredProcedure("adeptj_sum",
                List.of(new InParam("n1", 1, Integer.class),
                        new InParam("n2", 1653, Integer.class)), new OutParam("result", Integer.class));
        LOGGER.info("Result: {}", result);
    }
}