package dev.comma.agent.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class ChatServiceTest {

    private final ChatAgentProperties properties = new ChatAgentProperties();
    private final ChatService chatService = new ChatService(
            Clock.fixed(Instant.parse("2026-06-09T10:00:00Z"), ZoneOffset.UTC),
            properties,
            new ToolRegistry(List.of(new TimeTool())),
            new ConversationMemory());

    @Test
    void repliesWithPromptWhenProvided() {
        ChatResponse response = chatService.reply("hello agent");

        assertThat(response.message()).isEqualTo("Java Agent API 文档助手 received: hello agent");
        assertThat(response.generatedAt()).isNotNull();
        assertThat(response.sessionId()).isEqualTo("default");
        assertThat(response.turnCount()).isEqualTo(1);
        assertThat(response.previousPrompt()).isNull();
    }

    @Test
    void usesDefaultPromptWhenBlank() {
        ChatResponse response = chatService.reply(" ");

        assertThat(response.message()).contains("介绍一下 Java Agent API 文档助手");
    }

    @Test
    void supportsConfigurableRoleAndDefaultPrompt() {
        ChatAgentProperties customProperties = new ChatAgentProperties();
        customProperties.setRoleName("接口风险分析助手");
        customProperties.setDefaultPrompt("请生成接口风险清单");
        ChatService customService = new ChatService(
                Clock.fixed(Instant.parse("2026-06-09T10:00:00Z"), ZoneOffset.UTC),
                customProperties,
                new ToolRegistry(List.of(new TimeTool())),
                new ConversationMemory());

        ChatResponse response = customService.reply(null);

        assertThat(response.message()).isEqualTo("接口风险分析助手 received: 请生成接口风险清单");
    }

    @Test
    void callsTimeToolWhenPromptAsksForTime() {
        ChatResponse response = chatService.reply("现在几点");

        assertThat(response.message()).isEqualTo("Tool[time] current time: 2026-06-09T10:00:00Z");
    }

    @Test
    void keepsMinimalContextBySession() {
        ConversationMemory memory = new ConversationMemory();
        ChatService contextualService = new ChatService(
                Clock.fixed(Instant.parse("2026-06-09T10:00:00Z"), ZoneOffset.UTC),
                properties,
                new ToolRegistry(List.of(new TimeTool())),
                memory);

        ChatResponse first = contextualService.reply("先记住 Swagger 地址", "api-docs");
        ChatResponse second = contextualService.reply("继续分析接口", "api-docs");
        ChatResponse otherSession = contextualService.reply("新的会话", "risk-check");

        assertThat(first.turnCount()).isEqualTo(1);
        assertThat(first.previousPrompt()).isNull();
        assertThat(second.sessionId()).isEqualTo("api-docs");
        assertThat(second.turnCount()).isEqualTo(2);
        assertThat(second.previousPrompt()).isEqualTo("先记住 Swagger 地址");
        assertThat(otherSession.turnCount()).isEqualTo(1);
        assertThat(otherSession.previousPrompt()).isNull();
    }

    @Test
    void delegatesToolSelectionToRegistry() {
        ToolRegistry registry = new ToolRegistry(List.of(new TimeTool()));

        assertThat(registry.executeFirstSupported("what time is it", Instant.parse("2026-06-09T10:00:00Z").atOffset(ZoneOffset.UTC)))
                .contains("Tool[time] current time: 2026-06-09T10:00:00Z");
    }
}
