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

package com.adeptj.modules.data.jpa.dto;

import com.adeptj.modules.data.jpa.BaseEntity;
import com.adeptj.modules.data.jpa.query.QueryParam;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CrudDTO
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class CrudDTO<T extends BaseEntity> {

    private final Class<T> entity;

    private String namedQueryName;

    private String jpaQuery;

    private QueryParam[] queryParams;

    private int startPos;

    private int maxResult;

    private CrudDTO(Class<T> entity) {
        this.entity = entity;
    }

    public Class<T> getEntity() {
        return entity;
    }

    public String getNamedQueryName() {
        return namedQueryName;
    }

    public String getJpaQuery() {
        return jpaQuery;
    }

    public QueryParam[] getQueryParams() {
        return this.queryParams;
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
     * Builder for creating {@link CrudDTO}
     */
    public static class Builder<T extends BaseEntity> {

        private final Class<T> entity;

        private String namedQueryName;

        private String jpaQuery;

        private List<QueryParam> queryParams;

        private int startPos;

        private int maxResult;

        private Builder(Class<T> entity) {
            this.entity = entity;
        }

        public Builder<T> namedQueryName(String namedQueryName) {
            this.namedQueryName = namedQueryName;
            return this;
        }

        public Builder<T> jpaQuery(String jpaQuery) {
            this.jpaQuery = jpaQuery;
            return this;
        }

        public Builder<T> queryParam(QueryParam param) {
            if (this.queryParams == null) {
                this.queryParams = new ArrayList<>();
            }
            this.queryParams.add(param);
            return this;
        }

        public Builder<T> queryParams(QueryParam... params) {
            if (ArrayUtils.isNotEmpty(params)) {
                if (this.queryParams == null) {
                    this.queryParams = new ArrayList<>();
                }
                this.queryParams.addAll(Arrays.asList(params));
            }
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

        public CrudDTO<T> build() {
            CrudDTO<T> crudDTO = new CrudDTO<>(this.entity);
            crudDTO.namedQueryName = this.namedQueryName;
            crudDTO.jpaQuery = this.jpaQuery;
            if (this.queryParams != null) {
                crudDTO.queryParams = this.queryParams.toArray(new QueryParam[0]);
            }
            crudDTO.startPos = this.startPos;
            crudDTO.maxResult = this.maxResult;
            return crudDTO;
        }
    }
}
