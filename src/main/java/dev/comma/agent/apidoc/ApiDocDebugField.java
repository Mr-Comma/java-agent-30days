package dev.comma.agent.apidoc;

public record ApiDocDebugField(
        String name,
        String jsonType,
        boolean required,
        String readyMeaning,
        String needsInputMeaning,
        String usage,
        int displayOrder,
        String source,
        Object readyExampleValue,
        Object needsInputExampleValue) {
}
