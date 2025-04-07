package com.adobe.aem.guides.wknd.core.servlets;


import com.adobe.aem.guides.wknd.core.services.SitemapService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;


@Component(service = Servlet.class, immediate = true,
        property = {Constants.SERVICE_DESCRIPTION + "=Sitemap Generator and Display Servlet",
                "sling.servlet.methods=GET",
                "sling.servlet.paths=/bin/sitemap.xml"})
public class SitemapServlet extends SlingAllMethodsServlet {

    @Reference
    private transient SitemapService sitemapService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        ResourceResolver resourceResolver= request.getResourceResolver();
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");


        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);


        String sitemapData= sitemapService.generateSitemap(resourceResolver);
        response.getWriter().write(sitemapData);
    }
}