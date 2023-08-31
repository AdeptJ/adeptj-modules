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

package com.adeptj.modules.data.jpa.eclipselink.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import static org.eclipse.persistence.config.PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_ONLY;
import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_OR_EXTEND;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_BOTH_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_DATABASE_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DROP_AND_CREATE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DROP_ONLY;
import static org.eclipse.persistence.config.PersistenceUnitProperties.NONE;
import static org.eclipse.persistence.logging.SessionLog.CACHE;
import static org.eclipse.persistence.logging.SessionLog.CONNECTION;
import static org.eclipse.persistence.logging.SessionLog.DDL;
import static org.eclipse.persistence.logging.SessionLog.EVENT;
import static org.eclipse.persistence.logging.SessionLog.JPA;
import static org.eclipse.persistence.logging.SessionLog.METADATA;
import static org.eclipse.persistence.logging.SessionLog.METAMODEL;
import static org.eclipse.persistence.logging.SessionLog.MISC;
import static org.eclipse.persistence.logging.SessionLog.PROPERTIES;
import static org.eclipse.persistence.logging.SessionLog.QUERY;
import static org.eclipse.persistence.logging.SessionLog.SEQUENCING;
import static org.eclipse.persistence.logging.SessionLog.SERVER;
import static org.eclipse.persistence.logging.SessionLog.SQL;
import static org.eclipse.persistence.logging.SessionLog.TRANSACTION;
import static org.eclipse.persistence.logging.SessionLog.WEAVER;

