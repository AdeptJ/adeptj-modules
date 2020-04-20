package com.adeptj.modules.data.sql2o.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Sql2o configuration.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ Sql2o Configuration",
        description = "Sql2o Configuration"
)
public @interface Sql2oConfig {

    @AttributeDefinition(
            name = "PersistenceUnit Name",
            description = "Note: Must be same as provided in persistence.xml"
    )
    String persistenceUnitName(); // NOSONAR
}
