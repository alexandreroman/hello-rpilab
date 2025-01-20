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
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!inmemory")
class AIFactService implements FactService {
    private final Logger logger = LoggerFactory.getLogger(AIFactService.class);
    private final ChatClient chatClient;
    private final ObservationRegistry observationRegistry;

    AIFactService(ChatClient.Builder chatClientBuilder, ObservationRegistry observationRegistry) {
        this.chatClient = chatClientBuilder.build();
        this.observationRegistry = observationRegistry;
    }

    @Cacheable(key = "'last'", cacheNames = "facts")
    public Fact getFact() {
        return Observation.createNotStarted("facts.generate", observationRegistry)
                .observe(this::generateFact);
    }

    private Fact generateFact() {
        logger.info("Generating fact leveraging AI");
        return chatClient.prompt()
                .system("""
                        You're an helpful assistant.
                        You answer to user requests without using offending or aggressive language.
                        """)
                .user("""
                        Generate a fun fact.
                        Just include the fact in your answer.
                        Use up to 3 sentences for your answer.
                        """)
                .call()
                .entity(Fact.class);
    }
}
