/* 
 * =============================================================================
 * 
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * =============================================================================
 */
package com.adeptj.modularweb.common;

/**
 * Class loading related utilities.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
public final class ClassLoaders {
	
	/**
	 * Initializes an instance of this class.
	 */
	private ClassLoaders() {
	}

	/**
	 * Defines a callback processor for an action which will be executed using
	 * the provided class loader.
	 * 
	 * @param <T>
	 *            the return type of the execution method
	 * @author Rakesh.Kumar, AdeptJ
	 */
	@FunctionalInterface
	public static interface Callback<T> {
		T execute();
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
	public static <T> T executeWith(ClassLoader cl, Callback<T> callback) {
		Thread current = Thread.currentThread();
		ClassLoader contextCL = current.getContextClassLoader();
		try {
			current.setContextClassLoader(cl);
			return callback.execute();
		} finally {
			current.setContextClassLoader(contextCL);
		}
	}

}
