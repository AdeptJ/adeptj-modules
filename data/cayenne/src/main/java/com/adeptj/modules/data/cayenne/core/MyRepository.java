package com.adeptj.modules.data.cayenne.core;

import com.adeptj.modules.data.cayenne.CayenneRepository;
import com.adeptj.modules.data.cayenne.model.Users;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.osgi.service.component.annotations.Component;

@Component
public class MyRepository extends AbstractCayenneRepository<Users> implements CayenneRepository<Users> {

    public Users getUserById() {
        return this.doInCayenne(cayenne -> {
            return SelectById.query(Users.class, 7).selectOne(cayenne.newContext());
        });
    }

    public Users createUser() {
        return this.doInCayenne(cayenne -> {
            ObjectContext context = cayenne.newContext();
            Users users = context.newObject(Users.class);
            context.commitChanges();
            return users;
        });
    }
}
