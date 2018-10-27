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
 * Criteria object holding arguments for JpaCrudRepository findByTuple* methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class TupleQueryCriteria<T extends BaseEntity> extends BaseCriteria<T> {

    private List<String> selections;

    private TupleQueryCriteria(Class<T> entity) {
        super(entity);
    }

    public List<String> getSelections() {
        return selections;
    }

    public static <T extends BaseEntity> Builder<T> builder(Class<T> entity) {
        return new Builder<>(entity);
    }

    /**
     * Builder for creating {@link TupleQueryCriteria}
     */
    public static class Builder<T extends BaseEntity> {

        private Class<T> entity;

        private Map<String, Object> criteriaAttributes;

        private List<String> selections;

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

        public Builder<T> addSelection(String attributeName) {
            if (this.selections == null) {
                this.selections = new ArrayList<>();
            }
            this.selections.add(attributeName);
            return this;
        }

        public Builder<T> addSelections(String... attributeNames) {
            if (this.selections == null) {
                this.selections = new ArrayList<>();
            }
            this.selections.addAll(Arrays.asList(attributeNames));
            return this;
        }

        public TupleQueryCriteria<T> build() {
            TupleQueryCriteria<T> criteria = new TupleQueryCriteria<>(this.entity);
            criteria.criteriaAttributes = this.criteriaAttributes;
            criteria.selections = this.selections;
            return criteria;
        }
    }
}
