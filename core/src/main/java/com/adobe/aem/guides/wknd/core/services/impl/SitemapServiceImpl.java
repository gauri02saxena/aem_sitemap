package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.SitemapService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Component(service = SitemapService.class, immediate = true)
public class SitemapServiceImpl implements SitemapService {

    @Override
    public String generateSitemap(SlingHttpServletRequest request) {
        // Get the list of pages
        List<Page> pages = getAllPages(request);

        StringBuilder sitemapXml = new StringBuilder();
        sitemapXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sitemapXml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        // Set to track pages already added to the sitemap
        Set<String> addedPageUrls = new HashSet<>();

        for (Page page : pages) {
            String pageUrl = page.getPath();

            // Check if the page should be excluded based on the sitemapExclude property
            boolean isPageExcluded = isPageExcluded(page);
            if (isPageExcluded || addedPageUrls.contains(pageUrl)) {
                continue; // Skip this page if excluded or already added
            }

            // Add the page to the sitemap and mark it as added
            sitemapXml.append("<url>");
            sitemapXml.append("<loc>").append(pageUrl).append("</loc>");
            sitemapXml.append("<lastmod>").append(getLastModifiedDate(page)).append("</lastmod>");
            sitemapXml.append("<changefreq>daily</changefreq>");
            sitemapXml.append("<priority>0.5</priority>");
            sitemapXml.append("</url>");
            addedPageUrls.add(pageUrl); // Mark this page as added

            // Check if the page's child pages should be excluded based on sitemapExcludeChildren
            if (!isChildPagesExcluded(page)) {
                // If child pages are not excluded, add them to the sitemap
                addChildPagesToSitemap(page, sitemapXml, addedPageUrls);
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

    private void addChildPagesToSitemap(Page parentPage, StringBuilder sitemapXml, Set<String> addedPageUrls) {
        // First, check if the parent page has the "sitemapExcludeChildren" property set to true
        boolean areChildrenExcluded = isChildPagesExcluded(parentPage);

        if (areChildrenExcluded) {
            return; // If child pages are excluded, do not process any child pages
        }

        // If child pages are not excluded, recursively add them to the sitemap
        for (Iterator<Page> it = parentPage.listChildren(); it.hasNext(); ) {
            Page childPage = it.next();

            // If the child page itself is excluded or already added, skip it
            if (isPageExcluded(childPage) || addedPageUrls.contains(childPage.getPath())) {
                continue;
            }

            // Ensure child page is added only if not excluded
            sitemapXml.append("<url>");
            sitemapXml.append("<loc>").append(childPage.getPath()).append("</loc>");
            sitemapXml.append("<lastmod>").append(getLastModifiedDate(childPage)).append("</lastmod>");
            sitemapXml.append("<changefreq>daily</changefreq>");
            sitemapXml.append("<priority>0.5</priority>");
            sitemapXml.append("</url>");
            addedPageUrls.add(childPage.getPath()); // Mark the child page as added

            // Recursively check and add child pages of this child page if not excluded
            addChildPagesToSitemap(childPage, sitemapXml, addedPageUrls);
        }
    }

}
