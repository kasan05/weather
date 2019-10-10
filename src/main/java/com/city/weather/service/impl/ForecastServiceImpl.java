package com.city.weather.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.city.weather.dto.ForecastResponse;
import com.city.weather.modal.Forecast;
import com.city.weather.repository.ForecastRepository;
import com.city.weather.service.ForecastService;

@Service
public class ForecastServiceImpl implements ForecastService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ForecastRepository forecastRepository;

	@Override
	public void saveForecastData() {
		
		if(doesTodayRecordExists()) {
			return;
		}
		Map<String,String> urlMap = getUrlMap();
		for(String name : urlMap.keySet()) {
			
			ForecastResponse forecastResponse = restTemplate.getForObject(urlMap.get(name), ForecastResponse.class);
			
			Forecast forecast = new Forecast();
			forecast.setTimezone(forecastResponse.getTimezone());
			forecast.setIcon(forecastResponse.getCurrently().getIcon());
			forecast.setHumidity(forecastResponse.getCurrently().getHumidity());
			forecast.setTemperature(forecastResponse.getCurrently().getTemperature());
			forecast.setSummary(forecastResponse.getCurrently().getSummary());
			forecast.setTime(forecastResponse.getCurrently().getTime());
			forecast.setId(name+forecastResponse.getCurrently().getTime());
			forecast.setCityName(name);
			
			forecast = forecastRepository.save(forecast);
		}
	
		
	}

	
	private Map<String,String> getUrlMap() {
		Map<String,String> map = new HashMap<String,String>();
		
		map.put("Jakarta, Indonesia","https://api.darksky.net/forecast/72daffbd308258be3c7560f3057f9a5f/6.2088,106.8456?exclude=daily,flags,alerts,minutely,hourly");//Jakarta, Indonesia
		map.put("Nara, Japan","https://api.darksky.net/forecast/72daffbd308258be3c7560f3057f9a5f/34.6851,135.8048?exclude=daily,flags,alerts,minutely,hourly");//Nara, Japan
		map.put("Niseko, Japan","https://api.darksky.net/forecast/72daffbd308258be3c7560f3057f9a5f/42.8048,140.6874?exclude=daily,flags,alerts,minutely,hourly");//Niseko, Japan
		map.put("Austin, TX","https://api.darksky.net/forecast/72daffbd308258be3c7560f3057f9a5f/30.2672,-97.7431?exclude=daily,flags,alerts,minutely,hourly");//Austin, TX,
		map.put("Omaha, NE","https://api.darksky.net/forecast/72daffbd308258be3c7560f3057f9a5f/41.2565,-95.9345?exclude=daily,flags,alerts,minutely,hourly");//Omaha, NE,
		map.put("Campbell, CA","https://api.darksky.net/forecast/72daffbd308258be3c7560f3057f9a5f/37.2872,-121.9500?exclude=daily,flags,alerts,minutely,hourly");//Campbell, CA,
		
		
		return map;
	}


	@Override
	public List<String> getAllTime() {
		List<Forecast> forecasts  = forecastRepository.findAll();
		List<String> list = new ArrayList<String>(); 
		if(forecasts!=null && !forecasts.isEmpty()) {
			 list= 	forecasts .stream()
					.map(forecast->forecast.getTime())
					.collect(Collectors.toList());
		}
		
		
		return list;
	}

	private boolean doesTodayRecordExists() {
		Calendar now = Calendar.getInstance();
		
		 List<String> timeList = getAllTime();
		for(String time : timeList) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(Long.parseLong(time)*1000L);
			
			if(calendar.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)
					&& calendar.get(Calendar.MONTH) == now.get(Calendar.MONTH) )
				return true;
			
		}
		
		return false;
	}

	private boolean doesDataOlderThanThreeDays(String time) {
		Calendar now = Calendar.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.parseLong(time)*1000L);
		int diff = calendar.get(Calendar.DAY_OF_MONTH) - now.get(Calendar.DAY_OF_MONTH);
		if(Math.abs(diff)>=3) {
			return true;
		}
			return false;	
	}

	@Override
	public List<Forecast> getAllForecast() {
		List<Forecast> forecasts = forecastRepository.findAll();
		return forecasts==null?new ArrayList<Forecast>():forecasts;
	}


	@Override
	public void deleteAllForecast() {
		forecastRepository.deleteAll();
		
	}


	@Override
	public void housekeepForecast() {
		List<Forecast> forecasts = forecastRepository.findAll();
		
		List<Forecast> forecastsToDelete =forecasts.stream()
				.filter(forecast->doesDataOlderThanThreeDays(forecast.getTime()))
				.collect(Collectors.toList());
		forecastRepository.deleteAll(forecastsToDelete);
	}
}
