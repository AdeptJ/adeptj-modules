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

import java.util.HashMap;
import java.util.Map;

/**
 * Criteria object holding arguments for JpaCrudRepository delete* methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class DeleteCriteria<T extends BaseEntity> extends BaseCriteria<T> {

    private DeleteCriteria(Class<T> entity) {
        super(entity);
    }

    public static <T extends BaseEntity> Builder<T> builder(Class<T> entity) {
        return new Builder<>(entity);
    }

    /**
     * Builder for creating {@link DeleteCriteria}
     */
    public static class Builder<T extends BaseEntity> {

        private final Class<T> entity;

        private Builder(Class<T> entity) {
            this.entity = entity;
        }

        private Map<String, Object> criteriaAttributes;

        public Builder<T> addCriteriaAttribute(String attributeName, Object value) {
            if (this.criteriaAttributes == null) {
                this.criteriaAttributes = new HashMap<>();
            }
            this.criteriaAttributes.put(attributeName, value);
            return this;
        }

        public DeleteCriteria<T> build() {
            DeleteCriteria<T> criteria = new DeleteCriteria<>(this.entity);
            criteria.criteriaAttributes = this.criteriaAttributes;
            return criteria;
        }
    }
}
