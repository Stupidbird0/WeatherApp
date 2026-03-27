package com.weather.spring;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectPackages({"com.weather.spring.service", "com.weather.spring.controller"})
@SuiteDisplayName("Suite Completa Test App Meteo")
public class TestRunner {
    // Questa classe esegue tutti i test
}