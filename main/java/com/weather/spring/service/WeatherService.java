package com.weather.spring.service;

import com.weather.spring.model.WeatherData;

public interface WeatherService {
    WeatherData getWeatherByCity(String city) throws Exception;
}