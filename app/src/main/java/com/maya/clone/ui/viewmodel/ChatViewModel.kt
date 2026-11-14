package com.maya.clone.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maya.clone.data.entity.MessageEntity
import com.maya.clone.data.entity.ConversationEntity
import com.maya.clone.data.repository.ConversationRepository
import com.maya.clone.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming = _isStreaming.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentConversationId: String? = null
    private var currentUserId: String? = null

    fun init(conversationId: String, userId: String) {
        currentConversationId = conversationId
        currentUserId = userId
        observeMessages(conversationId)
    }

    private fun observeMessages(conversationId: String) {
        viewModelScope.launch {
            messageRepository.getMessagesForConversation(conversationId)
                .collect { msgs ->
                    _messages.value = msgs
                }
        }
    }

    fun sendMessage(content: String) {
        currentConversationId?.let { conversationId ->
            currentUserId?.let { userId ->
                val userMessage = MessageEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    role = "user",
                    content = content,
                    status = "sent",
                    createdAt = System.currentTimeMillis()
                )

                val assistantMessage = MessageEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    role = "assistant",
                    content = "",
                    status = "streaming",
                    createdAt = System.currentTimeMillis()
                )

                viewModelScope.launch {
                    messageRepository.createMessages(listOf(userMessage, assistantMessage))
                    _isStreaming.value = true
                    // TODO: Call LLM API via Edge Function, stream tokens
                    // For now, simulate streaming
                    simulateStreaming(assistantMessage.id, "This is a simulated response from Maya. In production, this would come from the LLM API.")
                }
            }
        }
    }

    private fun simulateStreaming(messageId: String, fullText: String) {
        viewModelScope.launch {
            val words = fullText.split(" ")
            var currentText = ""
            for (word in words) {
                currentText += word + " "
                val updatedMessages = _messages.value.map { msg ->
                    if (msg.id == messageId) {
                        msg.copy(content = currentText.trim(), status = "streaming")
                    } else msg
                }
                _messages.value = updatedMessages
                kotlinx.coroutines.delay(50)
            }
            // Mark as complete
            val finalMessages = _messages.value.map { msg ->
                if (msg.id == messageId) {
                    msg.copy(status = "sent")
                } else msg
            }
            _messages.value = finalMessages
            _isStreaming.value = false
        }
    }

    fun retryFailedMessage(messageId: String) {
        // TODO: Implement retry logic
    }
}