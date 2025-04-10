package com.adobe.aem.guides.wknd.core.services.impl;
import com.adobe.aem.guides.wknd.core.services.SitemapService;
import com.adobe.granite.asset.api.AssetManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import javax.jcr.*;
import java.io.ByteArrayInputStream;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = SitemapService.class, immediate = true)
public class SitemapServiceImpl implements SitemapService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SitemapServiceImpl.class);

    /**
     *
     * @param resourceResolver
     * @return
     */
    @Override
    public String generateSitemap(ResourceResolver resourceResolver) {
        List<Page> pages = getAllPages(resourceResolver);

        StringBuilder sitemapXml = new StringBuilder();
        sitemapXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sitemapXml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        Set<String> addedPageUrls = new HashSet<>();

        for (Page page : pages) {
            String pageUrl = page.getPath();

            boolean isPageAndChildrenExcluded = isPageAndChildrenExcluded(page);
            boolean areChildrenExcluded = isPageExcluded(page);

            if (isPageAndChildrenExcluded) {
                if (!areChildrenExcluded) {
                    addChildPagesToSitemap(page, sitemapXml, addedPageUrls, true);
                }
                continue;
            } else {
                if (areChildrenExcluded) {
                    continue;
                } else {
                    addPageToSitemap(page, sitemapXml, addedPageUrls, false);
                    addChildPagesToSitemap(page, sitemapXml, addedPageUrls, false);
                }
            }
        }

        sitemapXml.append("</urlset>");
        return sitemapXml.toString();
    }

    /**
     *
     * @param resourceResolver
     * @return
     */
    private List<Page> getAllPages(ResourceResolver resourceResolver) {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager == null) {
            return new ArrayList<>();
        }

        List<Page> pages = new ArrayList<>();
        Page rootPage = pageManager.getPage("/content/wknd");

        if (rootPage != null) {
            pages.add(rootPage);
        }
        return pages;
    }

    private String getLastModifiedDate(Page page) {
        if (page.getLastModified() != null) {
            return page.getLastModified().getTime().toString();
        }
        return "Unknown";
    }

    private boolean isPageAndChildrenExcluded(Page page) {
        Boolean excludePageAndChildren = page.getProperties().get("sitemapExcludePageAndChildren", Boolean.class);
        return excludePageAndChildren != null && excludePageAndChildren;
    }

    private boolean isPageExcluded(Page parentPage) {
        Boolean excludePage = parentPage.getProperties().get("sitemapExcludePage", Boolean.class);
        return excludePage != null && excludePage;
    }

    private void addPageToSitemap(Page page, StringBuilder sitemapXml, Set<String> addedPageUrls, boolean excludeCurrentPage) {
        String pageUrl = page.getPath();
        if (excludeCurrentPage) {
            return;
        }
        if (!addedPageUrls.contains(pageUrl)) {
            sitemapXml.append("<url>");
            sitemapXml.append("<loc>").append(pageUrl).append("</loc>");
            sitemapXml.append("<lastmod>").append(getLastModifiedDate(page)).append("</lastmod>");
            sitemapXml.append("<changefreq>daily</changefreq>");
            sitemapXml.append("<priority>0.5</priority>");
            sitemapXml.append("</url>");
            addedPageUrls.add(pageUrl);
        }

    }

    private void addChildPagesToSitemap(Page parentPage, StringBuilder sitemapXml, Set<String> addedPageUrls, boolean excludeCurrentPage) {
        for (Iterator<Page> it = parentPage.listChildren(); it.hasNext(); ) {
            Page childPage = it.next();

            boolean isPageAndChildrenExcluded = isPageAndChildrenExcluded(childPage);
            boolean isPageExcluded = isPageExcluded(childPage);

            if (isPageAndChildrenExcluded || (excludeCurrentPage && isPageExcluded) || addedPageUrls.contains(childPage.getPath())) {
                continue;
            }

            addPageToSitemap(childPage, sitemapXml, addedPageUrls, isPageExcluded);
            addChildPagesToSitemap(childPage, sitemapXml, addedPageUrls, isPageExcluded);
        }
    }

    /**
     *
     * @param resolver
     * @param sitemapContent
     */
    @Override
    public void generateAndSaveSitemap(ResourceResolver resolver, String sitemapContent) {
        try {
            AssetManager assetManager = resolver.adaptTo(AssetManager.class);

            if (assetManager != null) {
                String parentPath = "/content/dam/sitemaps";
                String sitemapNodePath = parentPath + "/sitemap.xml";

                Resource resource = resolver.getResource(parentPath);
                if (resource == null) {
                    JcrUtils.getOrCreateByPath(parentPath, "sling:Folder", "sling:Folder", Objects.requireNonNull(resolver.adaptTo(Session.class)), true);
                    LOGGER.info("Created missing folder structure: {}", parentPath);
                }

                Session session = resolver.adaptTo(Session.class);
                if (session != null) {
                    Node parentNode = session.getNode(parentPath);

                    // Check if sitemap.xml already exists
                    if (parentNode.hasNode("sitemap.xml")) {
                        // If the file exists, update the content
                        Node fileNode = parentNode.getNode("sitemap.xml");
                        Node contentNode = fileNode.getNode("jcr:content");
                        contentNode.setProperty("jcr:data", session.getValueFactory().createBinary(new ByteArrayInputStream(sitemapContent.getBytes())));
                        contentNode.setProperty("jcr:mimeType", "text/xml");
                        contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
                        session.save();
                        LOGGER.info("Sitemap updated successfully at: {}", sitemapNodePath);
                    } else {
                        Node fileNode = parentNode.addNode("sitemap.xml", "nt:file");
                        Node contentNode = fileNode.addNode("jcr:content", "nt:resource");

                        contentNode.setProperty("jcr:data", session.getValueFactory().createBinary(new ByteArrayInputStream(sitemapContent.getBytes())));
                        contentNode.setProperty("jcr:mimeType", "text/xml");
                        contentNode.setProperty("jcr:lastModified", Calendar.getInstance());

                        session.save();
                        LOGGER.info("Sitemap file created successfully at: {}", sitemapNodePath);
                    }
                } else {
                    LOGGER.error("Session could not be obtained, sitemap creation failed.");
                }
            }
        } catch (Exception e){
            LOGGER.error("Exception at 169 : {}",e.getMessage(),e);
        }
    }
}
