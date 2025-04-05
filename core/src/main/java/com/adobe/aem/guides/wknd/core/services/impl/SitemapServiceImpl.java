package com.adobe.ae.m.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.SitemapService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component(service = SitemapService.class, immediate = true)
public class SitemapServiceImpl implements SitemapService {

    @Override
    public String generateSitemap(SlingHttpServletRequest request) {
        // Get the list of pages
        List<Page> pages = getAllPages(request);

        StringBuilder sitemapXml = new StringBuilder();
        sitemapXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sitemapXml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        for (Page page : pages) {
            String pageUrl = page.getPath();

            // Check if the page should be excluded based on the sitemapExclude property
            boolean isPageExcluded = isPageExcluded(page);
            if (isPageExcluded) {
                continue; // Skip this page if excluded
            }

            sitemapXml.append("<url>");
            sitemapXml.append("<loc>").append(pageUrl).append("</loc>");
            sitemapXml.append("<lastmod>").append(getLastModifiedDate(page)).append("</lastmod>");
            sitemapXml.append("<changefreq>daily</changefreq>");
            sitemapXml.append("<priority>0.5</priority>");
            sitemapXml.append("</url>");

            // If the page is not excluded, check and include child pages based on sitemapExcludeChildren
            if (!isChildPagesExcluded(page)) {
                addChildPagesToSitemap(page, sitemapXml);
            }
        }

        sitemapXml.append("</urlset>");

        return sitemapXml.toString();
    }

    private List<Page> getAllPages(SlingHttpServletRequest request) {
        ResourceResolver resolver = request.getResourceResolver();
        PageManager pageManager = resolver.adaptTo(PageManager.class);

        if (pageManager == null) {
            return new ArrayList<>();
        }

        List<Page> pages = new ArrayList<>();

        Page rootPage = pageManager.getPage("/content/wknd");

        if (rootPage != null) {
            findPagesRecursively(rootPage, pages);
        }

        return pages;
    }

    private void findPagesRecursively(Page page, List<Page> pages) {
        pages.add(page);

        for (Iterator<Page> it = page.listChildren(); it.hasNext(); ) {
            Page childPage = it.next();
            findPagesRecursively(childPage, pages);
        }
    }

    private String getLastModifiedDate(Page page) {
        if (page.getLastModified() != null) {
            return page.getLastModified().getTime().toString();
        }
        return "Unknown";
    }

    // Check if the page should be excluded from the sitemap
    private boolean isPageExcluded(Page page) {
        Boolean excludePage = page.getProperties().get("sitemapExclude", Boolean.class);
        return excludePage != null && excludePage;
    }

    // Check if child pages should be excluded based on the sitemapExcludeChildren property
    private boolean isChildPagesExcluded(Page page) {
        Boolean excludeChildren = page.getProperties().get("sitemapExcludeChildren", Boolean.class);
        return excludeChildren != null && excludeChildren;
    }

    // Add child pages to the sitemap recursively, but exclude them if needed
    private void addChildPagesToSitemap(Page parentPage, StringBuilder sitemapXml) {
        for (Iterator<Page> it = parentPage.listChildren(); it.hasNext(); ) {
            Page childPage = it.next();

            // If the child page is excluded, skip it
            if (isPageExcluded(childPage)) {
                continue;
            }

            sitemapXml.append("<url>");
            sitemapXml.append("<loc>").append(childPage.getPath()).append("</loc>");
            sitemapXml.append("<lastmod>").append(getLastModifiedDate(childPage)).append("</lastmod>");
            sitemapXml.append("<changefreq>daily</changefreq>");
            sitemapXml.append("<priority>0.5</priority>");
            sitemapXml.append("</url>");
        }
    }
}
