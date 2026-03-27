package com.weather.spring.service;

import com.weather.spring.model.WeatherData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @InjectMocks
    private OpenMeteoService weatherService;

    @Test
    @DisplayName("TC01 - Città valida: Roma restituisce dati meteo corretti")
    void testValidCity_Rome_ReturnsWeatherData() throws Exception {
        // Given: una città valida
        String city = "Roma";
        
        // When: richiedo i dati meteo
        WeatherData result = weatherService.getWeatherByCity(city);
        
        // Then: verifico che i dati siano corretti
        assertNotNull(result, "Il risultato non dovrebbe essere nullo");
        assertEquals("Roma", result.getCity(), "Il nome della città dovrebbe essere Roma");
        assertTrue(result.getTemperature() > -20 && result.getTemperature() < 50, 
                   "La temperatura dovrebbe essere in un range realistico");
        assertTrue(result.getHumidity() >= 0 && result.getHumidity() <= 100, 
                   "L'umidità dovrebbe essere tra 0 e 100");
        assertTrue(result.getWindSpeed() >= 0, "La velocità del vento dovrebbe essere positiva");
        assertNotNull(result.getWeatherDescription(), "La descrizione meteo non dovrebbe essere nulla");
        assertFalse(result.getWeatherDescription().isEmpty(), "La descrizione meteo non dovrebbe essere vuota");
        
        System.out.println("✅ Test TC01 passato: Roma - Temperatura: " + result.getTemperature() + "°C");
    }

    @Test
    @DisplayName("TC02 - Città valida: Milano restituisce dati meteo corretti")
    void testValidCity_Milan_ReturnsWeatherData() throws Exception {
        // Given: un'altra città valida
        String city = "Milano";
        
        // When
        WeatherData result = weatherService.getWeatherByCity(city);
        
        // Then
        assertNotNull(result);
        assertEquals("Milano", result.getCity());
        assertNotNull(result.getTemperature());
        
        System.out.println("✅ Test TC02 passato: Milano - Temperatura: " + result.getTemperature() + "°C");
    }

    @Test
    @DisplayName("TC03 - Città valida: Napoli restituisce dati meteo corretti")
    void testValidCity_Naples_ReturnsWeatherData() throws Exception {
        // Given
        String city = "Napoli";
        
        // When
        WeatherData result = weatherService.getWeatherByCity(city);
        
        // Then
        assertNotNull(result);
        assertEquals("Napoli", result.getCity());
        assertNotNull(result.getTemperature());
        
        System.out.println("✅ Test TC03 passato: Napoli - Temperatura: " + result.getTemperature() + "°C");
    }

    @Test
    @DisplayName("TC04 - Città valida con spazi: New York restituisce dati meteo")
    void testValidCity_WithSpaces_ReturnsWeatherData() throws Exception {
        // Given: città con spazio
        String city = "New York";
        
        // When
        WeatherData result = weatherService.getWeatherByCity(city);
        
        // Then
        assertNotNull(result);
        assertEquals("New York", result.getCity());
        
        System.out.println("✅ Test TC04 passato: New York - Temperatura: " + result.getTemperature() + "°C");
    }

    @Test
    @DisplayName("TC05 - Città inesistente: 'CittàCheNonEsiste12345' restituisce errore")
    void testInvalidCity_NonExistent_ThrowsException() {
        // Given: una città che non esiste
        String city = "CittàCheNonEsiste12345";
        
        // When & Then: deve lanciare un'eccezione
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity(city);
        });
        
        // Verifico che il messaggio di errore sia appropriato
        String errorMessage = exception.getMessage();
        assertTrue(errorMessage.contains("Città non trovata") || 
                   errorMessage.contains("non trovata") ||
                   errorMessage.contains("not found"),
                   "Il messaggio di errore dovrebbe indicare città non trovata. Messaggio: " + errorMessage);
        
        System.out.println("✅ Test TC05 passato: Messaggio errore - " + errorMessage);
    }

    @Test
    @DisplayName("TC06 - Città con caratteri casuali: 'asdfghjkl' restituisce errore")
    void testInvalidCity_RandomString_ThrowsException() {
        // Given: stringa casuale
        String city = "asdfghjkl";
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity(city);
        });
        
        assertTrue(exception.getMessage().contains("Città non trovata") ||
                   exception.getMessage().contains("non trovata"));
        
        System.out.println("✅ Test TC06 passato: Stringa casuale - " + exception.getMessage());
    }

    @Test
    @DisplayName("TC07 - Città con numeri: '12345' restituisce errore")
    void testInvalidCity_Numbers_ThrowsException() {
        // Given: solo numeri
        String city = "12345";
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity(city);
        });
        
        assertTrue(exception.getMessage().contains("Città non trovata"));
        
        System.out.println("✅ Test TC07 passato: Solo numeri - " + exception.getMessage());
    }

    @Test
    @DisplayName("TC08 - Input vuoto: stringa vuota restituisce errore")
    void testEmptyInput_EmptyString_ThrowsException() {
        // Given: stringa vuota
        String city = "";
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity(city);
        });
        
        assertTrue(exception.getMessage().contains("Città non trovata") ||
                   exception.getMessage().contains("vuoto") ||
                   exception.getMessage().contains("empty"),
                   "Dovrebbe restituire errore per input vuoto");
        
        System.out.println("✅ Test TC08 passato: Input vuoto - " + exception.getMessage());
    }

    @Test
    @DisplayName("TC09 - Input null: città null restituisce errore")
    void testNullInput_NullCity_ThrowsException() {
        // Given: null
        String city = null;
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity(city);
        });
        
        assertNotNull(exception, "Dovrebbe lanciare un'eccezione per input null");
        
        System.out.println("✅ Test TC09 passato: Input null - " + exception.getMessage());
    }

    @Test
    @DisplayName("TC10 - Input solo spazi: '   ' restituisce errore")
    void testOnlySpaces_Whitespace_ThrowsException() {
        // Given: solo spazi
        String city = "   ";
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            weatherService.getWeatherByCity(city);
        });
        
        assertTrue(exception.getMessage().contains("Città non trovata") ||
                   exception.getMessage().contains("non valida"));
        
        System.out.println("✅ Test TC10 passato: Solo spazi - " + exception.getMessage());
    }
}