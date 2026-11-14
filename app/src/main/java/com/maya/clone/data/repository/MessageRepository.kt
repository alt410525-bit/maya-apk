package com.maya.clone.data.repository

import com.maya.clone.data.dao.MessageDao
import com.maya.clone.data.entity.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val messageDao: MessageDao
) {
    suspend fun createMessage(message: MessageEntity) {
        messageDao.insert(message)
    }

    suspend fun createMessages(messages: List<MessageEntity>) {
        messageDao.insertAll(messages)
    }

    suspend fun updateMessage(message: MessageEntity) {
        messageDao.update(message)
    }

    suspend fun deleteMessage(id: String) {
        messageDao.deleteById(id)
    }

    suspend fun deleteMessagesByConversation(conversationId: String) {
        messageDao.deleteByConversation(conversationId)
    }

    suspend fun getMessage(id: String): MessageEntity? {
        return messageDao.getById(id)
    }

    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>> {
        return messageDao.getByConversation(conversationId)
    }

    fun getPendingMessagesForConversation(conversationId: String): Flow<List<MessageEntity>> {
        return messageDao.getPendingForConversation(conversationId)
    }

    fun getAllFailedMessages(): Flow<List<MessageEntity>> {
        return messageDao.getAllFailed()
    }

    suspend fun countMessagesInConversation(conversationId: String): Int {
        return messageDao.countByConversation(conversationId)
    }
}