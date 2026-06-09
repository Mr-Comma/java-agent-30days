package dev.comma.agent.chat;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "agent.chat")
public class ChatAgentProperties {

    private String roleName = "Java Agent API 文档助手";
    private String defaultPrompt = "介绍一下 Java Agent API 文档助手";

    public String roleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String defaultPrompt() {
        return defaultPrompt;
    }

    public void setDefaultPrompt(String defaultPrompt) {
        this.defaultPrompt = defaultPrompt;
    }
}
