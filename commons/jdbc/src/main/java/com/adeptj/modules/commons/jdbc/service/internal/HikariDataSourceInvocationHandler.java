package com.adeptj.modules.commons.jdbc.service.internal;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The {@link InvocationHandler} which prevents the close of {@link HikariDataSource} from client code.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class HikariDataSourceInvocationHandler implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private HikariDataSource delegate;

    HikariDataSourceInvocationHandler(HikariDataSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // No-op if it is a call for close method.
        if (StringUtils.equals(method.getName(), "close")) {
            LOGGER.warn("HikariDataSource#close can't be invoked by the application code!!");
            return null;
        }
        return method.invoke(this.delegate, args);
    }
}
