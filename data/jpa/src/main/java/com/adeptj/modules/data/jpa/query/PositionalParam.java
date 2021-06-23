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

import org.apache.commons.lang3.Validate;

/**
 * Positional parameter based QueryParam.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class PositionalParam extends AbstractQueryParam {

    /**
     * Note: Query parameter position always starts with 1 therefore it should always be greater than zero.
     */
    private final int position;

    public PositionalParam(int position, Object value) {
        super(value);
        Validate.isTrue((position > 0), "Query bind parameter position must be greater than zero!");
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
