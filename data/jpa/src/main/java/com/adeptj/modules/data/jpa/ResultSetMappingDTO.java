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
import java.util.List;

/**
 * ResultSetMappingDTO for feting entities using SQL ResultSet mapping.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class ResultSetMappingDTO {

    private String nativeQuery;

    private String resultSetMapping;

    private List<Object> posParams;

    private ResultSetMappingDTO() {
    }

    public String getNativeQuery() {
        return nativeQuery;
    }

    public String getResultSetMapping() {
        return resultSetMapping;
    }

    public List<Object> getPosParams() {
        return posParams;
    }

    public static Builder builder() {
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

        private List<Object> posParams;

        public Builder nativeQuery(String nativeQuery) {
            this.nativeQuery = nativeQuery;
            return this;
        }

        public Builder resultSetMapping(String resultSetMapping) {
            this.resultSetMapping = resultSetMapping;
            return this;
        }

        public Builder addPosParam(Object param) {
            if (this.posParams == null) {
                this.posParams = new ArrayList<>();
            }
            this.posParams.add(param);
            return this;
        }

        public Builder addPosParams(Object... params) {
            if (this.posParams == null) {
                this.posParams = new ArrayList<>();
            }
            this.posParams.addAll(Arrays.asList(params));
            return this;
        }

        public ResultSetMappingDTO build() {
            ResultSetMappingDTO resultSetMappingDTO = new ResultSetMappingDTO();
            resultSetMappingDTO.nativeQuery = this.nativeQuery;
            resultSetMappingDTO.resultSetMapping = this.resultSetMapping;
            resultSetMappingDTO.posParams = this.posParams;
            return resultSetMappingDTO;
        }
    }
}
