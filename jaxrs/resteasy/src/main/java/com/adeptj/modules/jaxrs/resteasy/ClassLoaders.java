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
    public static interface Callback {
        void execute();
    }

    /**
     * Executes the provided callback within the context of the specified class
     * loader.
     *
     * @param cl
     *            the class loader to use as a context class loader for the
     *            execution
     * @param callback
     *            the execution callback handler
     * @return the result of the execution
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
