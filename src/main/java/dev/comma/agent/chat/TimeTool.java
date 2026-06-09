package dev.comma.agent.chat;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
class TimeTool implements AgentTool {

    @Override
    public boolean supports(String prompt) {
        String lowerPrompt = prompt.toLowerCase(Locale.ROOT);
        return lowerPrompt.contains("time") || prompt.contains("时间") || prompt.contains("几点");
    }

    @Override
    public String execute(String prompt, OffsetDateTime generatedAt) {
        String currentTime = generatedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return "Tool[time] current time: " + currentTime;
    }
}