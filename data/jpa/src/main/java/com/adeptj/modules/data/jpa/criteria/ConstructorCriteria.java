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

    private final Class<C> constructorClass;

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

    public static <T extends BaseEntity, C> Builder<T, C> builder(Class<T> entity, Class<C> constructorClass) {
        return new Builder<>(entity, constructorClass);
    }

    /**
     * Builder for creating {@link ConstructorCriteria}
     */
    public static class Builder<T extends BaseEntity, C> {

        private final Class<T> entity;

        private final Class<C> constructorClass;

        private Map<String, Object> criteriaAttributes;

        private List<String> selections;

        private Builder(Class<T> entity, Class<C> constructorClass) {
            this.entity = entity;
            this.constructorClass = constructorClass;
        }

        public Builder<T, C> addCriteriaAttribute(String attributeName, Object value) {
            if (this.criteriaAttributes == null) {
                this.criteriaAttributes = new HashMap<>();
            }
            this.criteriaAttributes.put(attributeName, value);
            return this;
        }

        public Builder<T, C> addSelection(String attributeName) {
            if (this.selections == null) {
                this.selections = new ArrayList<>();
            }
            this.selections.add(attributeName);
            return this;
        }

        public Builder<T, C> addSelections(String... attributeNames) {
            if (this.selections == null) {
                this.selections = new ArrayList<>();
            }
            this.selections.addAll(Arrays.asList(attributeNames));
            return this;
        }

        public ConstructorCriteria<T, C> build() {
            ConstructorCriteria<T, C> criteria = new ConstructorCriteria<>(this.entity, this.constructorClass);
            criteria.criteriaAttributes = this.criteriaAttributes;
            criteria.selections = this.selections;
            return criteria;
        }
    }
}
