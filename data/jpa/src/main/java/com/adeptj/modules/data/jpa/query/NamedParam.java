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

package com.adeptj.modules.data.jpa.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Named parameter based QueryParam.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class NamedParam extends AbstractQueryParam {

    private final String name;

    public NamedParam(String name, Object value) {
        super(value);
        Validate.isTrue(StringUtils.isNotEmpty(name), "Query bind parameter name can't be null or empty!");
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
