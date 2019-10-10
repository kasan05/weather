package com.city.weather.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.city.weather.modal.Forecast;

public interface ForecastRepository extends MongoRepository<Forecast, Integer> {

}
