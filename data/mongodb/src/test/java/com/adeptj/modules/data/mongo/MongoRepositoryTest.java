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

package com.adeptj.modules.data.mongo;

import com.adeptj.modules.data.mongo.api.MongoCrudRepository;
import com.adeptj.modules.data.mongo.internal.MongoConnectionProvider;
import com.adeptj.modules.data.mongo.internal.MongoCrudRepositoryImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.service.cm.ConfigurationException;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Test case to validate mongodb crud repository availability.
 *
 * @author prince.arora, Adeptj
 */
public class MongoRepositoryTest {

    private static final String TEST_UNIT = "m_u_name";
    private static final String TEST_ID = "m_instance_id";

    private static MongoConnectionProvider connectionProvider;
    private static MongoCrudRepository crudRepository;

    /**
     * Preparing mongodb connection provider {@link MongoConnectionProvider} and
     * Mongo Crud repository {@link MongoCrudRepositoryImpl} instances for further
     * testing.
     */
    @BeforeClass
    public static void preparingRepository() {
        connectionProvider = mock(MongoConnectionProvider.class);
        crudRepository = mock(MongoCrudRepositoryImpl.class);

        given(connectionProvider.getRepository(anyString()))
                .willReturn(Optional.of(crudRepository));
    }

    /**
     * Checking repository instance availability after connection provider
     * configurations are updated.
     *
     * @throws ConfigurationException
     */
    @Test
    public void testServiceProperties() throws ConfigurationException {
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("unitName", TEST_UNIT);
        connectionProvider.updated(new String(TEST_ID), (Dictionary<String, ?>) properties);
        verify(connectionProvider).updated(new String(TEST_ID), (Dictionary<String, ?>) properties);
    }

    /**
     *
     */
    @Test
    public void testRepositoryRetrieveFromProvider() {
        assertTrue(connectionProvider.getRepository(TEST_UNIT).isPresent());
    }
}
