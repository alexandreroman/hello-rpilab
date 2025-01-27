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

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.stereotype.Component;

@Component
class FactHistoryAdvisor implements CallAroundAdvisor {
    private final FactRepository repo;

    FactHistoryAdvisor(FactRepository repo) {
        this.repo = repo;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest req, CallAroundAdvisorChain chain) {
        final var previousFacts = repo.getAll();
        if (previousFacts.isEmpty()) {
            // Fact history is empty: there is no need to augment the prompt.
            return chain.nextAroundCall(req);
        }

        final var promptWithHistory = """
                    %s
                
                    Fact history is below, each item is surrounded by ---
                    ---
                    %s
                """.formatted(req.userText(), String.join("\n---\n", previousFacts));

        // Augment the prompt with the fact history.
        final var newReq = AdvisedRequest.from(req)
                .userText(promptWithHistory)
                .build();
        return chain.nextAroundCall(newReq);
    }

    @Override
    public String getName() {
        return "FactHistoryAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
