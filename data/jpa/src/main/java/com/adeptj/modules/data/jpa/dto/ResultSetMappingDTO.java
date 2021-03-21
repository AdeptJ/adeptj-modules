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

import com.adeptj.modules.data.jpa.query.QueryParam;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ResultSetMappingDTO for feting entities using SQL ResultSet mapping.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResultSetMappingDTO {

    private String nativeQuery;

    private String resultSetMapping;

    private QueryParam[] queryParams;

    private ResultSetMappingDTO() {
    }

    public String getNativeQuery() {
        return nativeQuery;
    }

    public String getResultSetMapping() {
        return resultSetMapping;
    }

    public QueryParam[] getQueryParams() {
        return queryParams;
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating {@link ResultSetMappingDTO}
     */
    public static class Builder {

        // no public access as available through static method.
        private Builder() {
        }

        private String nativeQuery;

        private String resultSetMapping;

        private List<QueryParam> queryParams;

        public Builder nativeQuery(String nativeQuery) {
            this.nativeQuery = nativeQuery;
            return this;
        }

        public Builder resultSetMapping(String resultSetMapping) {
            this.resultSetMapping = resultSetMapping;
            return this;
        }

        public Builder queryParam(QueryParam param) {
            if (this.queryParams == null) {
                this.queryParams = new ArrayList<>();
            }
            this.queryParams.add(param);
            return this;
        }

        public Builder queryParams(QueryParam... params) {
            if (ArrayUtils.isNotEmpty(params)) {
                if (this.queryParams == null) {
                    this.queryParams = new ArrayList<>();
                }
                this.queryParams.addAll(Arrays.asList(params));
            }
            return this;
        }

        public ResultSetMappingDTO build() {
            ResultSetMappingDTO resultSetMappingDTO = new ResultSetMappingDTO();
            resultSetMappingDTO.nativeQuery = this.nativeQuery;
            resultSetMappingDTO.resultSetMapping = this.resultSetMapping;
            if (this.queryParams != null) {
                resultSetMappingDTO.queryParams = this.queryParams.toArray(new QueryParam[0]);
            }
            return resultSetMappingDTO;
        }
    }
}
