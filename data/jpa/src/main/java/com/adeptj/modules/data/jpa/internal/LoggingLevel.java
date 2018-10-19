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

package com.adeptj.modules.data.jpa.internal;

/**
 * EclipseLink Log levels.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
final class LoggingLevel {

    private LoggingLevel() {
    }

    static final String OFF = "OFF";

    static final String SEVERE = "SEVERE";

    static final String WARNING = "WARNING";

    static final String INFO = "INFO";

    static final String CONFIG = "CONFIG";

    static final String FINE = "FINE";

    static final String FINER = "FINER";

    static final String FINEST = "FINEST";

    static final String ALL = "ALL";
}
