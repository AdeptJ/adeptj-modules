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

import com.adeptj.modules.data.jpa.api.JpaCrudRepository;

import java.io.Serializable;

/**
 * Marker Interface and super of all the JPA Entities that {@link JpaCrudRepository} will be dealing with.
 * <p>
 * This interface denotes that all of its implementations are Serializable.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface BaseEntity extends Serializable {

    /**
     * Most of the sub classes have an Id, can be of type Integer, Long etc. which are Serializable.
     */
    Serializable getId();
}
