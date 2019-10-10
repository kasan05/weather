package com.city.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.city.weather.service.ForecastService;

@Controller
@RequestMapping("/forecast")
public class ForecastController {

	@Autowired
	private ForecastService forecastService;

	@GetMapping
	public String getForecastForCities(Model model) {
		forecastService.saveForecastData();
		model.addAttribute("forecasts", forecastService.getAllForecast());
		return "weather-results";
	}

	@GetMapping("/reload")
	public String reload(Model model) {
		forecastService.saveForecastData();
		model.addAttribute("forecasts", forecastService.getAllForecast());
		return "weather-results";
	}

	@GetMapping("/housekeep")
	public String houseKeepRecords(Model model) {
		forecastService.housekeepForecast();
		model.addAttribute("forecasts", forecastService.getAllForecast());
		return "weather-results";
	}
	
	@GetMapping("/test")
	public void test() {
		forecastService.deleteAllForecast();
	}

}
