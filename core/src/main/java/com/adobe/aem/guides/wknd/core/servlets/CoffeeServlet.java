package com.adobe.aem.guides.wknd.core.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component(service= {Servlet.class})
@SlingServletResourceTypes(
        resourceTypes = "wknd/components/coffee-component",
        methods= HttpConstants.METHOD_POST,
        selectors = "coffee",
        extensions = "json"
)
public class CoffeeServlet extends SlingAllMethodsServlet {

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
//        Resource resource= request.getResource();
//        ValueMap properties= resource.getValueMap();
//        String id= properties.get("coffeeid", String.class);

        //getting id from request parameter
        String id= request.getParameter("id");
        if(id== null || id.isEmpty())
        {
            response.getWriter().write("Error, no id provided");
            return;
        }

        try{
            int coffeeIndex= Integer.parseInt(id);
            URL url=new URL("https://api.sampleapis.com/coffee/hot");
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result= new StringBuilder();
            String line;
            while((line=reader.readLine())!= null)
            {
                result.append(line);
            }
            reader.close();

            //method 1
            JsonElement coffeeData= JsonParser.parseString(result.toString());
            if(coffeeData.isJsonArray())
            {
                JsonArray coffeeArray=coffeeData.getAsJsonArray();
                JsonObject found=null;
                for(JsonElement e : coffeeArray)
                {
                    JsonObject coffeeObject= e.getAsJsonObject();
                    if(coffeeObject.has("id") &&  coffeeObject
                            .get("id").getAsInt()== coffeeIndex)
                    {
                        found=coffeeObject;
                        break;
                    }
                }
                if(found!=null)
                {
                    response.setContentType("application/json");
                    response.getWriter().write(found.toString());
                }
                else{
                    response.getWriter().write("Coffee id not found");
                }

//                if(coffeeIndex>0 && coffeeIndex<= coffeeArray.size())
//                {
//                    response.setContentType("application/json");
//                    response.getWriter().write(coffeeArray.get(coffeeIndex).toString());
//                }
//                else{
//                    response.getWriter().write("Invalid coffee id");
//                }
            }
            else{
                response.getWriter().write("Invalid response format");
            }

        }
        catch(Exception e)
        {
            response.getWriter().write("Error fetching api");
        }
    }

}
