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

package com.adeptj.modules.data.jpa;

/**
 * Exception thrown by {@link JpaRepository} methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class PersistenceException extends RuntimeException {

    private static final long serialVersionUID = 7296926130485279382L;

    public PersistenceException(String msg) {
        super(msg);
    }

    public PersistenceException(Throwable throwable) {
        super(throwable);
    }

    public PersistenceException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
