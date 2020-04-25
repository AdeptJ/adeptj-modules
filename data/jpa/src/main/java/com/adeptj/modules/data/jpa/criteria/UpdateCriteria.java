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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Criteria object holding arguments for JpaCrudRepository update* methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class UpdateCriteria<T extends BaseEntity> extends BaseCriteria<T> {

    private Map<String, Object> updateAttributes;

    private UpdateCriteria(Class<T> entity) {
        super(entity);
    }

    public Map<String, Object> getUpdateAttributes() {
        return updateAttributes;
    }

    @Contract(value = "_ -> new", pure = true)
    public static <T extends BaseEntity> @NotNull Builder<T> builder(Class<T> entity) {
        return new Builder<>(entity);
    }

    /**
     * Builder for creating {@link UpdateCriteria}
     */
    public static class Builder<T extends BaseEntity> {

        private final Class<T> entity;

        private Map<String, Object> criteriaAttributes;

        private Map<String, Object> updateAttributes;

        private Builder(Class<T> entity) {
            this.entity = entity;
        }

        public Builder<T> addCriteriaAttribute(String attributeName, Object value) {
            if (this.criteriaAttributes == null) {
                this.criteriaAttributes = new HashMap<>();
            }
            this.criteriaAttributes.put(attributeName, value);
            return this;
        }

        public Builder<T> addUpdateAttribute(String attributeName, Object value) {
            if (this.updateAttributes == null) {
                this.updateAttributes = new HashMap<>();
            }
            this.updateAttributes.put(attributeName, value);
            return this;
        }

        public UpdateCriteria<T> build() {
            UpdateCriteria<T> criteria = new UpdateCriteria<>(this.entity);
            criteria.criteriaAttributes = this.criteriaAttributes;
            criteria.updateAttributes = this.updateAttributes;
            return criteria;
        }
    }
}
