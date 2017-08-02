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

import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import com.adeptj.modules.data.jpa.entity.User;
import com.adeptj.modules.data.jpa.internal.EclipseLinkCrudRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

/**
 * JpaCrudRepositoryTest
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JpaCrudRepositoryTest {

    private static EntityManagerFactory emf;

    private static JpaCrudRepository crudRepository;

    @BeforeClass
    public static void init() throws Exception {
        emf = Persistence.createEntityManagerFactory("AdeptJ_PU");
        crudRepository = new EclipseLinkCrudRepository(emf);
    }

    @AfterClass
    public static void destroy() {
        emf.close();
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setAadhaar("123456789012");
        user.setContact("9811454009");
        user.setFirstName("Rakesh");
        user.setLastName("Kumar");
        user.setPan("AWMPK5510F");
        user.setEmail("kumar_rakesh@live.in");
        user = crudRepository.insert(user);
        System.out.println("User ID: " + user.getId());
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setId(10L);
        user.setAadhaar("09876543212");
        user.setContact("9811454009");
        user.setFirstName("Rakesh");
        user.setLastName("Kumar");
        user.setPan("AWMPK5510F");
        user.setEmail("kumar_rakesh@live.in");
        user = crudRepository.update(user);
        System.out.println("User's Aadhaar: " + user.getAadhaar());
    }

    @Test
    public void testUpdateByCriteria() {
        int rowsUpdated = crudRepository.updateByCriteria(UpdateCriteria.builder()
                .entity(User.class)
                .addCriteriaAttribute("firstName", "Rakesh")
                .addUpdateAttribute("aadhaar", "123456123456")
                .build());
        System.out.println("Rows updated: " + rowsUpdated);
    }

    @Test
    public void testDelete() {
        crudRepository.delete(User.class, 19L);
    }

    @Test
    public void testDeleteByCriteria() {
        int rows = crudRepository.deleteByCriteria(DeleteCriteria.builder()
                .entity(User.class)
                .addCriteriaAttribute("aadhaar", "123456123457")
                .build());
        System.out.println("Rows deleted: " + rows);
    }

    @Test
    public void testDeleteByJpaNamedQuery() {
        int rows = crudRepository.deleteByJpaNamedQuery(CrudDTO.builder()
                .entity(User.class)
                .namedQuery("User.deleteUserByAadhaar.JPA")
                .addPosParam("123456123457")
                .build());
        System.out.println("Rows deleted: " + rows);
    }

    @Test
    public void testFindByCriteria() {
        List<BaseEntity> users = crudRepository.findByCriteria(ReadCriteria.builder()
                .entity(User.class)
                .addCriteriaAttribute("aadhaar", "123456123456")
                .build());
        System.out.println("Rows found: " + users.size());
    }

    @Test
    public void testFindByTupleQuery() {
        crudRepository.findByTupleQuery(TupleQueryCriteria.builder()
                .entity(User.class)
                .addSelection("firstName")
                .addSelection("lastName")
                .addCriteriaAttribute("aadhaar", "123456123456")
                .build()).forEach(tuple -> {
            System.out.println("FirstName: " + tuple.get(0));
            System.out.println("LastName: " + tuple.get(1));
        });
    }

    @Test
    public void testFindByJpaNamedQueryAsUser() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("123456123456");
        List<User> users = crudRepository.findByJpaNamedQuery(User.class,
                "User.findUserByAadhaar.JPA.User", posParams);
        users.forEach(user -> {
            System.out.println("FirstName: " + user.getFirstName());
            System.out.println("LastName: " + user.getLastName());

        });
    }

    @Test
    public void testFindByJpaNamedQueryAsObjectArray() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("123456123456");
        crudRepository.findByJpaNamedQuery(Object[].class,
                "User.findUserByAadhaar.JPA.ObjectArray", posParams).forEach(objectArray -> {
            System.out.println("Object[]!!");
            System.out.println("FirstName: " + objectArray[0]);
            System.out.println("LastName: " + objectArray[1]);

        });
    }

    @Test
    public void testFindByNamedQueryJPA() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("123456123456");
        crudRepository.findByNamedQuery("User.findUserByAadhaar.JPA", posParams)
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
    public void testFindByNamedQueryNative() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("123456123456");
        crudRepository.findByNamedQuery("User.findUserByAadhaar.NATIVE", posParams)
                .forEach(object -> {
                    if (object instanceof Object[]) {
                        System.out.println("Object[]!!");
                        Object[] objectArray = (Object[]) object;
                        System.out.println("FirstName: " + objectArray[0]);
                        System.out.println("LastName: " + objectArray[1]);
                    }
                });
    }

    @Test
    public void testFindPaginatedRecords() {
        Long count = crudRepository.count(User.class);
        System.out.println("Total no of users: " + count);
        int pageSize = count.intValue() / 3;
        this.paginate(0, pageSize);
        this.paginate(pageSize, pageSize);
        this.paginate(pageSize * 2, pageSize);
    }

    private void paginate(int startPos, int pageSize) {
        List<User> users = crudRepository.findPaginatedRecords(User.class, startPos, pageSize);
        users.forEach(user -> {
            System.out.println("FirstName: " + user.getFirstName());
            System.out.println("LastName: " + user.getLastName());

        });
    }

    @Test
    public void testFindByJpaQuery() {
        List<User> users = crudRepository.findByJpaQuery(CrudDTO.builder()
                .entity(User.class)
                .jpaQuery("SELECT u FROM  User u WHERE u.aadhaar = ?1 and u.contact = ?2")
                .addPosParam("123456123456")
                .addPosParam("9811459009")
                .build());
        users.forEach(user -> {
            System.out.println("FirstName: " + user.getFirstName());
            System.out.println("LastName: " + user.getLastName());

        });
    }

    @Test
    public void testGetScalarResultByNamedQuery() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("123456123457");
        User user = crudRepository
                .getScalarResultByNamedQuery(User.class, "User.findUserByAadhaar.JPA.ScalarUser",
                        posParams);
        if (user != null) {
            System.out.println("FirstName: " + user.getFirstName());
            System.out.println("LastName: " + user.getLastName());
        }
    }

    @Test
    public void testFindAndMapResultSet() {
        List<Object> posParams = new ArrayList<>();
        posParams.add("123456123457");
        crudRepository.findAndMapResultSet(User.class,
                "SELECT * FROM  Users u WHERE AADHAAR_NUMBER = ?1",
                "User.findUserByAadhaar.Mapping", posParams).forEach(user -> {
            System.out.println("User ID: " + user.getId());
            System.out.println("FirstName: " + user.getFirstName());
            System.out.println("LastName: " + user.getLastName());

        });
    }

    @Test
    public void testFindAndMapConstructorByQuery() {
        String jpaQuery = "SELECT NEW com.adeptj.modules.data.jpa.UserDTO(u.id, u.firstName, u.lastName, u.email) " +
                "FROM User u WHERE u.aadhaar = ?1";
        List<Object> posParams = new ArrayList<>();
        posParams.add("123456123457");
        crudRepository.findAndMapConstructor(UserDTO.class, jpaQuery, posParams).forEach(user -> {
            System.out.println("User ID: " + user.getId());
            System.out.println("FirstName: " + user.getFirstName());
            System.out.println("LastName: " + user.getLastName());

        });
    }

    @Test
    public void testFindAndMapConstructorByCriteria() {
        List<UserDTO> usersDTOList = crudRepository.findAndMapConstructor(ConstructorCriteria.builder()
                .entity(User.class)
                .constructorClass(UserDTO.class)
                .addSelection("id")
                .addSelection("firstName")
                .addSelection("lastName")
                .addSelection("email")
                .addCriteriaAttribute("aadhaar", "123456123457")
                .build());
        usersDTOList
                .forEach((UserDTO dto) -> {
                    System.out.println("User ID: " + dto.getId());
                    System.out.println("FirstName: " + dto.getFirstName());
                    System.out.println("LastName: " + dto.getLastName());

                });
    }
}
