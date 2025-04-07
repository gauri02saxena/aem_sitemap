package com.adobe.aem.guides.wknd.core.services;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

public interface SitemapService {
    String generateSitemap(ResourceResolver resourceResolver);
    void generateAndSaveSitemap(ResourceResolver resourceResolver, String sitemapContent);
}
