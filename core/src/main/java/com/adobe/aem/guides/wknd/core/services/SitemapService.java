package com.adobe.aem.guides.wknd.core.services;

import org.apache.sling.api.SlingHttpServletRequest;

public interface SitemapService {
    String generateSitemap(SlingHttpServletRequest request);
}
