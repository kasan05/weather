package com.city.weather.service;

import java.util.List;

import com.city.weather.modal.Forecast;

public interface ForecastService {

	public void saveForecastData();

	public List<String> getAllTime();

	public List<Forecast> getAllForecast();
	
	public void deleteAllForecast();
	
	public void housekeepForecast();
}
