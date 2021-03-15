package com.adeptj.modules.data.cayenne.internal;

import com.adeptj.modules.commons.jdbc.service.DataSourceService;
import com.adeptj.modules.commons.utils.Functions;
import com.adeptj.modules.data.cayenne.CayenneRepository;
import com.adeptj.modules.data.cayenne.core.AbstractCayenneRepository;
import com.adeptj.modules.data.cayenne.model.Users;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.ObjectSelect;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.List;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Component
public class CayenneLifeCycle {

    private ServerRuntime cayenne;

    @Activate
    public CayenneLifeCycle(@Reference DataSourceService dataSourceService) {
        Functions.executeUnderContextClassLoader(this.getClass().getClassLoader(), () -> {
            this.cayenne = ServerRuntime.builder()
                    .dataSource(dataSourceService.getDataSource())
                    .disableModulesAutoLoading()
                    .addConfig("cayenne-adeptj.xml")
                    .build();
            ObjectContext context = this.cayenne.newContext();
            List<Users> users = ObjectSelect.query(Users.class).select(context);
            System.out.println(users);
        });
    }

    // <<------------------------------------- OSGi Internal  -------------------------------------->>

    @Deactivate
    protected void stop() {
        this.cayenne.shutdown();
    }

    // <<----------------------------------- JpaRepository Bind ------------------------------------>>

    @Reference(service = CayenneRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindCayenneRepository(CayenneRepository<?> repository) {
        if (repository instanceof AbstractCayenneRepository) {
            AbstractCayenneRepository<?> cayenneRepository = (AbstractCayenneRepository<?>) repository;
            cayenneRepository.setCayenne(this.cayenne);
        }
    }

    protected void unbindCayenneRepository(CayenneRepository<?> repository) {
        // Let's do an explicit type check to avoid a CCE.
        if (repository instanceof AbstractCayenneRepository) {
            AbstractCayenneRepository<?> cayenneRepository = (AbstractCayenneRepository<?>) repository;
            cayenneRepository.setCayenne(null);
        }
    }
}
