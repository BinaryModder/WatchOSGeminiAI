package com.example.neurochat.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            TextInputScreen()
        }
    }
}

data class Message(val text: String, val isUser: Boolean)

@Composable
fun TextInputScreen() {
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var textState by remember { mutableStateOf(TextFieldValue("")) }

    fun sendMessage() {
        val text = textState.text.trim()
        if (text.isNotBlank()) {
            messages = messages + Message(text, true)
            textState = TextFieldValue("") // Очищаем поле ввода

            // Отправляем сообщение в нейросеть
            sendMessageToGemini(text) { reply ->
                messages = messages + Message(reply, false)
            }
        }
    }

    ChatScreen(
        messages = messages,
        textState = textState,
        onTextChange = { textState = it },
        onSendClick = { sendMessage() }
    )
}

@Composable
fun ChatScreen(
    messages: List<Message>,
    textState: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    onSendClick: () -> Unit
) {
    Scaffold(
        timeText = { },
        vignette = { },
        positionIndicator = { }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
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
                        .fillMaxWidth()
                        .padding(bottom = 60.dp), // 👈 оставляем место для поля ввода
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(messages) { msg ->
                        ChatBubble(msg)
                    }
                }
            }

            // 👇 Поле ввода поверх списка сообщений
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter) // 👈 позиционируем снизу
                    .padding(bottom = 16.dp), // 👈 отступ от самого низа
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BasicTextField(
                    value = textState,
                    onValueChange = onTextChange,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .background(Color(0xFF2F2F2F), RoundedCornerShape(19.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (textState.text.isEmpty()) {
                            Text(
                                "Введите сообщение...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                )

                Button(
                    onClick = onSendClick,
                    modifier = Modifier.size(38.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF0A84FF)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Отправить",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
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

fun sendMessageToGemini(userText: String, onReply: (String) -> Unit) {
    // Заглушка для отправки сообщения
    GeminiApi.sendMessage(userText){ reply ->
        onReply(reply)
    }


}