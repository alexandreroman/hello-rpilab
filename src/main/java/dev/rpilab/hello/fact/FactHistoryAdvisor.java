/*
 * Copyright (c) 2026 Alexandre Roman
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

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

@Component
class FactHistoryAdvisor implements CallAdvisor {
    private final FactRepository repo;

    FactHistoryAdvisor(FactRepository repo) {
        this.repo = repo;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest req, CallAdvisorChain chain) {
        final var previousFacts = repo.getAll();
        if (previousFacts.isEmpty()) {
            // Fact history is empty: there is no need to augment the prompt.
            return chain.nextCall(req);
        }

        final var promptWithHistory = """
                    %s
                
                    Fact history is below, each item is surrounded by ---
                    ---
                    %s
                """.formatted(req.prompt().getUserMessage().getText(), String.join("\n---\n", previousFacts))
                .trim();

        // Augment the prompt with the fact history.
        final var newReq = req.mutate()
                .prompt(new Prompt(promptWithHistory))
                .build();
        return chain.nextCall(newReq);
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
