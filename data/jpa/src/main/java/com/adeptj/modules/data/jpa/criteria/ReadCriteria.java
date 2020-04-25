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

import java.util.ArrayList;
import java.util.Arrays;
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

    @Contract(value = "_ -> new", pure = true)
    public static <T extends BaseEntity> @NotNull Builder<T> builder(Class<T> entity) {
        return new Builder<>(entity);
    }

    /**
     * Builder for creating {@link ReadCriteria}
     */
    public static class Builder<T extends BaseEntity> {

        private final Class<T> entity;

        private Map<String, Object> criteriaAttributes;

        private List<Object> posParams;

        private int startPos;

        private int maxResult;

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

        public Builder<T> addPosParam(Object param) {
            if (this.posParams == null) {
                this.posParams = new ArrayList<>();
            }
            this.posParams.add(param);
            return this;
        }

        public Builder<T> addPosParams(Object... params) {
            if (this.posParams == null) {
                this.posParams = new ArrayList<>();
            }
            this.posParams.addAll(Arrays.asList(params));
            return this;
        }

        public Builder<T> startPos(int startPos) {
            this.startPos = startPos;
            return this;
        }

        public Builder<T> maxResult(int maxResult) {
            this.maxResult = maxResult;
            return this;
        }

        public ReadCriteria<T> build() {
            ReadCriteria<T> criteria = new ReadCriteria<>(this.entity);
            criteria.criteriaAttributes = this.criteriaAttributes;
            criteria.posParams = this.posParams;
            criteria.startPos = this.startPos;
            criteria.maxResult = this.maxResult;
            return criteria;
        }
    }
}
