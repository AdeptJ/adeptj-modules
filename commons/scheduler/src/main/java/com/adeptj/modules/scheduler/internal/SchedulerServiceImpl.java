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

package com.adeptj.modules.scheduler.internal;

import com.adeptj.modules.scheduler.JobContext;
import com.adeptj.modules.scheduler.SchedulerConfig;
import com.adeptj.modules.scheduler.api.SchedulerService;
import org.knowm.sundial.SundialJobScheduler;
import org.knowm.sundial.exceptions.SundialSchedulerException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of service interface {@link SchedulerService}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = SchedulerConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class SchedulerServiceImpl implements SchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Override
    public void addJob(JobContext jobContext) {
        SundialJobScheduler.addJob(jobContext.getJobName(),
                jobContext.getJobClassName(),
                jobContext.getParams(),
                jobContext.isConcurrencyAllowed());
    }

    @Override
    public void addCronTrigger(String triggerName, String jobName, String cronExpression) {
        SundialJobScheduler.addCronTrigger(triggerName, jobName, cronExpression);
    }

    @Override
    public void addSimpleTrigger(String triggerName, String jobName, int repeatCount, long repeatInterval) {
        SundialJobScheduler.addSimpleTrigger(triggerName, jobName, repeatCount, repeatInterval);
    }

    // Component Lifecycle methods.

    @Activate
    protected void start(SchedulerConfig config) {
        try {
            SundialJobScheduler.startScheduler(config.maxThreads());
        } catch (SundialSchedulerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Deactivate
    protected void stop() {
        try {
            SundialJobScheduler.shutdown();
        } catch (SundialSchedulerException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
