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

package dev.rpilab.hello.date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class DateService {
    private final Logger logger = LoggerFactory.getLogger(DateService.class);
    private final TimezoneApi api;
    private final String location;
    private ZoneId zoneId;

    DateService(TimezoneApi api, @Value("${app.location}") String location) {
        this.api = api;
        this.location = location;
    }

    public LocalDate getLocalDate() {
        if (zoneId == null) {
            logger.debug("Fetching timezone for location: {}", location);
            final var tz = api.getTimezone(location);
            zoneId = ZoneId.of(tz.timezone());
            logger.debug("Timezone for location {} is set to {}", location, zoneId);
        }
        return LocalDate.now(zoneId);
    }
}
