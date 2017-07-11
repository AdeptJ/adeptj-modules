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
 * Class loading related utilities.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class ClassLoaders {

    /**
     * Only static access.
     */
    private ClassLoaders() {
    }

    /**
     * Defines a callback processor for an action which will be executed using the provided class loader.
     *
     * @param <T> the return type of the execute method
     * @author Rakesh.Kumar, AdeptJ
     */
    @FunctionalInterface
    public interface ReturnCallback<T> {
        T execute();
    }

    /**
     * Defines a callback processor for an action which will be executed using the provided class loader.
     *
     * @author Rakesh.Kumar, AdeptJ
     */
    @FunctionalInterface
    public interface NonReturnCallback {
        void execute();
    }

    /**
     * Executes the provided callback within the context of the specified {@link ClassLoader}.
     *
     * @param cl the class loader to use as a context class loader for the execution
     * @param callback the execution callback handler
     */
    public static void executeWith(ClassLoader cl, NonReturnCallback callback) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);
            callback.execute();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    /**
     * Executes the provided callback within the context of the specified {@link ClassLoader}.
     *
     * @param cl the class loader to use as a context class loader for the execution
     * @param callback the execution callback handler
     *
     * @return the result of the execution
     */
    public static <T> T executeWith(ClassLoader cl, ReturnCallback<T> callback) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);
            return callback.execute();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

}
