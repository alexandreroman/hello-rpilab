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

package dev.rpilab.hello.date;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "app.location=London")
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
@Testcontainers
class DateServiceTest {
    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("7"));

    @Autowired
    private DateService ds;
    @Value("${app.weather.api.key}")
    private String apiKey;

    @Test
    void testGetDate() {
        stubFor(get(urlEqualTo("/v1/timezone.json?q=London&key=" + apiKey))
                .willReturn(okJson("""
                        {
                            "location": {
                                "tz_id": "Europe/London"
                            }
                          }
                        """)));
        assertThat(ds.getLocalDate()).isNotNull();
    }
}
