package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.SitemapService;
import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import javax.jcr.*;
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

            boolean isPageExcluded = isPageExcluded(page);
            boolean areChildrenExcluded = isChildPagesExcluded(page);

            if (isPageExcluded) {
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

    private boolean isPageExcluded(Page page) {
        Boolean excludePage = page.getProperties().get("sitemapExclude", Boolean.class);
        return excludePage != null && excludePage;
    }

    private boolean isChildPagesExcluded(Page parentPage) {
        Boolean excludeChildren = parentPage.getProperties().get("sitemapExcludeChildren", Boolean.class);
        return excludeChildren != null && excludeChildren;
    }

    private void addPageToSitemap(Page page, StringBuilder sitemapXml, Set<String> addedPageUrls, boolean excludeCurrentPage) {
        String pageUrl = page.getPath();

        if (!addedPageUrls.contains(pageUrl)) {
            sitemapXml.append("<url>");
            sitemapXml.append("<loc>").append(pageUrl).append("</loc>");
            sitemapXml.append("<lastmod>").append(getLastModifiedDate(page)).append("</lastmod>");
            sitemapXml.append("<changefreq>daily</changefreq>");
            sitemapXml.append("<priority>0.5</priority>");
            sitemapXml.append("</url>");
            addedPageUrls.add(pageUrl);
        }

        if (excludeCurrentPage) {
            return;
        }
    }

    private void addChildPagesToSitemap(Page parentPage, StringBuilder sitemapXml, Set<String> addedPageUrls, boolean excludeCurrentPage) {
        for (Iterator<Page> it = parentPage.listChildren(); it.hasNext(); ) {
            Page childPage = it.next();

            boolean isChildExcluded = isPageExcluded(childPage);
            boolean areChildrenExcluded = isChildPagesExcluded(childPage);

            if (isChildExcluded || (excludeCurrentPage && areChildrenExcluded) || addedPageUrls.contains(childPage.getPath())) {
                continue;
            }

            addPageToSitemap(childPage, sitemapXml, addedPageUrls, false);
            addChildPagesToSitemap(childPage, sitemapXml, addedPageUrls, excludeCurrentPage);
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
                    JcrUtils.getOrCreateByPath(parentPath, "sling:Folder", "nt:unstructured", resolver.adaptTo(Session.class), true);
                    LOGGER.info("Created missing folder structure: {}", parentPath);
                }

                Asset existingAsset = assetManager.getAsset(sitemapNodePath);

                if (existingAsset != null) {
                    ModifiableValueMap properties = existingAsset.adaptTo(ModifiableValueMap.class);
                    properties.put("jcr:data", sitemapContent.getBytes());
                    existingAsset.adaptTo(Node.class).setProperty("jcr:data", Arrays.toString(sitemapContent.getBytes()));
                    resolver.commit();
                    LOGGER.info("Sitemap updated successfully at: {}", sitemapNodePath);
                } else {
                    Asset newAsset = assetManager.createAsset(sitemapNodePath);
                    LOGGER.info("Sitemap saved successfully at: {}", sitemapNodePath);
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error while saving sitemap: {}", e.getMessage(), e);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }
}
