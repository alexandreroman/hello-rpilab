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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
class TimezoneService {
    private final Logger logger = LoggerFactory.getLogger(TimezoneService.class);
    private final TimezoneApi api;

    TimezoneService(TimezoneApi api) {
        this.api = api;
    }

    @Cacheable(key = "#location", cacheNames = "timezone")
    public ZoneId getZoneId(String location) {
        logger.debug("Fetching timezone for location: {}", location);
        final var tz = api.getTimezone(location);
        final var zid = ZoneId.of(tz.timezone());
        logger.debug("Got timezone for location {}: {}", location, zid);
        return zid;
    }
}
