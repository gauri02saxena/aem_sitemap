package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.services.SitemapService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import com.adobe.aem.guides.wknd.core.config.SitemapConfig; // Correct reference to your config class

@Component(immediate = true)
@Designate(ocd = SitemapConfig.class)  // Reference the correct configuration class
public class SitemapScheduler {

    @Reference
    private Scheduler scheduler;

    @Reference
    private SitemapService sitemapSchedulerService;

    @Reference
    private ResourceResolver resourceResolver;  // Used to interact with JCR

    @Activate
    protected void activate(SitemapConfig config) {
        // Define the cron expression (every minute)
        String cronExpression = config.scheduler_expression();

        // Create a Trigger based on the cron expression
        ScheduleOptions options = scheduler.EXPR(cronExpression);
        options.name(config.scheduler_name());
        options.canRunConcurrently(config.concurrent_scheduler());

        // Add the cron job to the scheduler
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                // This method will be triggered by the scheduler every minute
                updateJCRNode();
            }
        }, options);
    }

    @Deactivate
    protected void deactivate(SitemapConfig config) {
        // Unschedule the job when the component is deactivated
        scheduler.unschedule(config.scheduler_name());
    }

    private void updateJCRNode() {
        try {
            // Define the JCR path for storing content
            String nodePath = "/content/scheduled-task/test-node";

            // Create or get the resource at the specified path
            Resource resource = resourceResolver.getResource(nodePath);

            // If the node doesn't exist, create it
            if (resource == null) {
                resource = resourceResolver.create(resourceResolver.getResource("/content/scheduled-task"), "test-node", null);
                resourceResolver.commit();
            }

            // Get or create the properties map for the node
            ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
            if (properties != null) {
                // Store the current timestamp in the node
                properties.put("timestamp", System.currentTimeMillis());
                resourceResolver.commit();
                System.out.println("Node updated with timestamp: " + System.currentTimeMillis());
            } else {
                System.err.println("Could not adapt the resource to ModifiableValueMap.");
            }
        } catch (Exception e) {
            System.err.println("Error storing content in JCR: " + e.getMessage());
        }
    }
}
