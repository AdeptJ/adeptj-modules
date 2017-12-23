package com.adeptj.modules.data.mongo.internal;

import com.adeptj.modules.data.mongo.api.MongoConnectionFactoryService;
import com.adeptj.modules.data.mongo.api.MongoCrudRepository;
import com.adeptj.modules.data.mongo.exception.MongoRepositoryNotFoundException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.adeptj.modules.data.mongo.internal.Utils.PROVIDER_COMPONENT_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;

/**
 * Implementation for {@link MongoConnectionFactoryServiceImpl}
 *
 * @author prince.arora, AdeptJ.
 */
@Component(immediate = true, service = MongoConnectionFactoryService.class)
public class MongoConnectionFactoryServiceImpl implements MongoConnectionFactoryService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(MongoConnectionFactoryServiceImpl.class);

    @Reference(
            target = "("+SERVICE_PID+"="+PROVIDER_COMPONENT_NAME+")",
            service = ManagedServiceFactory.class
    )
    private MongoConnectionProvider connectionProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoCrudRepository getRepository(String unitName) throws MongoRepositoryNotFoundException {
        Optional<MongoCrudRepository> optionalRepository =
                    this.connectionProvider.getRepository(unitName);
        if (!optionalRepository.isPresent()) {
            throw new MongoRepositoryNotFoundException("Repository not found for unit name ["+ unitName +"] ");
        }
        return optionalRepository.get();
    }

}
