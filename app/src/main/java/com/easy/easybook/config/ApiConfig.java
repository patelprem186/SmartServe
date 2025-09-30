package com.easy.easybook.config;

public class ApiConfig {
    
    // OpenAI API Configuration
    // Replace with your actual OpenAI API key
    // You can get a free API key from: https://platform.openai.com/api-keys
    public static final String OPENAI_API_KEY = "YOUR_OPENAI_API_KEY_HERE";
    public static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    // Model configuration
    public static final String MODEL_NAME = "gpt-3.5-turbo";
    public static final int MAX_TOKENS = 500;
    public static final double TEMPERATURE = 0.7;
}