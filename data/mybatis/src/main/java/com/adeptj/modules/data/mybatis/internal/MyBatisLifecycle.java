package com.adeptj.modules.data.mybatis.internal;

import com.adeptj.modules.commons.jdbc.service.DataSourceService;
import com.adeptj.modules.commons.utils.CollectionUtil;
import com.adeptj.modules.commons.utils.Functions;
import com.adeptj.modules.data.mybatis.MyBatisInfoProvider;
import com.adeptj.modules.data.mybatis.MyBatisRepository;
import com.adeptj.modules.data.mybatis.core.AbstractMyBatisRepository;
import org.apache.commons.lang3.StringUtils;
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

    private final SqlSessionFactory sessionFactory;

    @Activate
    public MyBatisLifecycle(@NotNull @Reference MyBatisInfoProvider provider,
                            @NotNull @Reference DataSourceService dataSourceService, @NotNull MyBatisConfig config) {
        Configuration configuration = this.getConfiguration(provider, config);
        this.addMappers(provider.getMappers(), configuration);
        configuration.setEnvironment(new Environment.Builder(config.environment_id())
                .dataSource(dataSourceService.getDataSource())
                .transactionFactory(new JdbcTransactionFactory())
                .build());
        this.sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        LOGGER.info("MyBatis SqlSessionFactory initialized!");
    }

    private Configuration getConfiguration(MyBatisInfoProvider provider, @NotNull MyBatisConfig config) {
        if (config.disable_xml_configuration()) {
            LOGGER.info("MyBatis xml based configuration disabled, creating Configuration via constructor!");
            return new Configuration();
        }
        return Functions.executeUnderContextClassLoader(provider.getClass().getClassLoader(), () -> {
            String configXmlLocation = provider.getConfigXmlLocation();
            if (StringUtils.isEmpty(configXmlLocation) || config.override_provider_xml_config()) {
                configXmlLocation = config.config_xml_location();
            }
            LOGGER.info("Loading [{}]", configXmlLocation);
            try (InputStream stream = Resources.getResourceAsStream(configXmlLocation)) {
                XMLConfigBuilder configBuilder = new XMLConfigBuilder(stream, config.environment_id());
                LOGGER.info("Parsing [{}]", configXmlLocation);
                Configuration configuration = configBuilder.parse();
                LOGGER.info("Initialized [{}] after parsing [{}]", configuration, configXmlLocation);
                return configuration;
            } catch (IOException ex) {
                LOGGER.error(ex.getMessage(), ex);
                throw new MyBatisBootstrapException(ex);
            }
        });
    }

    private void addMappers(Collection<Class<?>> mappers, Configuration configuration) {
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
        if (!(repository instanceof AbstractMyBatisRepository)) {
            throw new MyBatisRepositoryBindException("The repository instance must extend AbstractMyBatisRepository!");
        }
        LOGGER.info("Binding MyBatisRepository {}", repository);
        AbstractMyBatisRepository<?, ?> myBatisRepository = (AbstractMyBatisRepository<?, ?>) repository;
        myBatisRepository.setSessionFactory(this.sessionFactory);
    }

    protected void unbindMyBatisRepository(MyBatisRepository<?, ?> repository) {
        if (repository instanceof AbstractMyBatisRepository) {
            LOGGER.info("Unbinding MyBatisRepository {}", repository);
            AbstractMyBatisRepository<?, ?> myBatisRepository = (AbstractMyBatisRepository<?, ?>) repository;
            myBatisRepository.setSessionFactory(null);
        }
    }
}
