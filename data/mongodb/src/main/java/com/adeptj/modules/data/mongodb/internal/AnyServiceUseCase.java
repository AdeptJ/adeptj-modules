package com.adeptj.modules.data.mongodb.internal;

import com.adeptj.modules.data.mongodb.api.AbstractMongoRepository;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.AnyService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@Component(immediate = true)
public class AnyServiceUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Reference(
            service = AnyService.class,
            target = "(&(mongodb.database.name=*)(mongodb.collection.name=*))",
            cardinality = MULTIPLE, policy = DYNAMIC
    )
    protected <T> void bindMongoRepository(@NotNull T repository, @NotNull Map<String, Object> properties) {
        // We are not interested in any of the MongoRepository impl which is not a subclass of AbstractMongoRepository.
        if (repository instanceof AbstractMongoRepository) {
            LOGGER.info("Binding MongoRepository [{}]", repository);
        }

    }

    protected <T> void unbindMongoRepository(@NotNull T repository) {
        // Let's do an explicit type check to avoid a CCE.
        if (repository instanceof AbstractMongoRepository) {
            LOGGER.info("Unbinding MongoRepository [{}]", repository);
        }
    }
}
