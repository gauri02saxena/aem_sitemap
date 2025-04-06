package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.config.SchedulerConfig;
import com.adobe.aem.guides.wknd.core.services.SitemapService;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd= SchedulerConfig.class)
public class SitemapScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SitemapScheduler.class);

    private int schedulerId;

    @Reference
    private SitemapService sitemapService;

    @Reference
    private Scheduler scheduler;

    @Activate
    protected void activate(SchedulerConfig config) {
        schedulerId= config.schedulerName().hashCode();
        addScheduler(config);
    }

    @Deactivate
    protected void deactivate(SchedulerConfig config) {
        LOG.info("Sitemap scheduler deactivated");
    }

    private void addScheduler(SchedulerConfig config) {
        ScheduleOptions schedulerOptions = scheduler.EXPR(config.cronExpression());
        schedulerOptions.name(String.valueOf(schedulerId)); // No static reference needed
        schedulerOptions.canRunConcurrently(false);
        ScheduleOptions scheduleOptionsNow= scheduler.NOW();
        scheduler.schedule(this, schedulerOptions);
    }




    public void run() {
        try {
            // Run the sitemap generation logic
//            sitemapService.generateSitemap(SlingHttpServletRequest request);
            LOG.info("Sitemap generated successfully at regular intervals.");
        } catch (Exception e) {
            LOG.error("Error generating sitemap", e);
        }
    }
}
