package com.adeptj.modules.data.sql2o.internal;

import com.adeptj.modules.commons.jdbc.service.DataSourceService;
import com.adeptj.modules.data.sql2o.Sql2oRepository;
import com.adeptj.modules.data.sql2o.User;
import com.adeptj.modules.data.sql2o.core.AbstractSql2oRepository;
import com.adeptj.modules.jaxrs.core.JaxRSResource;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Path("/sql2o")
@JaxRSResource(name = "Sql2oResource")
@ProviderType
@Designate(ocd = Sql2oConfig.class)
@Component(service = {Sql2oLifecycle.class}, immediate = true, configurationPolicy = REQUIRE)
public class Sql2oLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Sql2o sql2o;

    @Activate
    public Sql2oLifecycle(@Reference DataSourceService dataSourceService, Sql2oConfig config) {
        this.sql2o = new Sql2o(dataSourceService.getDataSource());
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<User> getUsers() {
        try (Connection connection = this.sql2o.open()) {
            return connection.createQuery("select * from adeptj.users")
                    .addColumnMapping("ID", "id")
                    .addColumnMapping("FIRST_NAME", "firstName")
                    .addColumnMapping("LAST_NAME", "lastName")
                    .addColumnMapping("EMAIL", "email")
                    .addColumnMapping("MOBILE_NO", "contact")
                    .addColumnMapping("SECONDARY_MOBILE_NO", "alternateContact")
                    .addColumnMapping("GOVT_ID", "govtId")
                    .executeAndFetch(User.class);
        }
    }

    // <<------------------------------------- OSGi Internal  -------------------------------------->>

    @Deactivate
    protected void stop() {

    }

    // <<----------------------------------- Sql2oRepository Bind ------------------------------------>>

    @Reference(service = Sql2oRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindJpaRepository(Sql2oRepository repository, Map<String, Object> properties) {
        ((AbstractSql2oRepository) repository).setSql2o(this.sql2o);
    }

    protected void unbindJpaRepository(Sql2oRepository repository, Map<String, Object> properties) {
        ((AbstractSql2oRepository) repository).setSql2o(null);
    }
}
