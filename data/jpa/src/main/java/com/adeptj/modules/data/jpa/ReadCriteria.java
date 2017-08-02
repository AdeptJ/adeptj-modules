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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Criteria object holding arguments for JpaCrudRepository find* methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ReadCriteria<T extends BaseEntity> extends BaseCriteria<T> {

    private List<Object> posParams;

    // For pagination - Start
    private int startPos;

    private int maxResult;
    // For pagination - End

    private ReadCriteria(Class<T> entity) {
        super(entity);
    }

    public List<Object> getPosParams() {
        return posParams;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating {@link ReadCriteria}
     */
    public static class Builder {

        // no public access as available through static method.
        private Builder() {
        }

        private Class<? extends BaseEntity> entity;

        private Map<String, Object> criteriaAttributes;

        private List<Object> posParams;

        private int startPos;

        private int maxResult;

        public <T extends BaseEntity> Builder entity(Class<T> entity) {
            this.entity = entity;
            return this;
        }

        public Builder addCriteriaAttribute(String attributeName, Object value) {
            if (this.criteriaAttributes == null) {
                this.criteriaAttributes = new HashMap<>();
            }
            this.criteriaAttributes.put(attributeName, value);
            return this;
        }

        public Builder addPosParam(Object param) {
            if (this.posParams == null) {
                this.posParams = new ArrayList<>();
            }
            this.posParams.add(param);
            return this;
        }

        public Builder startPos(int startPos) {
            this.startPos = startPos;
            return this;
        }

        public Builder maxResult(int maxResult) {
            this.maxResult = maxResult;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T extends BaseEntity> ReadCriteria<T> build() {
            ReadCriteria<T> criteria = new ReadCriteria<>((Class<T>) this.entity);
            criteria.criteriaAttributes = this.criteriaAttributes;
            criteria.posParams = this.posParams;
            criteria.startPos = this.startPos;
            criteria.maxResult = this.maxResult;
            return criteria;
        }
    }
}
