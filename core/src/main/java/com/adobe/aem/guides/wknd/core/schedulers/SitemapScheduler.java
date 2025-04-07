package com.adobe.aem.guides.wknd.core.schedulers;

import com.adobe.aem.guides.wknd.core.config.SchedulerConfig;
import com.adobe.aem.guides.wknd.core.services.SitemapService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;

import java.util.HashMap;
import java.util.Map;


@Component(service = Runnable.class, immediate = true)
@Designate(ocd = SchedulerConfig.class)
public class SitemapScheduler implements Runnable {

    private static final String SERVICE_USER = "sitemapServiceUser5";

    @Reference
    private SitemapService sitemapService;

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private int schedulerId;

    @Activate
    protected void activate(SchedulerConfig config) {
        System.out.println("Sitemap scheduler activated");

        schedulerId = config.schedulerName().hashCode();
        addScheduler(config);
    }

    @Deactivate
    protected void deactivate(SchedulerConfig config) {
        System.out.println("Sitemap scheduler deactivated");
    }

    private void addScheduler(SchedulerConfig config) {
        ScheduleOptions schedulerOptions = scheduler.EXPR(config.cronExpression());
        schedulerOptions.name("SitemapScheduler");
        schedulerOptions.canRunConcurrently(false);

        scheduler.schedule(this, schedulerOptions);
        System.out.println("Sitemap scheduler activated with cron expression: " + config.cronExpression());
    }

    @Override
    public void run() {
        ResourceResolver resourceResolver=null;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put(ResourceResolverFactory.SUBSERVICE, SERVICE_USER);
//            params.put("password", "admin");
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(params);
            String sitemapContent = sitemapService.generateSitemap(resourceResolver);
           sitemapService.generateAndSaveSitemap(resourceResolver, sitemapContent);
            System.out.println(sitemapContent);
            System.out.println("Sitemap generated and stored successfully.");
        } catch (Exception e) {
            System.out.println("Error during scheduled sitemap generation" + e.getMessage());
        }
    }
}
