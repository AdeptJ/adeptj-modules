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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Criteria object holding arguments for JpaCrudRepository findByTuple* methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class TupleCriteria<T extends BaseEntity> extends BaseCriteria<T> {

    private List<TupleSelection> selections;

    private TupleCriteria(Class<T> entity) {
        super(entity);
    }

    public List<TupleSelection> getSelections() {
        return selections;
    }

    public static class TupleSelection {

        private final String attributeName;

        private final String alias;

        public TupleSelection(String attributeName, String alias) {
            Validate.isTrue(StringUtils.isNotEmpty(attributeName), "attributeName string can't be null!");
            this.attributeName = attributeName;
            this.alias = alias;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public String getAlias() {
            return alias;
        }
    }

    public static <T extends BaseEntity> @NotNull Builder<T> builder(Class<T> entity) {
        return new Builder<>(entity);
    }

    /**
     * Builder for creating {@link TupleCriteria}
     */
    public static class Builder<T extends BaseEntity> {

        private final Class<T> entity;

        private Map<String, Object> criteriaAttributes;

        private List<TupleSelection> selections;

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

        public Builder<T> addSelection(String attributeName, String alias) {
            if (this.selections == null) {
                this.selections = new ArrayList<>();
            }
            this.selections.add(new TupleSelection(attributeName, alias));
            return this;
        }

        public TupleCriteria<T> build() {
            TupleCriteria<T> criteria = new TupleCriteria<>(this.entity);
            criteria.criteriaAttributes = this.criteriaAttributes;
            criteria.selections = this.selections;
            return criteria;
        }
    }
}
