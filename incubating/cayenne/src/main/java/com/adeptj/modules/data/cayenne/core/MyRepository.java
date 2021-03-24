package com.adeptj.modules.data.cayenne.core;

import com.adeptj.modules.data.cayenne.CayenneRepository;
import com.adeptj.modules.data.cayenne.model.Users;
import org.osgi.service.component.annotations.Component;

import java.util.List;

@Component
public class MyRepository extends AbstractCayenneRepository<Users> implements CayenneRepository<Users> {

    public Users getUserById(Object id) {
        return this.findById(id, Users.class);
    }

    public List<Users> getAllUsers() {
        return this.findAll(Users.class);
    }

    public Users getUsersByExpression() {
        return this.findOneByExpression(Users.class, Users.EMAIL.eq("john.doe4@johndoe.com"));
    }

    public List<Users> getAllUsersByExpression() {
        return this.findManyByExpression(Users.class, Users.FIRST_NAME.like("John%"));
    }

    public Users createNewUser() {
        Users users = new Users();
        users.setEmail("John.Rees33@johnreese.com");
        users.setFirstName("John33");
        users.setLastName("Reese33");
        return this.insert(users);
    }

    public Users createUser() {
        return this.insert(Users.class, users -> {
            users.setEmail("John.Rees33@johnreese.com");
            users.setFirstName("John33");
            users.setLastName("Reese33");
        });
    }
}
