package com.maya.clone.data.repository

import com.maya.clone.data.dao.AgentDao
import com.maya.clone.data.entity.AgentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRepository @Inject constructor(
    private val agentDao: AgentDao
) {
    suspend fun createAgent(agent: AgentEntity) {
        agentDao.insert(agent)
    }

    suspend fun updateAgent(agent: AgentEntity) {
        agentDao.update(agent)
    }

    suspend fun deleteAgent(id: String) {
        agentDao.deleteById(id)
    }

    suspend fun getAgent(id: String): AgentEntity? {
        return agentDao.getById(id)
    }

    fun getActiveAgents(userId: String): Flow<List<AgentEntity>> {
        return agentDao.getActiveForUser(userId)
    }

    fun getAllAgents(userId: String): Flow<List<AgentEntity>> {
        return agentDao.getAllForUser(userId)
    }

    suspend fun countActiveAgents(userId: String): Int {
        return agentDao.countActiveForUser(userId)
    }
}