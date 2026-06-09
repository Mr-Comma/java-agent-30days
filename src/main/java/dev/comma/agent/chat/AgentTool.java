package dev.comma.agent.chat;

import java.time.OffsetDateTime;

interface AgentTool {

    boolean supports(String prompt);

    String execute(String prompt, OffsetDateTime generatedAt);
}