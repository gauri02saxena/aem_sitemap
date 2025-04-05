package com.adobe.aem.guides.wknd.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Sitemap Scheduler Configuration", description = "Configuration for the Sitemap Scheduler")
public @interface SitemapConfig {

    @AttributeDefinition(
            name = "Scheduler Name",
            description = "Name of the scheduler",
            type = AttributeType.STRING
    )
    String scheduler_name() default "SitemapScheduler";

    @AttributeDefinition(
            name = "Cron Expression",
            description = "Cron expression for job scheduling",
            type = AttributeType.STRING
    )
    String scheduler_expression() default "0 * * * * ?"; // Default cron expression: Every minute

    @AttributeDefinition(
            name = "Enable Scheduler",
            description = "Enable or Disable Scheduler",
            type = AttributeType.BOOLEAN
    )
    boolean enable_scheduler() default true;

    @AttributeDefinition(
            name = "Concurrent Scheduler",
            description = "Enable concurrent scheduler",
            type = AttributeType.BOOLEAN
    )
    boolean concurrent_scheduler() default false;

    @AttributeDefinition(
            name = "Custom Property",
            description = "Custom property for testing",
            type = AttributeType.STRING
    )
    String customProperty() default "Test";
}
