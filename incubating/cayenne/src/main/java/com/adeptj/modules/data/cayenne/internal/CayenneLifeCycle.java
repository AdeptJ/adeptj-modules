package com.adeptj.modules.data.cayenne.internal;

import com.adeptj.modules.commons.jdbc.DataSourceService;
import com.adeptj.modules.commons.utils.ClassLoaders;
import com.adeptj.modules.data.cayenne.CayenneRepository;
import com.adeptj.modules.data.cayenne.core.AbstractCayenneRepository;
import com.adeptj.modules.data.cayenne.core.MyRepository;
import com.adeptj.modules.data.cayenne.model.Users;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Component
public class CayenneLifeCycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ServerRuntime cayenne;

    @Activate
    public CayenneLifeCycle(@Reference DataSourceService dataSourceService) {
        ClassLoaders.executeUnderContextClassLoader(this.getClass().getClassLoader(), () -> {
            this.cayenne = ServerRuntime.builder()
                    .dataSource(dataSourceService.getDataSource())
                    .disableModulesAutoLoading()
                    .addConfig("cayenne-adeptj.xml")
                    .build();
            ObjectContext context = this.cayenne.newContext();
            List<Users> users = ObjectSelect.query(Users.class).select(context);
            LOGGER.info("{}", users);
        });
    }

    // <<------------------------------------- OSGi Internal  -------------------------------------->>

    @Deactivate
    protected void stop() {
        this.cayenne.shutdown();
    }

    // <<----------------------------------- CayenneRepository Bind ------------------------------------>>

    @Reference(service = CayenneRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindCayenneRepository(CayenneRepository<?> repository) {
        if (repository instanceof AbstractCayenneRepository<?> cayenneRepository) {
            cayenneRepository.setCayenne(this.cayenne);
            MyRepository myRepository = (MyRepository) cayenneRepository;
            List<Users> all = myRepository.getAllUsers();
            all.forEach(u -> LOGGER.info("{}", u));
            List<Users> usersByExpression = myRepository.getAllUsersByExpression();
            if (usersByExpression != null) {
                usersByExpression.forEach(System.out::println);
            }
            try {
                Users user = myRepository.createNewUser();
                if (user != null) {
                    LOGGER.info("{}", user);
                }
                user = myRepository.createUser();
                if (user != null) {
                    LOGGER.info("{}", user);
                }
                Users usersByExpression1 = myRepository.getUsersByExpression();
                if (usersByExpression1 != null) {
                    LOGGER.info("{}", usersByExpression1);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    protected void unbindCayenneRepository(CayenneRepository<?> repository) {
        // Let's do an explicit type check to avoid a CCE.
        if (repository instanceof AbstractCayenneRepository<?> cayenneRepository) {
            cayenneRepository.setCayenne(null);
        }
    }
}
