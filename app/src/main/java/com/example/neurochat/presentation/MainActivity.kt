package com.example.neurochat.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.*

class MainActivity : ComponentActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    override fun onCreate(savedInstanceState: Bundle?) {



        //installSplashScreen()

        super.onCreate(savedInstanceState)

        //Регистрация запроса разрешения

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (!granted) {
                    // TODO: показать предупреждение пользователю
                }
            }

        //Отправка и проверка запроса разрешения

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            VoiceUserMessage(speechRecognizer)
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}

data class Message(val text: String , val isUser: Boolean)

@Composable
fun VoiceUserMessage(speechRecognizer: SpeechRecognizer) {
    var messages by remember { mutableStateOf(listOf<Message>()) }

    fun startListening() {
        var UserResponse = "Привет"
        messages = messages + Message(UserResponse, true)
        sendMessageToGemini(messages.last().text) { reply ->
            messages = messages + Message(reply, false)
        }
        //val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        //    putExtra(
//                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//            )
//            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU") // русский язык
//        }

//        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
//            override fun onReadyForSpeech(params: Bundle?) {}
//            override fun onBeginningOfSpeech() {}
//            override fun onRmsChanged(rmsdB: Float) {}
//            override fun onBufferReceived(buffer: ByteArray?) {}
//            override fun onEndOfSpeech() {}
//            override fun onError(error: Int) {
//                messages = messages + Message("Ошибка распознавания ($error)", false)
//            }
//
//            override fun onResults(results: Bundle?) {
//                val spokenText =
//                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
//                if (spokenText != null) {
//                    messages = messages + Message(spokenText, true)
//                    // Здесь можно отправить spokenText в нейросеть
//                }
//            }
//
//            override fun onPartialResults(partialResults: Bundle?) {}
//            override fun onEvent(eventType: Int, params: Bundle?) {}
//        })
//
//        speechRecognizer.startListening(intent)

    }

    ChatScreen(messages = messages, onMicClick = { startListening() })
}

@Composable
fun ChatScreen(messages: List<Message>, onMicClick: () -> Unit) {
    Scaffold(
        timeText = { },
        vignette = { },
        positionIndicator = { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp)
        ) {
            // Заголовок
            Text(
                text = "ChatAI",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )

            // Список сообщений
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg)
                }
            }

            // Кнопка микрофона
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onMicClick,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Voice input",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val bubbleColor = if (message.isUser) Color(0xFF2F2F2F) else Color(0xFF0A84FF)
    val textColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        contentAlignment = if (message.isUser) Alignment.CenterStart else Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}


fun sendMessageToGemini(userText: String , onReply: (String) -> Unit) {
    GeminiApi.sendMessage(userText){ reply ->
        onReply(reply)
    }
}