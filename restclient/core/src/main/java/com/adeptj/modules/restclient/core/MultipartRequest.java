/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://www.adeptj.com)                      #
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
package com.adeptj.modules.restclient.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rakesh Kumar, AdeptJ
 */
public class MultipartRequest {

    private Map<String, Part> parts;

    public void addPart(String key, Part part) {
        if (this.parts == null) {
            this.parts = new HashMap<>();
        }
        this.parts.put(key, part);
    }

    public Map<String, Part> getParts() {
        return parts;
    }
}
