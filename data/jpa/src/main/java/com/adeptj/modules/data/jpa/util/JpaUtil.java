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

package com.adeptj.modules.data.jpa.util;

import com.adeptj.modules.data.jpa.query.InParam;
import com.adeptj.modules.data.jpa.query.NamedParam;
import com.adeptj.modules.data.jpa.query.PositionalParam;
import com.adeptj.modules.data.jpa.query.QueryParam;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import java.lang.invoke.MethodHandles;

import static javax.persistence.ParameterMode.IN;

/**
 * Common JPA Utilities.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class JpaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private JpaUtil() {
    }

    public static EntityManager createEntityManager(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("EntityManagerFactory can't be null!!");
        }
        return entityManagerFactory.createEntityManager();
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

    /**
     * This method binds the the query parameters to the given JPA {@link Query} or {@link TypedQuery}.
     *
     * @param query  the JPA {@link Query} or {@link TypedQuery}
     * @param params query bind parameters, either an array of {@link NamedParam} or a {@link PositionalParam}
     */
    public static void bindQueryParams(Query query, QueryParam... params) {
        if (ArrayUtils.isNotEmpty(params)) {
            for (QueryParam param : params) {
                if (param instanceof NamedParam) {
                    NamedParam namedParam = (NamedParam) param;
                    query.setParameter(namedParam.getName(), namedParam.getValue());
                } else if (param instanceof PositionalParam) {
                    PositionalParam positionalParam = (PositionalParam) param;
                    query.setParameter(positionalParam.getPosition(), positionalParam.getValue());
                }
            }
        }
    }

    public static void bindStoredProcedureInParams(StoredProcedureQuery query, InParam... params) {
        if (ArrayUtils.isNotEmpty(params)) {
            for (InParam param : params) {
                query.registerStoredProcedureParameter(param.getName(), param.getType(), IN)
                        .setParameter(param.getName(), param.getValue());
            }
        }
    }

    public static void bindNamedStoredProcedureInParams(StoredProcedureQuery query, InParam... params) {
        if (ArrayUtils.isNotEmpty(params)) {
            for (InParam param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
        }
    }
}
