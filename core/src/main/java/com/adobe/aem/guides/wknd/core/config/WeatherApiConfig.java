package com.adobe.aem.guides.wknd.core.config;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Weather API Configuration", description = "Configuration for the OpenWeather API")
public @interface WeatherApiConfig {

    @AttributeDefinition(name = "API Key", description = "The API key for accessing the OpenWeather API")
    String apiKey() default "";

    @AttributeDefinition(name = "API URL", description = "The base URL for the OpenWeather API")
    String apiUrl() default "http://api.openweathermap.org/data/2.5/weather";
}
