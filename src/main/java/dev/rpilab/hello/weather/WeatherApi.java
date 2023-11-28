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

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.time.LocalDateTime;

interface WeatherApi {
    @GetExchange(url = "/v1/current.json?q={location}&aqi=no&key={key}")
    WeatherApiResponse getCurrent(@PathVariable("location") String location);
}

record WeatherApiResponse(
        WeatherApiCurrent current,
        WeatherApiLocation location
) {
}

record WeatherApiCurrent(
        WeatherApiCondition condition,
        float temp_c,
        @JsonFormat(pattern = "yyyy-MM-dd H:mm")
        LocalDateTime last_updated
) {
}

record WeatherApiCondition(
        int code
) {
}

record WeatherApiLocation(
        String tz_id
) {
}
