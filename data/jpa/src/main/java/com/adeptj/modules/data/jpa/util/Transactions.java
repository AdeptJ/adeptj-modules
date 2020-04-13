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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.lang.invoke.MethodHandles;

/**
 * Utilities for Jpa {@link EntityTransaction}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class Transactions {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Transactions() {
    }

    public static void markRollback(EntityManager em) {
        if (em.getTransaction().isActive() && !em.getTransaction().getRollbackOnly()) {
            em.getTransaction().setRollbackOnly();
        }
    }

    public static void rollback(EntityManager em) {
        if (em.getTransaction().isActive() && em.getTransaction().getRollbackOnly()) {
            try {
                LOGGER.warn("Rolling back transaction!!");
                em.getTransaction().rollback();
            } catch (RuntimeException ex) {
                LOGGER.error("Exception while rolling back transaction!!", ex);
            }
        }
    }
}
