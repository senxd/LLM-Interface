package net.prismclient.model

enum class ModelVendor {
    /**
     * GPT Series (gpt-4o and gpt-4o-mini)
     * o1 Series (o1-preview and o1-mini)
     */
    OpenAI,

    /**
     * Claude (e.g., Sonnet 3.5 or Opus)
     */
    Anthropic,

    Google,

    /**
     * LLama
     */
    Meta,

    /**
     * Mistral
     */
    Mistral,

    /**
     * Provider that use the OpenAI protocol (e.g., LM Studio).
     */
    Custom_OpenAI,

    /**
     *
     */
    Other
}