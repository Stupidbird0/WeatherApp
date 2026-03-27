package com.weather.spring.model;

public class WeatherData {
    private String city;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private String weatherDescription;
    private double windSpeed;
    
    public WeatherData() {}
    
    public WeatherData(String city, double temperature, double feelsLike, 
                       int humidity, String weatherDescription, double windSpeed) {
        this.city = city;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.weatherDescription = weatherDescription;
        this.windSpeed = windSpeed;
    }
    
    // Getter e Setter
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }
    
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    
    public String getWeatherDescription() { return weatherDescription; }
    public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }
    
    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
}