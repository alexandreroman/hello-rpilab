/*
 * Copyright (c) 2025 Broadcom, Inc. or its affiliates
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

package dev.rpilab.hello.fact;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Service
public class FactService {
    private final Logger logger = LoggerFactory.getLogger(FactService.class);
    private final FactApi api;
    private final ObservationRegistry observationRegistry;

    FactService(FactApi api, ObservationRegistry observationRegistry) {
        this.api = api;
        this.observationRegistry = observationRegistry;
    }

    @Cacheable(key = "'last'", cacheNames = "facts")
    public Fact getFact() {
        final FactApiResponse resp =
                Observation.createNotStarted("facts.api", observationRegistry)
                        .observe(this::callApi);
        return new Fact(resp.fact());
    }

    private FactApiResponse callApi() {
        logger.info("Fetching fact");
        final List<FactApiResponse> resp;
        try {
            resp = api.getFacts();
        } catch (RestClientException e) {
            throw new FactServiceException("Failed to get fact", e);
        }
        if (resp == null || resp.isEmpty()) {
            throw new FactServiceException("No fact available", null);
        }
        final var fact = resp.get(0);
        logger.debug("Received fact: {}", fact);
        return fact;
    }
}
