package com.adobe.aem.guides.wknd.core.services.impl;

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
    public String generateSitemap(SlingHttpServletRequest request){
        // Get the list of pages
        List<Page> pages = getAllPages(request);

        StringBuilder sitemapXml = new StringBuilder();
        sitemapXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sitemapXml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        for (Page page : pages) {
            String pageUrl = page.getPath();
            sitemapXml.append("<url>");
            sitemapXml.append("<loc>").append(pageUrl).append("</loc>");
            sitemapXml.append("<lastmod>").append(getLastModifiedDate(page)).append("</lastmod>");
            sitemapXml.append("<changefreq>daily</changefreq>");
            sitemapXml.append("<priority>0.5</priority>");
            sitemapXml.append("</url>");
        }

        sitemapXml.append("</urlset>");

        return sitemapXml.toString();
        // Save the sitemap to JCR (DAM) or file system
//        saveSitemapToJCR(request, sitemapXml.toString());
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

//    private void saveSitemapToJCR(SlingHttpServletRequest request, String sitemapXml) throws IOException {
//        ResourceResolver resolver = request.getResourceResolver();
//        Session session = resolver.adaptTo(Session.class);
//
//        if (session == null) {
//            throw new IOException("Unable to obtain JCR session.");
//        }
//
//        try {
//            String jcrPath = "/content/dam/wknd/sitemap/sitemap.xml";
//            Node rootNode = session.getRootNode();
//
//            // Check if the path exists, if not create it
//            if (!rootNode.hasNode(jcrPath)) {
//                Node fileNode = rootNode.addNode(jcrPath, "nt:file");
//                Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
//                contentNode.setProperty("jcr:data", new ByteArrayInputStream(sitemapXml.getBytes()));
//                contentNode.setProperty("jcr:mimeType", "text/xml");
//                contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
//            }
//
//            session.save();  // Save the changes to the JCR
//
//        } catch (Exception e) {
//            throw new IOException("Error saving sitemap to JCR: " + e.getMessage(), e);
//        }
//    }
}
