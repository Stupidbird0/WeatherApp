package com.weather.spring.controller;

import com.weather.spring.model.WeatherData;
import com.weather.spring.service.WeatherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    @DisplayName("TC16 - GET /: caricamento pagina principale")
    void testHomePage_ReturnsWeatherTemplate() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attributeExists("success"))
                .andExpect(model().attribute("success", false));
        
        System.out.println("✅ Test TC16 passato: Home page caricata correttamente");
    }

    @Test
    @DisplayName("TC17 - POST con città valida: Roma restituisce dati meteo")
    void testPostValidCity_Rome_ReturnsWeatherData() throws Exception {
        // Given: preparo dati meteo simulati
        WeatherData mockWeather = new WeatherData();
        mockWeather.setCity("Roma");
        mockWeather.setTemperature(22.5);
        mockWeather.setFeelsLike(21.0);
        mockWeather.setHumidity(65);
        mockWeather.setWindSpeed(12.5);
        mockWeather.setWeatherDescription("Mite");
        
        when(weatherService.getWeatherByCity("Roma")).thenReturn(mockWeather);
        
        // When & Then
        mockMvc.perform(post("/weather")
                .param("city", "Roma"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attribute("success", true))
                .andExpect(model().attributeExists("weather"))
                .andExpect(model().attribute("error", nullValue()));
        
        verify(weatherService, times(1)).getWeatherByCity("Roma");
        
        System.out.println("✅ Test TC17 passato: Città valida gestita correttamente");
    }

    @Test
    @DisplayName("TC18 - POST con città inesistente: restituisce errore")
    void testPostInvalidCity_ReturnsError() throws Exception {
        // Given: simuliamo città non trovata
        when(weatherService.getWeatherByCity("CittàInesistente"))
                .thenThrow(new Exception("Città non trovata: CittàInesistente"));
        
        // When & Then
        mockMvc.perform(post("/weather")
                .param("city", "CittàInesistente"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Città non trovata: CittàInesistente"));
        
        System.out.println("✅ Test TC18 passato: Città inesistente gestita correttamente");
    }

    @Test
    @DisplayName("TC19 - POST con input vuoto: restituisce errore")
    void testPostEmptyCity_ReturnsError() throws Exception {
        // Given: parametro città vuoto
        when(weatherService.getWeatherByCity(""))
                .thenThrow(new Exception("Città non trovata"));
        
        // When & Then
        mockMvc.perform(post("/weather")
                .param("city", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attributeExists("error"));
        
        System.out.println("✅ Test TC19 passato: Input vuoto gestito correttamente");
    }

    @Test
    @DisplayName("TC20 - POST con timeout API: gestisce errore")
    void testPostApiTimeout_ReturnsError() throws Exception {
        // Given: simuliamo timeout
        when(weatherService.getWeatherByCity("Roma"))
                .thenThrow(new Exception("Timeout connessione API"));
        
        // When & Then
        mockMvc.perform(post("/weather")
                .param("city", "Roma"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attribute("success", false))
                .andExpect(model().attributeExists("error"));
        
        System.out.println("✅ Test TC20 passato: Timeout API gestito correttamente");
    }

    @Test
    @DisplayName("TC21 - POST con caratteri speciali: città accentata")
    void testPostAccentedCity_ReturnsWeatherData() throws Exception {
        // Given
        WeatherData mockWeather = new WeatherData();
        mockWeather.setCity("München");
        mockWeather.setTemperature(15.0);
        
        when(weatherService.getWeatherByCity("München")).thenReturn(mockWeather);
        
        // When & Then
        mockMvc.perform(post("/weather")
                .param("city", "München"))
                .andExpect(status().isOk())
                .andExpect(view().name("weather"))
                .andExpect(model().attribute("success", true));
        
        System.out.println("✅ Test TC21 passato: Città con accenti gestita correttamente");
    }
}