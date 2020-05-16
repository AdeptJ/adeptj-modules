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

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

/**
 * Execute provided functions by setting current Thread's context class loader.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class Functions {

    /**
     * Only static access.
     */
    private Functions() {
    }

    /**
     * A function which will be executed by setting the provided class loader in current thread's
     * context class loader and returns a value.
     *
     * @param <T> the return type of the execute method
     * @author Rakesh.Kumar, AdeptJ
     */
    @FunctionalInterface
    public interface ValueFunction<T> {
        T execute();
    }

    /**
     * A function which will be executed by setting the provided class loader in current thread's
     * context class loader and returns nothing.
     *
     * @author Rakesh.Kumar, AdeptJ
     */
    @FunctionalInterface
    public interface VoidFunction {
        void execute();
    }

    /**
     * Executes the provided {@link VoidFunction} by setting the provided {@link ClassLoader} in current thread's
     * context class loader.
     *
     * @param cl       the class loader to use as a context class loader for the function execution
     * @param function the function to be executed under given class loader
     */
    public static void executeUnderContextClassLoader(ClassLoader cl, @NotNull Functions.VoidFunction function) {
        Validate.isTrue(cl != null, "ClassLoader can't be null!!");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);
            function.execute();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    /**
     * Executes the provided {@link ValueFunction} by setting the provided {@link ClassLoader} in current thread's
     * context class loader.
     *
     * @param cl       the class loader to use as a context class loader for the function execution
     * @param function the function to be executed under given class loader
     * @param <T>      Type that ReturningFunction returns.
     * @return the result of the execution
     */
    public static <T> T executeUnderContextClassLoader(ClassLoader cl, @NotNull Functions.ValueFunction<T> function) {
        Validate.isTrue(cl != null, "ClassLoader can't be null!!");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);
            return function.execute();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }
}
