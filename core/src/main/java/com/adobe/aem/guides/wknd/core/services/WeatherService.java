package com.adobe.aem.guides.wknd.core.services;

import com.adobe.aem.guides.wknd.core.models.WeatherData;

public interface WeatherService {
    WeatherData getWeatherData(String cityName);
}