/**
 * EntityManagerFactory configuration.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ JPA EntityManagerFactory Configuration",
        description = "EntityManagerFactory(EclipseLink) Configuration"
)
public @interface EntityManagerFactoryConfig {

    // EclipseLink logging levels.

    String OFF = "OFF";

    String SEVERE = "SEVERE";

    String WARNING = "WARNING";

    String INFO = "INFO";

    String CONFIG = "CONFIG";

    String FINE = "FINE";

    String FINER = "FINER";

    String FINEST = "FINEST";

    String ALL = "ALL";

    // Common Constants

    String JPA_TRANSACTION_TYPE = "RESOURCE_LOCAL";

    @AttributeDefinition(
            name = "Deploy on Startup",
            description = "Whether to register PersistenceUnit when the application starts up",
            options = {
                    @Option(label = "YES", value = "true"),
                    @Option(label = "NO", value = "false")
            }
    )
    String deploy_on_startup() default "true";

    @AttributeDefinition(
            name = "Use EclipseLink ExceptionHandler",
            description = "Whether to use EclipseLink's ExceptionHandler mechanism"
    )
    boolean use_exception_handler() default true;

    @AttributeDefinition(
            name = "EclipseLink ExceptionHandler FQCN",
            description = "Fully qualified class name of the EclipseLink ExceptionHandler"
    )
    String exception_handler_fqcn() default "com.adeptj.modules.data.jpa.eclipselink.extension.JpaExceptionHandler";

    @AttributeDefinition(
            name = "Log JPA Query Parameters",
            description = "Whether to log JPA query parameters"
    )
    boolean log_query_parameters();

    @AttributeDefinition(
            name = "EclipseLink Query Retry Attempt Count",
            description = "The number of attempts EclipseLink should make to re-connect to a database and re-execute" +
                    " a query after a query has failed because of a communication issue."
    )
    int query_retry_attempt_count();

    @AttributeDefinition(
            name = "Transaction Type",
            description = "JPA Transaction Type(JTA is not supported at this moment!)",
            options = {
                    @Option(label = "RESOURCE_LOCAL", value = JPA_TRANSACTION_TYPE),
                    @Option(label = "JTA", value = "JTA")
            })
    String persistence_unit_transaction_type() default JPA_TRANSACTION_TYPE;

    @AttributeDefinition(
            name = "L2 Cache Mode",
            description = "JPA L2 Caching Strategy",
            options = {
                    @Option(label = "ENABLE_SELECTIVE", value = "ENABLE_SELECTIVE"),
                    @Option(label = "DISABLE_SELECTIVE", value = "DISABLE_SELECTIVE"),
                    @Option(label = "ALL", value = "ALL"),
                    @Option(label = "NONE", value = "NONE"),
                    @Option(label = "UNSPECIFIED", value = "UNSPECIFIED")
            })
    String shared_cache_mode() default "ENABLE_SELECTIVE";

    @AttributeDefinition(
            name = "Entity Validation Mode",
            description = "Bean Validation Options",
            options = {
                    @Option(label = "AUTO", value = "AUTO"),
                    @Option(label = "CALLBACK", value = "CALLBACK"),
                    @Option(label = "NONE", value = "NONE")
            })
    String validation_mode() default "NONE";

    @AttributeDefinition(
            name = "EclipseLink LoggingLevel",
            description = "EclipseLink Logging Level",
            options = {
                    @Option(label = FINE, value = FINE),
                    @Option(label = FINER, value = FINER),
                    @Option(label = FINEST, value = FINEST),
                    @Option(label = SEVERE, value = SEVERE),
                    @Option(label = WARNING, value = WARNING),
                    @Option(label = INFO, value = INFO),
                    @Option(label = CONFIG, value = CONFIG),
                    @Option(label = OFF, value = OFF),
                    @Option(label = ALL, value = ALL)
            })
    String logging_level() default FINE;

    @AttributeDefinition(
            name = "EclipseLink Loggers",
            description = "EclipseLink Logger Names For SLF4J Logging"
    )
    String[] eclipselink_loggers() default {
            CATEGORY_LOGGING_LEVEL_ + CONNECTION,
            CATEGORY_LOGGING_LEVEL_ + JPA,
            CATEGORY_LOGGING_LEVEL_ + SEQUENCING,
            CATEGORY_LOGGING_LEVEL_ + SQL,
            CATEGORY_LOGGING_LEVEL_ + QUERY,
            CATEGORY_LOGGING_LEVEL_ + CACHE,
            CATEGORY_LOGGING_LEVEL_ + TRANSACTION,
            CATEGORY_LOGGING_LEVEL_ + WEAVER,
            CATEGORY_LOGGING_LEVEL_ + METADATA,
            CATEGORY_LOGGING_LEVEL_ + METAMODEL,
            CATEGORY_LOGGING_LEVEL_ + PROPERTIES,
            CATEGORY_LOGGING_LEVEL_ + DDL,
            CATEGORY_LOGGING_LEVEL_ + SERVER,
            CATEGORY_LOGGING_LEVEL_ + EVENT,
            CATEGORY_LOGGING_LEVEL_ + MISC
    };

    @AttributeDefinition(
            name = "EclipseLink SLF4J Logger FQCN",
            description = "Fully qualified class name of the EclipseLink SLF4J Logger"
    )
    String eclipselink_slf4j_logger_fqcn() default "com.adeptj.modules.data.jpa.eclipselink.extension.SLF4JLogger";

    @AttributeDefinition(
            name = "DDL Generation Mode",
            description = "Property to specify where EclipseLink generates and writes the DDL",
            options = {
                    @Option(label = "BOTH", value = DDL_BOTH_GENERATION),
                    @Option(label = "DATABASE", value = DDL_DATABASE_GENERATION),
                    @Option(label = "SQL_SCRIPT", value = DDL_SQL_SCRIPT_GENERATION),
            })
    String ddl_generation_output_mode() default DDL_BOTH_GENERATION;

    @AttributeDefinition(
            name = "DDL Generation Strategy",
            description = "Specifies how the DDL runs",
            options = {
                    @Option(label = "CREATE_OR_EXTEND", value = CREATE_OR_EXTEND),
                    @Option(label = "CREATE_ONLY", value = CREATE_ONLY),
                    @Option(label = "DROP_AND_CREATE", value = DROP_AND_CREATE),
                    @Option(label = "DROP_ONLY", value = DROP_ONLY),
                    @Option(label = "NONE", value = NONE),
            })
    String ddl_generation() default CREATE_OR_EXTEND;

    @AttributeDefinition(
            name = "JPA Properties",
            description = "Extra JPA Properties(key=value) format"
    )
    String[] jpa_properties();
}
