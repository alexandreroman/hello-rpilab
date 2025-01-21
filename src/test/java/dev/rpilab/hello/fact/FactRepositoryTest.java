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

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest(properties = "app.fact.history-size=3")
@Import({FactConfig.class, FactRepository.class})
@Testcontainers
@ActiveProfiles("test")
class FactRepositoryTest {
    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("7"));

    @Autowired
    private FactRepository repo;

    @Test
    void testMaxHistorySize() {
        assertThat(repo.getAll()).isEmpty();

        repo.add("Fact 1");
        assertThat(repo.getAll()).containsExactly("Fact 1");

        repo.add("Fact 2");
        assertThat(repo.getAll()).containsExactly("Fact 1", "Fact 2");

        repo.add("Fact 3");
        assertThat(repo.getAll()).containsExactly("Fact 1", "Fact 2", "Fact 3");

        repo.add("Fact 4");
        assertThat(repo.getAll()).containsExactly("Fact 2", "Fact 3", "Fact 4");
    }
}
