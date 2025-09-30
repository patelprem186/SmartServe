package com.easy.easybook.ui.customer;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easy.easybook.R;
import com.easy.easybook.config.ApiConfig;
import com.easy.easybook.databinding.ActivityChatbotBinding;
import com.easy.easybook.ui.customer.adapters.ChatAdapter;
import com.easy.easybook.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONArray;

public class ChatBotActivity extends AppCompatActivity {
    
    private ActivityChatbotBinding binding;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private ExecutorService executor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatbotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        executor = Executors.newSingleThreadExecutor();
        setupUI();
        setupRecyclerView();
        setupClickListeners();
        
        // Add welcome message
        addWelcomeMessage();
    }
    
    private void setupUI() {
        binding.toolbar.setTitle("AI Assistant");
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        binding.rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChatMessages.setAdapter(chatAdapter);
    }
    
    private void setupClickListeners() {
        binding.btnSend.setOnClickListener(v -> sendMessage());
        
        binding.etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }
    
    private void addWelcomeMessage() {
        // Test API on startup
        testAPI();
        
        // Get AI-generated welcome message
        executor.execute(() -> {
            try {
                String welcomeResponse = callOpenAI("Hello! Please introduce yourself as an AI assistant for Smart Serve, a service booking platform. Keep it brief and friendly.");
                runOnUiThread(() -> {
                    if (welcomeResponse != null && !welcomeResponse.trim().isEmpty()) {
                        ChatMessage welcomeMessage = new ChatMessage(welcomeResponse, false, System.currentTimeMillis());
                        chatMessages.add(welcomeMessage);
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                        scrollToBottom();
                    } else {
                        // Minimal fallback if AI fails
                        ChatMessage welcomeMessage = new ChatMessage("Hello! I'm your AI assistant for Smart Serve. How can I help you today?", false, System.currentTimeMillis());
                        chatMessages.add(welcomeMessage);
                        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                        scrollToBottom();
                    }
                });
            } catch (Exception e) {
                Log.e("OpenAI", "Failed to get AI welcome message: " + e.getMessage());
                runOnUiThread(() -> {
                    ChatMessage welcomeMessage = new ChatMessage("Hello! I'm your AI assistant for Smart Serve. How can I help you today?", false, System.currentTimeMillis());
                    chatMessages.add(welcomeMessage);
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    scrollToBottom();
                });
            }
        });
    }
    
    private void testAPI() {
        Log.d("OpenAI", "=== TESTING API CONFIGURATION ===");
        Log.d("OpenAI", "API Key length: " + ApiConfig.OPENAI_API_KEY.length());
        Log.d("OpenAI", "API Key (first 10): " + ApiConfig.OPENAI_API_KEY.substring(0, Math.min(10, ApiConfig.OPENAI_API_KEY.length())));
        Log.d("OpenAI", "API URL: " + ApiConfig.OPENAI_API_URL);
        Log.d("OpenAI", "Model: " + ApiConfig.MODEL_NAME);
        Log.d("OpenAI", "Max tokens: " + ApiConfig.MAX_TOKENS);
        Log.d("OpenAI", "Temperature: " + ApiConfig.TEMPERATURE);
        
        // Test a simple API call
        executor.execute(() -> {
            try {
                Log.d("OpenAI", "=== STARTING API TEST ===");
                String testResponse = callOpenAI("Say hello");
                Log.d("OpenAI", "=== API TEST RESULT ===");
                Log.d("OpenAI", "Test response: " + (testResponse != null ? testResponse : "NULL"));
                
                if (testResponse == null || testResponse.trim().isEmpty()) {
                    Log.e("OpenAI", "API TEST FAILED - No response received");
                    Log.e("OpenAI", "This means the API is not working. Check:");
                    Log.e("OpenAI", "1. API key is valid");
                    Log.e("OpenAI", "2. Internet connection");
                    Log.e("OpenAI", "3. OpenAI service is up");
                } else {
                    Log.d("OpenAI", "API TEST SUCCESS - Response received: " + testResponse);
                }
            } catch (Exception e) {
                Log.e("OpenAI", "API test failed: " + e.getMessage(), e);
                Log.e("OpenAI", "Exception details: " + e.getClass().getSimpleName());
            }
        });
    }
    
    private void sendMessage() {
        String message = binding.etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        
        // Add user message to chat
        ChatMessage userMessage = new ChatMessage(message, true, System.currentTimeMillis());
        chatMessages.add(userMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        scrollToBottom();
        
        // Clear input
        binding.etMessage.setText("");
        
        // Show typing indicator
        showTypingIndicator();
        
        // Send to AI
        sendToAI(message);
    }
    
    private void showTypingIndicator() {
        ChatMessage typingMessage = new ChatMessage("AI is typing...", false, System.currentTimeMillis());
        chatMessages.add(typingMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        scrollToBottom();
    }
    
    private void removeTypingIndicator() {
        if (!chatMessages.isEmpty() && chatMessages.get(chatMessages.size() - 1).getMessage().equals("AI is typing...")) {
            chatMessages.remove(chatMessages.size() - 1);
            chatAdapter.notifyItemRemoved(chatMessages.size());
        }
    }
    
    private void sendToAI(String userMessage) {
        executor.execute(() -> {
            try {
                // Log the API key (first few characters only for security)
                Log.d("OpenAI", "Using API key: " + ApiConfig.OPENAI_API_KEY.substring(0, Math.min(10, ApiConfig.OPENAI_API_KEY.length())) + "...");
                
                String aiResponse = getAIResponse(userMessage);
                
                runOnUiThread(() -> {
                    removeTypingIndicator();
                    
                    String finalResponse = aiResponse;
                    
                    ChatMessage aiMessage = new ChatMessage(finalResponse, false, System.currentTimeMillis());
                    chatMessages.add(aiMessage);
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    scrollToBottom();
                });
                } catch (Exception e) {
                Log.e("OpenAI", "Exception in sendToAI: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    removeTypingIndicator();
                    
                    // Show error message if API fails
                    ChatMessage errorMessage = new ChatMessage(
                        "I'm sorry, I'm having trouble connecting to my AI service right now. Please try again in a moment.",
                        false,
                        System.currentTimeMillis()
                    );
                    chatMessages.add(errorMessage);
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    scrollToBottom();
                });
            }
        });
    }
    
    private String getAIResponse(String userMessage) {
        Log.d("OpenAI", "=== GETTING AI RESPONSE ===");
        Log.d("OpenAI", "User message: " + userMessage);
        
        // Only use OpenAI API - no hardcoded responses
        Log.d("OpenAI", "Calling OpenAI API...");
        String apiResponse = callOpenAI(userMessage);
        Log.d("OpenAI", "API response: " + (apiResponse != null ? apiResponse.substring(0, Math.min(50, apiResponse.length())) + "..." : "null"));
        
        if (apiResponse != null && !apiResponse.trim().isEmpty()) {
            Log.d("OpenAI", "Using raw API response");
            return apiResponse;
        }
        
        // Only fallback if API completely fails
        Log.d("OpenAI", "API failed, using minimal fallback");
        return "I'm sorry, I'm having trouble connecting to my AI service right now. Please try again in a moment.";
    }
    
    
    private String callOpenAI(String userMessage) {
        Log.d("OpenAI", "=== STARTING API CALL ===");
        Log.d("OpenAI", "User message: " + userMessage);
        Log.d("OpenAI", "API Key (first 10 chars): " + ApiConfig.OPENAI_API_KEY.substring(0, Math.min(10, ApiConfig.OPENAI_API_KEY.length())));
        Log.d("OpenAI", "API URL: " + ApiConfig.OPENAI_API_URL);
        
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
            
            // Prepare the request
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", ApiConfig.MODEL_NAME);
            requestBody.put("max_tokens", ApiConfig.MAX_TOKENS);
            requestBody.put("temperature", ApiConfig.TEMPERATURE);
            
            JSONArray messages = new JSONArray();
            
            // System message to make the AI act as a service assistant
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful assistant for Smart Serve, a service booking platform. " +
                "Help users with service-related questions, booking assistance, and general inquiries about the platform. " +
                "Keep responses concise and helpful. If asked about specific services, mention that users can browse " +
                "services in the app or search for specific categories like Plumbing, Electrical, HVAC, Cleaning, etc.");
            messages.put(systemMessage);
            
            // User message
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.put(userMsg);
            
            requestBody.put("messages", messages);
            
            RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.get("application/json; charset=utf-8")
            );
            
            Request request = new Request.Builder()
                .url(ApiConfig.OPENAI_API_URL)
                .addHeader("Authorization", "Bearer " + ApiConfig.OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
            
            Log.d("OpenAI", "Making API request...");
            Response response = client.newCall(request).execute();
            Log.d("OpenAI", "Response received. Code: " + response.code());
            
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("OpenAI", "=== SUCCESSFUL RESPONSE ===");
                Log.d("OpenAI", "Full response body: " + responseBody);
                
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    String content = message.getString("content");
                    
                    Log.d("OpenAI", "Extracted content: " + content);
                    
                    // Return raw AI response without any filtering
                    if (content != null) {
                        Log.d("OpenAI", "=== RETURNING RAW API RESPONSE ===");
                        Log.d("OpenAI", "Raw content: " + content);
                        return content.trim();
                    } else {
                        Log.w("OpenAI", "Empty content in response");
                        return null;
                    }
                } catch (Exception e) {
                    Log.e("OpenAI", "Error parsing JSON response: " + e.getMessage());
                    return null;
                }
            } else {
                Log.e("OpenAI", "=== API CALL FAILED ===");
                Log.e("OpenAI", "Response code: " + response.code());
                Log.e("OpenAI", "Response message: " + response.message());
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                Log.e("OpenAI", "Error body: " + errorBody);
                return null; // Let the calling method handle fallback
            }
        } catch (Exception e) {
            Log.e("OpenAI", "Exception in callOpenAI: " + e.getMessage(), e);
            e.printStackTrace();
            return null; // Let the calling method handle fallback
        }
    }
    
    
    private void scrollToBottom() {
        binding.rvChatMessages.post(() -> {
            if (chatAdapter.getItemCount() > 0) {
                binding.rvChatMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}
