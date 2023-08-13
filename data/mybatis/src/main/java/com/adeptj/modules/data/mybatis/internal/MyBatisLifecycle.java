package com.adeptj.modules.data.mybatis.internal;

import com.adeptj.modules.commons.jdbc.DataSourceService;
import com.adeptj.modules.commons.utils.ClassLoaders;
import com.adeptj.modules.commons.utils.CollectionUtil;
import com.adeptj.modules.data.mybatis.api.AbstractMyBatisRepository;
import com.adeptj.modules.data.mybatis.api.MyBatisInfoProvider;
import com.adeptj.modules.data.mybatis.api.MyBatisRepository;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Collection;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@ProviderType
@Designate(ocd = MyBatisConfig.class)
@Component(immediate = true, configurationPolicy = REQUIRE)
public class MyBatisLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String MYBATIS_CONFIG_XML_PATH = "META-INF/mybatis-config.xml";

    private final SqlSessionFactory sessionFactory;

    @Activate
    public MyBatisLifecycle(@NotNull @Reference MyBatisInfoProvider provider,
                            @NotNull @Reference DataSourceService dataSourceService, @NotNull MyBatisConfig config) {
        Configuration configuration = this.initConfiguration(provider, dataSourceService, config);
        this.sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        LOGGER.info("MyBatis SqlSessionFactory initialized!");
    }

    private Configuration initConfiguration(@NotNull MyBatisInfoProvider provider,
                                            @NotNull DataSourceService dataSourceService, @NotNull MyBatisConfig config) {
        Configuration configuration = this.doInitConfiguration(provider, config);
        this.addMappersFromMyBatisInfoProvider(provider.getMappers(), configuration);
        Environment environment = new Environment.Builder(config.environment_id())
                .dataSource(dataSourceService.getDataSource())
                .transactionFactory(new JdbcTransactionFactory())
                .build();
        configuration.setEnvironment(environment);
        LOGGER.info("Initialized mybatis Configuration [{}]", configuration);
        return configuration;
    }

    private Configuration doInitConfiguration(@NotNull MyBatisInfoProvider provider, @NotNull MyBatisConfig config) {
        Configuration configuration;
        if (config.disable_xml_configuration()) {
            LOGGER.info("MyBatis xml based configuration disabled, creating Configuration via constructor!");
            configuration = new Configuration(); // This is with minimal defaults.
        } else {
            LOGGER.info("Parsing mybatis config xml [{}]", MYBATIS_CONFIG_XML_PATH);
            configuration = ClassLoaders.executeUnderContextClassLoader(provider.getClass().getClassLoader(), () -> {
                try (InputStream stream = Resources.getResourceAsStream(MYBATIS_CONFIG_XML_PATH)) {
                    return new XMLConfigBuilder(stream, config.environment_id()).parse();
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    throw new MyBatisBootstrapException(ex);
                }
            });
        }
        return configuration;
    }

    private void addMappersFromMyBatisInfoProvider(Collection<Class<?>> mappers, Configuration configuration) {
        if (CollectionUtil.isNotEmpty(mappers)) {
            for (Class<?> mapper : mappers) {
                if (configuration.hasMapper(mapper)) {
                    LOGGER.error("Mapper {} is already known to the MapperRegistry, skipping it!", mapper.getName());
                } else {
                    configuration.addMapper(mapper);
                }
            }
        }
    }

    // <<------------------------------------- OSGi Internal  -------------------------------------->>

    @Reference(service = MyBatisRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindMyBatisRepository(MyBatisRepository<?, ?> repository) {
        if (!(repository instanceof AbstractMyBatisRepository<?, ?> myBatisRepository)) {
            throw new MyBatisRepositoryBindException("The repository instance must extend AbstractMyBatisRepository!");
        }
        LOGGER.info("Binding MyBatisRepository {}", repository);
        myBatisRepository.setSessionFactory(this.sessionFactory);
    }

    protected void unbindMyBatisRepository(MyBatisRepository<?, ?> repository) {
        if (repository instanceof AbstractMyBatisRepository<?, ?> myBatisRepository) {
            LOGGER.info("Unbinding MyBatisRepository {}", repository);
            myBatisRepository.setSessionFactory(null);
        }
    }
}
