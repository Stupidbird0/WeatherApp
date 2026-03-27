package com.weather.spring.service;

import com.weather.spring.model.WeatherData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

@Service
public class OpenMeteoService implements WeatherService {
    
    @Override
    public WeatherData getWeatherByCity(String city) throws Exception {
        // Step 1: Geocoding
        String geoUrl = String.format(
            "https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1&language=it&format=json",
            URLEncoder.encode(city, "UTF-8")
        );
        
        String geoResponse = callApi(geoUrl);
        JSONObject geoJson = new JSONObject(geoResponse);
        
        if (!geoJson.has("results")) {
            throw new Exception("Città non trovata: " + city);
        }
        
        JSONArray results = geoJson.getJSONArray("results");
        if (results.isEmpty()) {
            throw new Exception("Nessun risultato per: " + city);
        }
        
        JSONObject location = results.getJSONObject(0);
        double latitude = location.getDouble("latitude");
        double longitude = location.getDouble("longitude");
        String cityName = location.getString("name");
        
        // Step 2: Dati meteo
        String weatherUrl = String.format(Locale.US,
            "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current_weather=true&hourly=temperature_2m,relative_humidity_2m,apparent_temperature,windspeed_10m",
            latitude, longitude
        );
        System.out.println(weatherUrl);
        String weatherResponse = callApi(weatherUrl);
        JSONObject weatherJson = new JSONObject(weatherResponse);
        JSONObject currentWeather = weatherJson.getJSONObject("current_weather");
        
        double temperature = currentWeather.getDouble("temperature");
        double windSpeed = currentWeather.getDouble("windspeed");
        
        JSONObject hourly = weatherJson.getJSONObject("hourly");
        JSONArray humidityArray = hourly.getJSONArray("relative_humidity_2m");
        JSONArray feelsLikeArray = hourly.getJSONArray("apparent_temperature");
        
        int humidity = humidityArray.getInt(0);
        double feelsLike = feelsLikeArray.getDouble(0);
        
        String description = getWeatherDescription(temperature);
        
        return new WeatherData(cityName, temperature, feelsLike, humidity, description, windSpeed);
    }
    
    private String getWeatherDescription(double temperature) {
        if (temperature <= -10) return "Freddo polare";
        if (temperature <= 0) return "Gelido";
        if (temperature <= 5) return "Molto freddo";
        if (temperature <= 10) return "Freddo";
        if (temperature <= 15) return "Fresco";
        if (temperature <= 20) return "Leggermente fresco";
        if (temperature <= 25) return "Mite";
        if (temperature <= 30) return "Caldo";
        if (temperature <= 35) return "Molto caldo";
        return "Torrido";
    }
    
    private String callApi(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        
        conn.disconnect();
        return response.toString();
    }
    public String getWeatherIconUrl(double temperature) {
        if (temperature < 0) {
            return "https://cdn-icons-png.flaticon.com/512/3233/3233945.png";
        } else if (temperature < 10) {
            return "https://cdn-icons-png.flaticon.com/512/3233/3233943.png";
        } else if (temperature < 20) {
            return "https://cdn-icons-png.flaticon.com/512/3233/3233932.png";
        } else if (temperature < 30) {
            return "https://cdn-icons-png.flaticon.com/512/3233/3233936.png";
        } else {
            return "https://cdn-icons-png.flaticon.com/512/3233/3233937.png";
        }
    }
}