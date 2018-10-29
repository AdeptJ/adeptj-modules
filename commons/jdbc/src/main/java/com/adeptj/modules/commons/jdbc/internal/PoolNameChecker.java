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

package com.adeptj.modules.commons.jdbc.internal;

import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks if the pool name for a given {@link javax.sql.DataSource} exists, if yes, then throw exception.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = PoolNameChecker.class)
public class PoolNameChecker {

    private static final String JDBC_DS_EXISTS_MSG = "JDBC pool [%s] already configured, please choose a different name!!";

    private final List<String> poolNames = new ArrayList<>();

    void checkExists(String poolName) {
        if (this.poolNames.contains(poolName)) {
            throw new IllegalStateException(String.format(JDBC_DS_EXISTS_MSG, poolName));
        }
        this.poolNames.add(poolName);
    }

    void remove(String poolName) {
        this.poolNames.remove(poolName);
    }
}
