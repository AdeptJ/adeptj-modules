package com.adeptj.modules.data.mybatis.internal;

import com.adeptj.modules.commons.jdbc.service.DataSourceService;
import com.adeptj.modules.commons.utils.Functions;
import com.adeptj.modules.data.mybatis.MyBatisInfoProvider;
import com.adeptj.modules.data.mybatis.MyBatisRepository;
import com.adeptj.modules.data.mybatis.core.AbstractMyBatisRepository;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
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
import java.net.URL;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@ProviderType
@Designate(ocd = MyBatisConfig.class)
@Component(immediate = true)
public class MyBatisLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private SqlSessionFactory sessionFactory;

    @Activate
    public MyBatisLifecycle(@Reference @NotNull MyBatisInfoProvider provider,
                            @Reference DataSourceService dataSourceService, MyBatisConfig config) {
        ClassLoader providerClassLoader = provider.getClass().getClassLoader();
        URL myBatisConfig = providerClassLoader.getResource(provider.getMyBatisConfig());
        if (myBatisConfig == null) {
            throw new MyBatisBootstrapException("mybatis config not found!!");
        }
        try (InputStream stream = myBatisConfig.openStream()) {
            Functions.executeUnderContextClassLoader(providerClassLoader, () -> {
                Configuration configuration = new XMLConfigBuilder(stream).parse();
                configuration.setEnvironment(new Environment.Builder(provider.getEnvironmentId())
                        .dataSource(dataSourceService.getDataSource())
                        .transactionFactory(new JdbcTransactionFactory())
                        .build());
                this.sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
            });
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new MyBatisBootstrapException(ex);
        }
    }

    @Reference(service = MyBatisRepository.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindMyBatisRepository(MyBatisRepository<?, ?> repository) {
        if (!(repository instanceof AbstractMyBatisRepository)) {
            throw new MyBatisRepositoryBindException("The repository instance must extend AbstractMyBatisRepository!");
        }
        AbstractMyBatisRepository<?, ?> myBatisRepository = (AbstractMyBatisRepository<?, ?>) repository;
        myBatisRepository.setSessionFactory(this.sessionFactory);
    }

    protected void unbindMyBatisRepository(MyBatisRepository<?, ?> repository) {
        if (repository instanceof AbstractMyBatisRepository) {
            AbstractMyBatisRepository<?, ?> myBatisRepository = (AbstractMyBatisRepository<?, ?>) repository;
            myBatisRepository.setSessionFactory(null);
        }
    }
}
