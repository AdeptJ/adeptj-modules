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

import com.adeptj.modules.scheduler.SchedulerConfig;
import com.adeptj.modules.scheduler.api.SchedulerService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.DirectSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * QuartzSchedulerService
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = SchedulerConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class QuartzSchedulerService implements SchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzSchedulerService.class);

    private Scheduler scheduler;

    @Override
    public void schedule(String cronExpr, Runnable job) {
        if (!CronExpression.isValidExpression(cronExpr)) {
            throw new IllegalStateException("Cron expression is invalid!!");
        }
        try {
            this.scheduler.scheduleJob(JobBuilder.newJob()
                    .ofType(Job.class)
                    .usingJobData(null)
                    .build(), TriggerBuilder
                    .newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpr))
                    .build());
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public void schedule(LocalDateTime dateTime) {

    }

    // Component Lifecycle methods.

    @Activate
    protected void start(SchedulerConfig config) {
        try {
            DirectSchedulerFactory schedulerFactory = DirectSchedulerFactory.getInstance();
            schedulerFactory.createVolatileScheduler(config.maxThreads());
            this.scheduler = schedulerFactory.getScheduler();
            this.scheduler.start();
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Deactivate
    protected void stop() {
        try {
            this.scheduler.shutdown(true);
        } catch (SchedulerException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
