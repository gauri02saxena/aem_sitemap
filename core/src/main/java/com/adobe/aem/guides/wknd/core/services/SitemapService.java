package com.adobe.aem.guides.wknd.core.services;
import org.apache.sling.api.resource.ResourceResolver;

public interface SitemapService {
    /**
     *
     * @param resourceResolver
     * @return
     */

    String generateSitemap(ResourceResolver resourceResolver);

    /**
     *
     * @param resourceResolver
     * @param sitemapContent
     */
    void generateAndSaveSitemap(ResourceResolver resourceResolver, String sitemapContent);
}
