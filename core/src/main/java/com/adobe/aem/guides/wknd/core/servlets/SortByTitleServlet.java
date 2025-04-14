package com.adobe.aem.guides.wknd.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.IOException;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Sort By Title Servlet",
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/sortByTitleServlet"
        })
public class SortByTitleServlet extends SlingAllMethodsServlet {

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("SortByTitleServlet called");

        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Servlet called successfully!\"}");
    }
}
