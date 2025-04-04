package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.services.IpService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;

import java.io.IOException;
import java.io.PrintWriter;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Component(service= {Servlet.class})
@SlingServletResourceTypes(
        resourceTypes = "wknd/components/page",
        methods = HttpConstants.METHOD_GET,

        extensions = "json"
)
public class NewServlet extends SlingAllMethodsServlet {

    @Reference
    private transient IpService ipservice;
    @Override
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String query= request.getParameter("query");
        PrintWriter out= response.getWriter();
        response.setContentType("text/html");
        out.print("<h1>Hello World</h1>");
        out.print(query);
        out.print("The IP Service"+ ipservice.getIp());
        Resource resource= request.getResource();
        out.print("Resource: "+ resource.getValueMap()); //for getting jcr properties of this recourse but this is a path not a jcr node

        out.flush();
        out.close();
    }

    @Override
    public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String query= request.getParameter("query");
        PrintWriter out= response.getWriter();
        response.setContentType("text/html");
        out.print("<h1>Hello World</h1>");
        out.print(query);
        out.print("The IP Service"+ ipservice.getIp());
        Resource resource= request.getResource();
        out.print("Resource: "+ resource.getValueMap()); //for getting jcr properties of this recourse but this is a path not a jcr node

        out.flush();
        out.close();
    }
}
