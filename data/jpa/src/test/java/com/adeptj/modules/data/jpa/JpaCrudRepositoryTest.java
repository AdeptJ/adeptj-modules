package com.adeptj.modules.data.jpa;

import com.adeptj.modules.data.jpa.api.JpaCrudRepository;
import com.adeptj.modules.data.jpa.entity.User;
import com.adeptj.modules.data.jpa.internal.EclipseLinkCrudRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Tuple;
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
        // Get the entity manager for the tests.
        emf = Persistence.createEntityManagerFactory("AdeptJ_PU");
        System.out.println(emf);
        crudRepository = new EclipseLinkCrudRepository(emf);
    }

    /**
     * Cleans up the EntityManagerFactory.
     */
    @AfterClass
    public static void destroy() {
        emf.close();
    }

    //@Test
    public void testInsert() {
        User user = new User();
        user.setAadhaar("123456789012");
        user.setContact("9811454009");
        user.setFirstName("Rakesh");
        user.setLastName("Kumar");
        user.setPan("AWMPK5510F");
        user.setEmail("kumar_rakesh@live.in");
        crudRepository.insert(user);
        System.out.println("User ID: " + user.getId());
    }

    //@Test
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

    //@Test
    public void testUpdateByCriteria() {
        int rowsUpdated = crudRepository.updateByCriteria(UpdateCriteria.builder()
                .entity(User.class)
                .addCriteriaAttribute("firstName", "Rakesh")
                .addUpdateAttribute("aadhaar", "123456123456")
                .build());
        System.out.println("Rows updated: " + rowsUpdated);
    }

    //@Test
    public void testDelete() {
        crudRepository.delete(User.class, 19L);
    }

    //@Test
    public void testDeleteByCriteria() {
        int rows = crudRepository.deleteByCriteria(DeleteCriteria.builder()
                .entity(User.class)
                .addCriteriaAttribute("aadhaar", "123456123457")
                .build());
        System.out.println("Rows deleted: " + rows);
    }

    //@Test
    public void testDeleteByJpaNamedQuery() {
        int rows = crudRepository.deleteByJpaNamedQuery(CrudDTO.builder()
                .entity(User.class)
                .namedQuery("User.deleteUserByAadhaar.JPA")
                .addPosParam("123456123457")
                .build());
        System.out.println("Rows deleted: " + rows);
    }

    //@Test
    public void testFindByCriteria() {
        List<BaseEntity> users = crudRepository.findByCriteria(ReadCriteria.builder()
                .entity(User.class)
                .addCriteriaAttribute("aadhaar", "123456123456")
                .build());
        System.out.println("Rows found: " + users.size());
    }

    //@Test
    public void testFindByTupleQuery() {
        List<Tuple> tuples = crudRepository.findByTupleQuery(TupleQueryCriteria.builder()
                .entity(User.class)
                .addSelection("firstName")
                .addSelection("lastName")
                .addCriteriaAttribute("aadhaar", "123456123456")
                .build());
        tuples.forEach(tuple -> {
            System.out.println("FirstName: " + tuple.get(0));
            System.out.println("LastName: " + tuple.get(1));
        });
    }
}
