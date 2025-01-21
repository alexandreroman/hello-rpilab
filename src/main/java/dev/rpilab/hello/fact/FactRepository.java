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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
class FactRepository {
    private final Logger logger = LoggerFactory.getLogger(FactRepository.class);
    private final FactProperties props;
    private final StringRedisTemplate redis;

    FactRepository(FactProperties props, RedisConnectionFactory connFactory) {
        this.props = props;
        this.redis = new StringRedisTemplate();
        redis.setConnectionFactory(connFactory);
        redis.afterPropertiesSet();
    }

    void add(String fact) {
        logger.atDebug().log("Adding fact: {}", fact);
        redis.opsForList().rightPush("facts::entries", fact);

        logger.atDebug().log("Removing oldest entries: keep only {} entries", props.historySize());
        redis.opsForList().trim("facts::entries", -props.historySize(), -1);
    }

    List<String> getAll() {
        return redis.opsForList().range("facts::entries", 0, -1);
    }
}
