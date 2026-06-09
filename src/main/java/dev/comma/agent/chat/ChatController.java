package dev.comma.agent.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public ChatResponse chat(
            @RequestParam(defaultValue = "") String prompt,
            @RequestParam(defaultValue = "default") String sessionId) {
        return chatService.reply(prompt, sessionId);
    }
}
