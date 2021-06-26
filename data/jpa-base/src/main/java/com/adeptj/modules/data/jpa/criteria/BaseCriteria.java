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

package com.adeptj.modules.data.jpa.criteria;

import com.adeptj.modules.data.jpa.BaseEntity;
import org.apache.commons.lang3.Validate;

import java.util.Map;

/**
 * Base for all the *Criteria classes.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public abstract class BaseCriteria<T extends BaseEntity> {

    private final Class<T> entity;

    Map<String, Object> criteriaAttributes;

    BaseCriteria(Class<T> entity) {
        Validate.isTrue((entity != null), "Entity class cannot be null!");
        this.entity = entity;
    }

    public Class<T> getEntity() {
        return entity;
    }

    public Map<String, Object> getCriteriaAttributes() {
        return criteriaAttributes;
    }
}
