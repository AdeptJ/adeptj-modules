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

/**
 * Constants for JPA properties.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public final class JpaConstants {

    // Class just declaring constants, no instances required.
    private JpaConstants() {
    }

    public static final String SHARED_CACHE_MODE = "javax.persistence.sharedCache.mode";

    public static final String PERSISTENCE_PROVIDER = "javax.persistence.provider";

    public static final String JPA_FACTORY_PID = "com.adeptj.modules.data.jpa.JpaRepository.factory";

    public static final String PU_NAME = "osgi.unit.name";
}
