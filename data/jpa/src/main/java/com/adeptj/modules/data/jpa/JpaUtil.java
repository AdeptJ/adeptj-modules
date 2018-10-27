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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static void close(EntityManagerFactory emf) {
        if (emf != null && emf.isOpen()) {
            try {
                emf.close();
                LOGGER.info("EntityManagerFactory closed!!");
            } catch (RuntimeException ex) {
                LOGGER.error("Exception while closing EntityManagerFactory!!", ex);
            }
        }
    }

    public static void close(EntityManager em) {
        if (em != null && em.isOpen()) {
            try {
                em.close();
            } catch (RuntimeException ex) {
                LOGGER.error("Exception while closing EntityManager!!", ex);
            }
        }
    }

    /**
     * This method sets the the positional parameters to the given JPA {@link Query}.
     *
     * @param query     the JPA {@link Query}
     * @param posParams positional parameters
     * @return JPA {@link Query}
     */
    public static Query setQueryParams(Query query, List<Object> posParams) {
        setQueryParamsInternal(query, posParams);
        return query;
    }

    /**
     * This method sets the the positional parameters to the given JPA {@link TypedQuery}.
     *
     * @param query     the JPA {@link TypedQuery}
     * @param posParams positional parameters
     * @param <T>       as the type of entity
     * @return JPA {@link TypedQuery}
     */
    public static <T> TypedQuery<T> setTypedQueryParams(TypedQuery<T> query, List<Object> posParams) {
        setQueryParamsInternal(query, posParams);
        return query;
    }


    private static void setQueryParamsInternal(Query query, List<Object> posParams) {
        if (posParams != null && !posParams.isEmpty()) {
            AtomicInteger posParamCounter = new AtomicInteger();
            posParams.forEach(param -> query.setParameter(posParamCounter.incrementAndGet(), param));
        }
    }
}
