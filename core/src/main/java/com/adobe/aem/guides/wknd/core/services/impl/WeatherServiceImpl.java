package com.adobe.aem.guides.wknd.core.services.impl;
import com.adobe.aem.guides.wknd.core.config.WeatherApiConfig;
import com.adobe.aem.guides.wknd.core.models.WeatherData;
import com.adobe.aem.guides.wknd.core.services.WeatherService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component(service = WeatherService.class, immediate = true)
@Designate(ocd = WeatherApiConfig.class)
public class WeatherServiceImpl implements WeatherService {

    private String apiUrl;
    private String apiKey;

    @Activate
    public void activate(WeatherApiConfig config) {
        this.apiKey = config.apiKey();
        this.apiUrl = config.apiUrl();
    }

    @Override
    public WeatherData getWeatherData(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            return null;
        }

        if (apiKey == null || apiKey.isEmpty()) {
            return null;
        }

        try {
            String apiUrlWithCity = apiUrl + "?q=" + cityName + "&appid=" + apiKey;
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrlWithCity).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(result.toString(), JsonObject.class);
            JsonObject main = jsonResponse.getAsJsonObject("main");
            JsonObject wind = jsonResponse.getAsJsonObject("wind");
            JsonArray weatherArray = jsonResponse.getAsJsonArray("weather");

            String weatherCondition = "Unknown";
            if (weatherArray != null && weatherArray.size() > 0) {
                JsonObject weatherObj = weatherArray.get(0).getAsJsonObject();
                weatherCondition = weatherObj.get("description").getAsString();
            }

            double temperature = main.get("temp").getAsDouble();
            double windSpeed = wind.get("speed").getAsDouble();

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String currentTimestamp = sdf.format(new Date());
            currentTimestamp = currentTimestamp + " IST";


            WeatherData weatherData = new WeatherData();
            weatherData.setCity(cityName);
            weatherData.setTemperature(temperature);
            weatherData.setWindSpeed(windSpeed);
            weatherData.setWeatherCondition(weatherCondition);
            weatherData.setTimestamp(currentTimestamp);

            return weatherData;

        } catch (IOException e) {
            e.getMessage();
            return null;
        }
    }
}
