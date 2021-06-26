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

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link SessionCustomizer} for setting the query retry attempt count to {@link DatabaseLogin}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class QueryRetryCustomizer implements SessionCustomizer {

    private final int queryRetryAttemptCount;

    QueryRetryCustomizer(int queryRetryAttemptCount) {
        this.queryRetryAttemptCount = queryRetryAttemptCount;
    }

    @Override
    public void customize(@NotNull Session session) {
        DatabaseLogin datasourceLogin = (DatabaseLogin) session.getDatasourceLogin();
        datasourceLogin.setQueryRetryAttemptCount(this.queryRetryAttemptCount);
    }
}
