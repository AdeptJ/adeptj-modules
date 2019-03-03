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

/**
 * Utility methods to deal with exceptions.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class Exceptions {

    private Exceptions() {
    }

    /**
     * Wraps the given exception in a {@link RuntimeException} and rethrow.
     *
     * @param ex the given exception.
     * @return RuntimeException wrapping the original exception.
     */
    public static RuntimeException unchecked(Exception ex) {
        return new RuntimeException(ex);
    }

    /**
     * Wraps the given exception in an {@link IllegalStateException} and rethrow.
     *
     * @param ex the given exception.
     * @return IllegalStateException wrapping the original exception.
     */
    public static IllegalStateException state(Exception ex) {
        return new IllegalStateException(ex);
    }

    /**
     * Wraps the given exception in an {@link IllegalArgumentException} and rethrow.
     *
     * @param ex the given exception.
     * @return IllegalArgumentException wrapping the original exception.
     */
    public static IllegalArgumentException arg(Exception ex) {
        return new IllegalArgumentException(ex);
    }
}
