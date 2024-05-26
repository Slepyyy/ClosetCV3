package com.example.closetcv3

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.DetectIntentRequest
import com.google.cloud.dialogflow.v2.DetectIntentResponse
import com.google.cloud.dialogflow.v2.QueryInput
import com.google.cloud.dialogflow.v2.SessionName
import com.google.cloud.dialogflow.v2.SessionsClient
import com.google.cloud.dialogflow.v2.SessionsSettings
import com.google.cloud.dialogflow.v2.TextInput
import com.example.closetcv3.adapters.ChatAdapter
import com.example.closetcv3.databinding.ActivityChatBotBinding
import com.example.closetcv3.models.Message
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatBot : AppCompatActivity() {
    private var messageList: ArrayList<Message> = ArrayList()
    private var userResponses: ArrayList<String> = ArrayList()

    // DialogFlow
    private var sessionsClient: SessionsClient? = null
    private var sessionName: SessionName? = null
    private val uuid = UUID.randomUUID().toString()
    private val TAG = "chat-bot"
    private lateinit var chatAdapter: ChatAdapter

    // View Binding
    private lateinit var binding: ActivityChatBotBinding

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting adapter to RecyclerView
        chatAdapter = ChatAdapter(this, messageList)
        binding.chatView.adapter = chatAdapter

        // OnClickListener to update the list and call Dialogflow
        binding.btnSend.setOnClickListener {
            val message: String = binding.editMessage.text.toString()
            if (message.isNotEmpty()) {
                addMessageToChat(message, true)
                binding.editMessage.setText("")
                sendMessageToBot(message)
            } else {
                Toast.makeText(this, "Please enter text!", Toast.LENGTH_SHORT).show()
            }
        }

        // Bottom Navigation
        val navView: BottomNavigationView = binding.navView
        NavigationUtil.setupBottomNavigationView(this, navView)

        setupBot()
    }

    // Method to add message to chat
    private fun addMessageToChat(message: String, isUser: Boolean) {
        messageList.add(Message(message, isUser))
        chatAdapter.notifyDataSetChanged()
        binding.chatView.layoutManager?.scrollToPosition(messageList.size - 1)

        if (isUser) {
            // Add user response to list and log
            userResponses.add(message)
            saveUserResponses()
            Log.d(TAG, "User Response Added: $message")
            Log.d(TAG, "Current User Responses: $userResponses")
        }
    }

    // Method to send message to bot
    private fun sendMessageToBot(message: String) {
        val queryInput = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build()
        GlobalScope.launch {
            val detectIntentRequest = DetectIntentRequest.newBuilder()
                .setSession(sessionName.toString())
                .setQueryInput(queryInput)
                .build()
            try {
                val response = sessionsClient?.detectIntent(detectIntentRequest)
                withContext(Dispatchers.Main) {
                    if (response != null) {
                        handleBotResponse(response)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "sendMessageToBot: ${e.message}")
            }
        }
    }

    // Method to handle bot response
    private fun handleBotResponse(response: DetectIntentResponse) {
        val botReply = response.queryResult.fulfillmentText
        if (botReply.isNotEmpty()) {
            addMessageToChat(botReply, false)
        } else {
            addMessageToChat("Sorry, I didn't get that. Can you please repeat?", false)
        }
    }

    // Setup bot
    private fun setupBot() {
        try {
            val stream = resources.openRawResource(R.raw.cred)
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
            val projectId = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder = SessionsSettings.newBuilder()
            val sessionsSettings = settingsBuilder.setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)
            ).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            sessionName = SessionName.of(projectId, uuid)

        } catch (e: Exception) {
            Log.e(TAG, "setupBot: ${e.message}")
        }
    }

    // Save user responses to SharedPreferences
    private fun saveUserResponses() {
        val sharedPreferences = getSharedPreferences("UserResponses", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(userResponses)
        editor.putString("responses", json)
        editor.apply()
    }
}
