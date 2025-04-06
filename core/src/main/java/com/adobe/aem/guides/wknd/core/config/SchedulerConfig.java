package com.adobe.aem.guides.wknd.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;



@ObjectClassDefinition(name = "Sitemap Scheduler Configuration", description = "Configuration for Sitemap Scheduler")
public @interface SchedulerConfig {

    @AttributeDefinition(name = "Scheduler Cron Expression", description = "Cron expression for when the scheduler runs", type = AttributeType.STRING)
    public String schedulerName() default "Sitemap Scheduler";

    @AttributeDefinition(
            name="Cron Expression", description = "Cron",
            type= AttributeType.STRING
    )
    public String cronExpression() default "0 0 0 * * ?"; // Example: Runs daily at midnight
}
