package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.services.SitemapService;
import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;


import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component(service = SitemapService.class, immediate = true)
public class SitemapServiceImpl implements SitemapService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SitemapServiceImpl.class);


    private static final String FILE_PATH = "/content/sitemaps/sitemap.xml";
    private static final String FOLDER_PATH = "/content/sitemaps";

//    @Override
//    public String generateSitemap(ResourceResolver resourceResolver) {
//        List<Page> pages = getAllPages(resourceResolver);
//
//        StringBuilder sitemapXml = new StringBuilder();
//        sitemapXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//        sitemapXml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
//
//        Set<String> addedPageUrls = new HashSet<>();
//
//        for (Page page : pages) {
//            String pageUrl = page.getPath();
//
//            boolean isPageExcluded = isPageExcluded(page);
//            if (isPageExcluded || addedPageUrls.contains(pageUrl)) {
//                continue;
//            }
//
//            sitemapXml.append("<url>");
//            sitemapXml.append("<loc>").append(pageUrl).append("</loc>");
//            sitemapXml.append("<lastmod>").append(getLastModifiedDate(page)).append("</lastmod>");
//            sitemapXml.append("<changefreq>daily</changefreq>");
//            sitemapXml.append("<priority>0.5</priority>");
//            sitemapXml.append("</url>");
//            addedPageUrls.add(pageUrl); // Mark this page as added
//
//            if (!isChildPagesExcluded(page)) {
//                addChildPagesToSitemap(page, sitemapXml, addedPageUrls);
//            }
//        }
//
//        sitemapXml.append("</urlset>");
//
//        return sitemapXml.toString();
//    }
//
//    private List<Page> getAllPages(ResourceResolver resourceResolver) {
//
//        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
//
//        if (pageManager == null) {
//            return new ArrayList<>();
//        }
//
//        List<Page> pages = new ArrayList<>();
//
//        Page rootPage = pageManager.getPage("/content/wknd");
//
//        if (rootPage != null) {
//            findPagesRecursively(rootPage, pages);
//        }
//
//        return pages;
//    }
//
//    private void findPagesRecursively(Page page, List<Page> pages) {
//        pages.add(page);
//
//        for (Iterator<Page> it = page.listChildren(); it.hasNext(); ) {
//            Page childPage = it.next();
//            findPagesRecursively(childPage, pages);
//        }
//    }
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
        if (isPageExcluded || addedPageUrls.contains(pageUrl)) {
            continue;
        }

        sitemapXml.append("<url>");
        sitemapXml.append("<loc>").append(pageUrl).append("</loc>");
        sitemapXml.append("<lastmod>").append(getLastModifiedDate(page)).append("</lastmod>");
        sitemapXml.append("<changefreq>daily</changefreq>");
        sitemapXml.append("<priority>0.5</priority>");
        sitemapXml.append("</url>");
        addedPageUrls.add(pageUrl); // Mark this page as added

        // Only process children for the current page (not for its ancestors)
        addChildPagesToSitemap(page, sitemapXml, addedPageUrls);
    }

    sitemapXml.append("</urlset>");

    return sitemapXml.toString();
}

    private List<Page> getAllPages(ResourceResolver resourceResolver) {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager == null) {
            return new ArrayList<>();
        }

        List<Page> pages = new ArrayList<>();
        Page rootPage = pageManager.getPage("/content/wknd");

        if (rootPage != null) {
            // Only retrieve root pages, not child pages, as `addChildPagesToSitemap` will handle them
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

//    private boolean isChildPagesExcluded(Page page) {
//        Boolean excludeChildren = page.getProperties().get("sitemapExcludeChildren", Boolean.class);
//        return excludeChildren != null && excludeChildren;
//    }

    private boolean isChildPagesExcluded(Page parentPage) {
        Boolean excludeChildren = parentPage.getProperties().get("sitemapExcludeChildren", Boolean.class);
        return excludeChildren != null && excludeChildren;
    }

    private void addChildPagesToSitemap(Page parentPage, StringBuilder sitemapXml, Set<String> addedPageUrls) {
        // Check if children should be excluded for the parent page before proceeding
        boolean areChildrenExcluded = isChildPagesExcluded(parentPage);

        if (areChildrenExcluded) {
            return; // Skip processing children if the parent has excluded them
        }

        for (Iterator<Page> it = parentPage.listChildren(); it.hasNext(); ) {
            Page childPage = it.next();

            if (isPageExcluded(childPage) || addedPageUrls.contains(childPage.getPath())) {
                continue;
            }

            // Add the child page to the sitemap if it is not excluded
            sitemapXml.append("<url>");
            sitemapXml.append("<loc>").append(childPage.getPath()).append("</loc>");
            sitemapXml.append("<lastmod>").append(getLastModifiedDate(childPage)).append("</lastmod>");
            sitemapXml.append("<changefreq>daily</changefreq>");
            sitemapXml.append("<priority>0.5</priority>");
            sitemapXml.append("</url>");
            addedPageUrls.add(childPage.getPath());

            // Recursively add child pages of this child page
            addChildPagesToSitemap(childPage, sitemapXml, addedPageUrls);
        }
    }


//    private void addChildPagesToSitemap(Page parentPage, StringBuilder sitemapXml, Set<String> addedPageUrls) {
//        boolean areChildrenExcluded = isChildPagesExcluded(parentPage);
//
//        if (areChildrenExcluded) {
//            return;
//        }
//
//        for (Iterator<Page> it = parentPage.listChildren(); it.hasNext(); ) {
//            Page childPage = it.next();
//
//            if (isPageExcluded(childPage) || addedPageUrls.contains(childPage.getPath())) {
//                continue;
//            }
//
//            sitemapXml.append("<url>");
//            sitemapXml.append("<loc>").append(childPage.getPath()).append("</loc>");
//            sitemapXml.append("<lastmod>").append(getLastModifiedDate(childPage)).append("</lastmod>");
//            sitemapXml.append("<changefreq>daily</changefreq>");
//            sitemapXml.append("<priority>0.5</priority>");
//            sitemapXml.append("</url>");
//            addedPageUrls.add(childPage.getPath());
//
//            addChildPagesToSitemap(childPage, sitemapXml, addedPageUrls);
//        }
//    }

    @Override
    public void generateAndSaveSitemap(ResourceResolver resolver, String sitemapContent) {
        try {
            // Get the AssetManager from the ResourceResolver
            AssetManager assetManager = resolver.adaptTo(AssetManager.class);

            if (assetManager != null) {
                String parentPath = "/content/dam/sitemaps";
                String sitemapNodePath = parentPath + "/sitemap.xml";

                // Check if the sitemaps folder exists, if not, create it
                Resource resource = resolver.getResource(parentPath);
                if (resource == null) {
                    // Create the sitemaps folder if it doesn't exist
                    JcrUtils.getOrCreateByPath(parentPath, "sling:Folder", "nt:unstructured", resolver.adaptTo(Session.class), true);
                    LOGGER.info("Created missing folder structure: {}", parentPath);
                }

                // Check if the sitemap.xml asset already exists in the DAM
                Asset existingAsset = assetManager.getAsset(sitemapNodePath);

                if (existingAsset != null) {
                    // If the asset exists, update its content using ModifiableValueMap
                    ModifiableValueMap properties = existingAsset.adaptTo(ModifiableValueMap.class);
                    properties.put("jcr:data", sitemapContent.getBytes()); // Update content
                    existingAsset.adaptTo(Node.class).setProperty("jcr:data", Arrays.toString(sitemapContent.getBytes()));
                    resolver.commit(); // Commit the changes to the repository
                    LOGGER.info("Sitemap updated successfully at: {}", sitemapNodePath);
                } else {
                    // If the asset doesn't exist, create a new one
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



//    @Override
//
//    public void generateAndSaveSitemap(ResourceResolver resolver, String sitemapContent) {
//
//        try {
//
//            if (resolver == null || !resolver.isLive()) {
//
//                throw new IllegalArgumentException("Invalid or inactive resolver");
//
//            }
//
//            Resource damRoot = resolver.getResource("/content/dam");
//
//            if (damRoot == null || !damRoot.isResourceType("sling:OrderedFolder")) {
//
//                throw new IllegalArgumentException("Invalid or missing /content/dam");
//
//            }
//
//            Session session = resolver.adaptTo(Session.class);
//
//            if (session == null) {
//
//                throw new RepositoryException("Session is null");
//
//            }
//
//            String parentPath = "/content/dam/sitemaps";
//
//            Node parentNode = JcrUtils.getOrCreateByPath(parentPath, "sling:OrderedFolder", "nt:unstructured", session, true);
//
//            String sitemapNodePath = parentPath + "/sitemap.xml";
//
//            Node sitemapNode;
//
//            if (parentNode.hasNode("sitemap.xml")) {
//
//                sitemapNode = parentNode.getNode("sitemap.xml");
//
//                Node contentNode = sitemapNode.getNode("jcr:content");
//
//                contentNode.setProperty("jcr:data", new ByteArrayInputStream(sitemapContent.getBytes(StandardCharsets.UTF_8)));
//
//                contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
//
//            } else {
//
//                sitemapNode = parentNode.addNode("sitemap.xml", "nt:file");
//
//                Node contentNode = sitemapNode.addNode("jcr:content", "nt:resource");
//
//                contentNode.setProperty("jcr:mimeType", "application/xml");
//
//                contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
//
//                contentNode.setProperty("jcr:data", new ByteArrayInputStream(sitemapContent.getBytes(StandardCharsets.UTF_8)));
//
//            }
//
//            session.save();
//
//            LOGGER.info("Sitemap saved successfully at: {}", sitemapNodePath);
//
//        } catch (RepositoryException e) {
//
//            LOGGER.error("Error while saving sitemap: {}", e.getMessage(), e);
//
//        }
//
//    }





}
