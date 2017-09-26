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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Criteria object holding arguments for JpaCrudRepository findByCriteriaAndMapConstructor* methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ConstructorCriteria<T extends BaseEntity, C> extends BaseCriteria<T> {

    private Class<C> constructorClass;

    private List<String> selections;

    private ConstructorCriteria(Class<T> entity, Class<C> constructorClass) {
        super(entity);
        this.constructorClass = constructorClass;
    }

    public Class<C> getConstructorClass() {
        return constructorClass;
    }

    public List<String> getSelections() {
        return selections;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating {@link ConstructorCriteria}
     */
    public static class Builder {

        // no public access as available through static method.
        private Builder() {
        }

        private Class<? extends BaseEntity> entity;

        private Class<?> constructorClass;

        private Map<String, Object> criteriaAttributes;

        private List<String> selections;

        public <T extends BaseEntity> Builder entity(Class<T> entity) {
            this.entity = entity;
            return this;
        }

        public <T extends BaseEntity> Builder constructorClass(Class<?> constructorClass) {
            this.constructorClass = constructorClass;
            return this;
        }

        public Builder addCriteriaAttribute(String attributeName, Object value) {
            if (this.criteriaAttributes == null) {
                this.criteriaAttributes = new HashMap<>();
            }
            this.criteriaAttributes.put(attributeName, value);
            return this;
        }

        public Builder addSelection(String attributeName) {
            if (this.selections == null) {
                this.selections = new ArrayList<>();
            }
            this.selections.add(attributeName);
            return this;
        }

        public Builder addSelections(String... attributeNames) {
            if (this.selections == null) {
                this.selections = new ArrayList<>();
            }
            this.selections.addAll(Arrays.asList(attributeNames));
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T extends BaseEntity, C> ConstructorCriteria<T, C> build() {
            ConstructorCriteria<T, C> criteria = new ConstructorCriteria<>((Class<T>) this.entity,
                    (Class<C>) this.constructorClass);
            criteria.criteriaAttributes = this.criteriaAttributes;
            criteria.selections = this.selections;
            return criteria;
        }
    }
}
