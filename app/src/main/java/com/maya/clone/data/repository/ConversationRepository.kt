package com.maya.clone.data.repository

import com.maya.clone.data.dao.ConversationDao
import com.maya.clone.data.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepository @Inject constructor(
    private val conversationDao: ConversationDao
) {
    suspend fun createConversation(conversation: ConversationEntity) {
        conversationDao.insert(conversation)
    }

    suspend fun updateConversation(conversation: ConversationEntity) {
        conversationDao.update(conversation)
    }

    suspend fun deleteConversation(id: String) {
        conversationDao.deleteById(id)
    }

    suspend fun getConversation(id: String): ConversationEntity? {
        return conversationDao.getById(id)
    }

    fun getConversationsForUser(userId: String): Flow<List<ConversationEntity>> {
        return conversationDao.getAllForUser(userId)
    }

    fun getConversationsForAgent(agentId: String): Flow<List<ConversationEntity>> {
        return conversationDao.getByAgent(agentId)
    }

    suspend fun getRecentConversations(userId: String, limit: Int): List<ConversationEntity> {
        return conversationDao.getRecentForUser(userId, limit)
    }
}