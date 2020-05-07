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

package com.adeptj.modules.commons.utils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Utility for SLF4J {@link Logger}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class Loggers {

    // Static utility methods, no instances required.
    private Loggers() {
    }

    public static Logger of(@NotNull MethodHandles.Lookup lookup) {
        return LoggerFactory.getLogger(lookup.lookupClass());
    }

    public static <T> Logger of(Class<T> type) {
        return LoggerFactory.getLogger(type);
    }

    public static Logger of(String name) {
        return LoggerFactory.getLogger(name);
    }
}
