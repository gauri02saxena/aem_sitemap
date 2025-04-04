package com.adobe.aem.guides.wknd.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component(service = Servlet.class, immediate = true,
        property = {Constants.SERVICE_DESCRIPTION + "=Sitemap Generator and Display Servlet",
                "sling.servlet.methods=GET",
                "sling.servlet.paths=/bin/sitemap.xml"})
public class SitemapServlet extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // Generate the sitemap and store it in a file
        generateSitemapToFile(request, response);

        // Display the sitemap from the file
        displaySitemapFile(response);
    }

    private void generateSitemapToFile(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        List<Resource> pages = getAllPages(request);

        StringBuilder sitemapXml = new StringBuilder();
        sitemapXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sitemapXml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        for (Resource page : pages) {
            String pageUrl = page.getPath();
            sitemapXml.append("<url>");
            sitemapXml.append("<loc>").append(pageUrl).append("</loc>");
            sitemapXml.append("<lastmod>").append(getLastModifiedDate(page)).append("</lastmod>");
            sitemapXml.append("<changefreq>daily</changefreq>");
            sitemapXml.append("<priority>0.5</priority>");
            sitemapXml.append("</url>");
        }

        sitemapXml.append("</urlset>");

        // Specify the path where the sitemap file should be stored
        String sitemapFilePath = "/var/www/sitemaps/sitemap.xml"; // Use the directory already created on AEM
        File file = new File(sitemapFilePath);

        // Log the file path to verify
        response.getWriter().write("Sitemap will be saved to: " + sitemapFilePath + "\n");

        // Write the sitemap XML to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(sitemapXml.toString());
            response.getWriter().write("Sitemap file written successfully.\n");
        } catch (IOException e) {
            response.getWriter().write("Error writing the sitemap file: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void displaySitemapFile(SlingHttpServletResponse response) throws IOException {
        String sitemapFilePath = "/var/www/sitemaps/sitemap.xml"; // Path of the sitemap file
        File file = new File(sitemapFilePath);

        if (file.exists()) {
            // Read the file content and display it in the response
            try (FileReader fileReader = new FileReader(file)) {
                int character;
                while ((character = fileReader.read()) != -1) {
                    response.getWriter().write(character);
                }
            }
        } else {
            response.getWriter().write("Sitemap file not found.\n");
        }
    }

    private List<Resource> getAllPages(SlingHttpServletRequest request) {
        ResourceResolver resolver = request.getResourceResolver();

        String path = "/content"; // Path to start searching for pages

        Resource rootResource = resolver.getResource(path);

        List<Resource> pages = new ArrayList<>();

        if (rootResource != null) {
            findPagesRecursively(rootResource, pages);
        }

        return pages;
    }

    private void findPagesRecursively(Resource resource, List<Resource> pages) {
        if ("wknd/components/page".equals(resource.getResourceType())) {
            pages.add(resource);
        }

        Iterator<Resource> children = resource.listChildren();
        while (children.hasNext()) {
            Resource child = children.next();
            findPagesRecursively(child, pages);
        }
    }

    private String getLastModifiedDate(Resource page) {
        return "2025-01-01"; // Static date for now, can be dynamic based on the page
    }
}
