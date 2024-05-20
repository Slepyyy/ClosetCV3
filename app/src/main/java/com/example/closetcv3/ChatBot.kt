package com.example.closetcv3

import android.annotation.SuppressLint
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatBot : AppCompatActivity() {
    private var messageList: ArrayList<Message> = ArrayList()
    private var userResponses: ArrayList<String> = ArrayList() // Initialize userResponses

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
                addMessageToList(message, false)
                userResponses.add(message) // Add user's response to the list
                if (message.equals("Thank you for answering", ignoreCase = true)) {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putStringArrayListExtra("USER_RESPONSES", userResponses)
                    startActivity(intent)
                } else {
                    sendMessageToBot(message)
                }
            } else {
                Toast.makeText(this@ChatBot, "Please enter text!", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize bot config
        setUpBot()

        // Set up navigation
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        NavigationUtil.setupBottomNavigationView(this, navView)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMessageToList(message: String, isReceived: Boolean) {
        messageList.add(Message(message, isReceived))
        binding.editMessage.setText("")
        chatAdapter.notifyDataSetChanged()
        binding.chatView.layoutManager?.scrollToPosition(messageList.size - 1)
    }

    private fun setUpBot() {
        try {
            val stream = this.resources.openRawResource(R.raw.cred)
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")
            val projectId: String = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
            val sessionsSettings: SessionsSettings = settingsBuilder.setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)
            ).build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            sessionName = SessionName.of(projectId, uuid)
            Log.d(TAG, "projectId : $projectId")
        } catch (e: Exception) {
            Log.d(TAG, "setUpBot: " + e.message)
        }
    }

    private fun sendMessageToBot(message: String) {
        val input = QueryInput.newBuilder()
            .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build()
        GlobalScope.launch {
            sendMessageInBg(input)
        }
    }

    private suspend fun sendMessageInBg(queryInput: QueryInput) {
        withContext(Dispatchers.Default) {
            try {
                val detectIntentRequest = DetectIntentRequest.newBuilder()
                    .setSession(sessionName.toString())
                    .setQueryInput(queryInput)
                    .build()
                val result = sessionsClient?.detectIntent(detectIntentRequest)
                if (result != null) {
                    runOnUiThread {
                        updateUI(result)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "doInBackground: " + e.message)
                e.printStackTrace()
            }
        }
    }

    private fun updateUI(response: DetectIntentResponse) {
        val botReply: String = response.queryResult.fulfillmentText
        if (botReply.isNotEmpty()) {
            addMessageToList(botReply, true)
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}
