package com.weather.spring.service;

import com.weather.spring.utils.ApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceErrorTest {

    @Mock
    private ApiClient apiClient;
    
    @InjectMocks
    private OpenMeteoService weatherService;

    @Test
    @DisplayName("TC11 - Timeout API: simulazione timeout di connessione")
    void testApiTimeout_ThrowsTimeoutException() throws Exception {
        // Given: simuliamo un timeout
        when(apiClient.get(anyString())).thenThrow(new SocketTimeoutException("Connection timeout"));
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            // Usiamo reflection per iniettare il mock, oppure testiamo direttamente
            weatherService.getWeatherByCity("Roma");
        });
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("timeout") || 
                   exception.getCause() instanceof SocketTimeoutException,
                   "Dovrebbe gestire il timeout");
        
        System.out.println("✅ Test TC11 passato: Timeout gestito correttamente");
    }

    @Test
    @DisplayName("TC12 - Errore connessione: API non raggiungibile")
    void testApiConnectionError_ThrowsConnectException() throws Exception {
        // Given: simuliamo errore di connessione
        when(apiClient.get(anyString())).thenThrow(new ConnectException("Connection refused"));
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity("Roma");
        });
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("connessione") || 
                   exception.getCause() instanceof ConnectException,
                   "Dovrebbe gestire l'errore di connessione");
        
        System.out.println("✅ Test TC12 passato: Errore connessione gestito");
    }

    @Test
    @DisplayName("TC13 - Errore generico API: Internal Server Error")
    void testApiServerError_ThrowsException() throws Exception {
        // Given: simuliamo errore server
        when(apiClient.get(anyString())).thenThrow(new RuntimeException("API Server Error: 500"));
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity("Roma");
        });
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("errore") || 
                   exception.getMessage().contains("500"),
                   "Dovrebbe gestire l'errore del server");
        
        System.out.println("✅ Test TC13 passato: Errore server gestito");
    }

    @Test
    @DisplayName("TC14 - API geocodifica restituisce formato JSON non valido")
    void testInvalidJsonResponse_ThrowsException() throws Exception {
        // Given: JSON non valido
        when(apiClient.get(anyString())).thenReturn("{ invalid json }");
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity("Roma");
        });
        
        assertNotNull(exception);
        
        System.out.println("✅ Test TC14 passato: JSON non valido gestito");
    }

    @Test
    @DisplayName("TC15 - API meteo restituisce risposta vuota")
    void testEmptyResponse_ThrowsException() throws Exception {
        // Given: risposta vuota
        when(apiClient.get(anyString())).thenReturn("{}");
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity("Roma");
        });
        
        assertNotNull(exception);
        
        System.out.println("✅ Test TC15 passato: Risposta vuota gestita");
    }
}