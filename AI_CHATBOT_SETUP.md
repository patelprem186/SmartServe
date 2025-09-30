# AI Chatbot Setup Instructions

## 🤖 Smart Serve AI Assistant Integration

The Smart Serve app now includes a free AI chatbot powered by OpenAI's GPT-3.5-turbo model to help customers with service-related questions and booking assistance.

## 🔧 Setup Instructions

### 1. Get Your Free OpenAI API Key

1. Visit [OpenAI Platform](https://platform.openai.com/api-keys)
2. Sign up for a free account (if you don't have one)
3. Navigate to "API Keys" section
4. Click "Create new secret key"
5. Copy the generated API key (starts with `sk-proj-`)

### 2. Configure the API Key

1. Open `app/src/main/java/com/easy/easybook/config/ApiConfig.java`
2. Replace `"sk-proj-your-api-key-here"` with your actual API key:

```java
public static final String OPENAI_API_KEY = "sk-proj-your-actual-api-key-here";
```

### 3. Features

✅ **Free AI Assistant**: Uses OpenAI's GPT-3.5-turbo model  
✅ **Service-Focused**: Trained to help with Smart Serve platform questions  
✅ **Real-time Chat**: Instant responses to customer queries  
✅ **User-Friendly**: Clean chat interface with message bubbles  
✅ **Always Available**: Accessible via floating action button in customer dashboard  

### 4. How It Works

- **Access**: Tap the chat icon (💬) floating action button in the customer dashboard
- **Usage**: Type any question about services, booking, or platform features
- **AI Responses**: Get helpful, contextual answers about Smart Serve services
- **Categories**: AI can help with Plumbing, Electrical, HVAC, Cleaning, and more

### 5. Cost Information

- **Free Tier**: OpenAI provides free credits for new accounts
- **Usage**: Each conversation uses minimal tokens (very cost-effective)
- **Monitoring**: Check your OpenAI dashboard for usage and billing

### 6. Troubleshooting

**If the chatbot doesn't respond:**
1. Verify your API key is correct
2. Check your internet connection
3. Ensure you have sufficient OpenAI credits
4. Check the console logs for error messages

**Common Issues:**
- Invalid API key → Update `ApiConfig.java` with correct key
- Network errors → Check internet connectivity
- Rate limiting → Wait a moment and try again

## 🎯 AI Assistant Capabilities

The AI chatbot can help customers with:

- **Service Discovery**: "What plumbing services do you offer?"
- **Booking Assistance**: "How do I book a service?"
- **Pricing Questions**: "What's the cost of electrical repairs?"
- **Scheduling Help**: "Can I book for tomorrow?"
- **General Support**: "How does Smart Serve work?"

## 🔒 Security Notes

- API key is stored in the app code (consider using environment variables for production)
- All conversations are sent to OpenAI (review their privacy policy)
- No personal data is stored in the chat history
- Each conversation is independent

## 📱 User Experience

1. **Easy Access**: Floating chat button always visible
2. **Quick Responses**: AI responds within seconds
3. **Contextual Help**: Understands Smart Serve platform
4. **Professional Interface**: Clean, modern chat design
5. **Mobile Optimized**: Perfect for mobile devices

---

**Ready to help your customers 24/7 with AI-powered assistance!** 🚀
