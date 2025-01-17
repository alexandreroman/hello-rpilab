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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(FactNativeHints.class)
class FactConfig {
    @Bean
    FactApi factApi(
            @Value("${app.ninjas.api.url}") String apiUrl,
            @Value("${app.ninjas.api.key}") String apiKey,
            RestClient.Builder clientBuilder) {
        // Configure a REST client.
        final var client = clientBuilder.clone()
                .defaultHeader("X-Api-Key", apiKey)
                .baseUrl(apiUrl)
                .build();
        // Create a service instance using this REST client.
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build().createClient(FactApi.class);
    }
}
