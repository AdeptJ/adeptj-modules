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
            name = "Default Column Mapping",
            description = "Column mappings that will be applied globally."
    )
    String[] default_column_mappings(); // NOSONAR
}
