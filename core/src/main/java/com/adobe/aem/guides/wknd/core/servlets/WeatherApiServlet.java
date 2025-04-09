package com.adobe.aem.guides.wknd.core.servlets;
import com.adobe.aem.guides.wknd.core.models.WeatherData;
import com.adobe.aem.guides.wknd.core.services.WeatherService;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(service = {Servlet.class}, immediate = true,
        property = {
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/weatherapi"
        })
public class WeatherApiServlet extends SlingAllMethodsServlet {

    @Reference
    private WeatherService weatherService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String cityName = request.getParameter("cityName");

        if (cityName == null || cityName.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error: No city name provided");
            return;
        }

        WeatherData weatherData = weatherService.getWeatherData(cityName);

        if (weatherData == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: Could not fetch weather data");
            return;
        }

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(weatherData);

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }
}
