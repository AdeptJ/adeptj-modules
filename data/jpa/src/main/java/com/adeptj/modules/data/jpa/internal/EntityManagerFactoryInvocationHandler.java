package com.adeptj.modules.data.jpa.internal;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The {@link InvocationHandler} which prevents the close of {@link EntityManagerFactory} from client code.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class EntityManagerFactoryInvocationHandler implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final EntityManagerFactory delegate;

    EntityManagerFactoryInvocationHandler(EntityManagerFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // No-op if it is a call for close method.
        if (StringUtils.equals(method.getName(), "close")) {
            LOGGER.warn("EntityManagerFactory#close can't be invoked by the application code!!");
            return null;
        }
        return method.invoke(this.delegate, args);
    }
}
