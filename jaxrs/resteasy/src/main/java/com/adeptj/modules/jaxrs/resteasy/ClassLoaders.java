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

package com.adeptj.modules.jaxrs.resteasy;

/**
 * Class loading related utilities.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public class ClassLoaders {

    /**
     * Deny direct instantiation.
     */
    private ClassLoaders() {
    }

    /**
     * Defines a callback processor for an action which will be executed using the provided class loader.
     *
     * @author Rakesh.Kumar, AdeptJ
     */
    @FunctionalInterface
    public interface Callback {
        void execute();
    }

    /**
     * Executes the provided callback within the context of the specified class loader.
     *
     * @param cl the class loader to use as a context class loader for the execution
     * @param callback the execution callback handler
     */
    public static void executeWith(ClassLoader cl, Callback callback) {
        ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);
            callback.execute();
        } finally {
            Thread.currentThread().setContextClassLoader(contextCL);
        }
    }

}
