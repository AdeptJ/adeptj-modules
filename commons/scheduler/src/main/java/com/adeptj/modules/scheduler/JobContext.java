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

package com.adeptj.modules.scheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * Contextual info for Job to be executed.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JobContext {

    private String jobName;

    private String jobClassName;

    private Map<String, Object> params;

    private boolean concurrencyAllowed;

    private JobContext(String jobName, String jobClassName) {
        this.jobName = jobName;
        this.jobClassName = jobClassName;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobClassName() {
        return jobClassName;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public boolean isConcurrencyAllowed() {
        return concurrencyAllowed;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Simple builder for JobContext
     */
    public static class Builder {

        private String jobName;

        private String jobClassName;

        private Map<String, Object> params;

        private boolean concurrencyAllowed;

        public Builder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder jobClassName(String jobClassName) {
            this.jobClassName = jobClassName;
            return this;
        }

        public Builder addParam(String paramName, Object paramValue) {
            if (this.params == null) {
                this.params = new HashMap<>();
            }
            this.params.put(paramName, paramValue);
            return this;
        }

        public Builder concurrencyAllowed(boolean concurrencyAllowed) {
            this.concurrencyAllowed = concurrencyAllowed;
            return this;
        }

        public JobContext build() {
            JobContext jobContext = new JobContext(this.jobName, this.jobClassName);
            jobContext.params = this.params;
            jobContext.concurrencyAllowed = this.concurrencyAllowed;
            return jobContext;
        }
    }
}
