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

package com.adeptj.modules.data.jpa;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Common JPA Utilities.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JpaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private JpaUtil() {
    }

    public static EntityManager createEntityManager(EntityManagerFactory emf) {
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory is null!!");
        }
        return emf.createEntityManager();
    }

    public static EntityManagerFactory proxyEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        return (EntityManagerFactory) Proxy.newProxyInstance(JpaUtil.class.getClassLoader(),
                new Class[]{EntityManagerFactory.class},
                new EntityManagerFactoryInvocationHandler(entityManagerFactory));
    }

    public static void closeEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            try {
                entityManagerFactory.close();
                LOGGER.info("EntityManagerFactory closed!!");
            } catch (RuntimeException ex) {
                LOGGER.error("Exception while closing EntityManagerFactory!!", ex);
            }
        }
    }

    public static void closeEntityManager(EntityManager entityManager) {
        if (entityManager != null && entityManager.isOpen()) {
            try {
                entityManager.close();
            } catch (RuntimeException ex) {
                LOGGER.error("Exception while closing EntityManager!!", ex);
            }
        }
    }

    /**
     * This method binds the the positional parameters to the given JPA {@link Query} or {@link TypedQuery}.
     *
     * @param query     the JPA {@link Query} or {@link TypedQuery}
     * @param posParams positional parameters
     */
    public static void bindQueryParams(Query query, List<Object> posParams) {
        if (posParams != null) {
            for (int index = 0; index < posParams.size(); index++) {
                int position = index + 1;
                Object value = posParams.get(index);
                LOGGER.info("Binding JPA Query parameter: [{}] at position: [{}]", value, position);
                query.setParameter(position, value);
            }
        }
    }

    /**
     * The {@link InvocationHandler} which prevents the close of {@link EntityManagerFactory} from client code.
     *
     * @author Rakesh.Kumar, AdeptJ
     */
    private static class EntityManagerFactoryInvocationHandler implements InvocationHandler {

        private final EntityManagerFactory delegate;

        private EntityManagerFactoryInvocationHandler(EntityManagerFactory delegate) {
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
}
