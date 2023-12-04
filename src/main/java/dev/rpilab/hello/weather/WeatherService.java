/*
 * Copyright (c) 2023 Broadcom, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.rpilab.hello.weather;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {
    private static final Map<Integer, WeatherType> WEATHER_CONDITION_CODES = new HashMap<>(64);

    static {
        // See spec here:
        // https://www.weatherapi.com/docs/weather_conditions.json
        WEATHER_CONDITION_CODES.put(1087, WeatherType.THUNDERSTORM);
        WEATHER_CONDITION_CODES.put(1063, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1066, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1069, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1072, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1150, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1153, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1168, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1171, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1180, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1183, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1198, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1204, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1207, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1249, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1252, WeatherType.LIGHT_RAIN);
        WEATHER_CONDITION_CODES.put(1186, WeatherType.RAIN);
        WEATHER_CONDITION_CODES.put(1189, WeatherType.RAIN);
        WEATHER_CONDITION_CODES.put(1192, WeatherType.RAIN);
        WEATHER_CONDITION_CODES.put(1195, WeatherType.RAIN);
        WEATHER_CONDITION_CODES.put(1201, WeatherType.RAIN);
        WEATHER_CONDITION_CODES.put(1240, WeatherType.RAIN);
        WEATHER_CONDITION_CODES.put(1246, WeatherType.RAIN);
        WEATHER_CONDITION_CODES.put(1273, WeatherType.RAIN);
        WEATHER_CONDITION_CODES.put(1276, WeatherType.RAIN);
        WEATHER_CONDITION_CODES.put(1213, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1216, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1219, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1222, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1225, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1237, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1255, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1258, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1261, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1264, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1279, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1282, WeatherType.SNOW);
        WEATHER_CONDITION_CODES.put(1117, WeatherType.FOG);
        WEATHER_CONDITION_CODES.put(1135, WeatherType.FOG);
        WEATHER_CONDITION_CODES.put(1147, WeatherType.FOG);
        WEATHER_CONDITION_CODES.put(1000, WeatherType.CLEAR);
        WEATHER_CONDITION_CODES.put(1003, WeatherType.CLOUDS);
        WEATHER_CONDITION_CODES.put(1006, WeatherType.CLOUDS);
        WEATHER_CONDITION_CODES.put(1009, WeatherType.CLOUDS);
        WEATHER_CONDITION_CODES.put(1030, WeatherType.CLOUDS);
    }

    private final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final WeatherApi api;
    private final String location;
    private final ObservationRegistry observationRegistry;

    WeatherService(WeatherApi api, @Value("${app.location}") String location, ObservationRegistry observationRegistry) {
        this.api = api;
        this.location = location;
        this.observationRegistry = observationRegistry;
    }

    private static WeatherType toWeatherType(int cid) {
        return WEATHER_CONDITION_CODES.getOrDefault(cid, WeatherType.UNKNOWN);
    }

    @Cacheable(key = "'current'", cacheNames = "weather")
    public Weather getCurrent() {
        final WeatherApiResponse resp =
                Observation.createNotStarted("weather.api", observationRegistry)
                        .lowCardinalityKeyValue("location", location)
                        .observe(this::callApi);

        final ZoneId zoneId;
        try {
            zoneId = ZoneId.of(resp.location().tz_id());
        } catch (DateTimeException e) {
            throw new WeatherServiceException("Failed to parse ZoneId: " + resp.location().tz_id(), e);
        }
        final ZonedDateTime dateTime = ZonedDateTime.of(resp.current().last_updated(), zoneId);
        final WeatherType weatherType = toWeatherType(resp.current().condition().code());
        return new Weather(location, dateTime, weatherType, resp.current().temp_c());
    }

    private WeatherApiResponse callApi() {
        logger.info("Fetching current weather");
        try {
            final var resp = api.getCurrent(location);
            logger.debug("Received current weather: {}", resp);
            return resp;
        } catch (RestClientException e) {
            throw new WeatherServiceException("Failed to get current weather", e);
        }
    }
}
