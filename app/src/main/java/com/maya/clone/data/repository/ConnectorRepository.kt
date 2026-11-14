package com.maya.clone.data.repository

import com.maya.clone.data.dao.ConnectorDao
import com.maya.clone.data.entity.ConnectorEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectorRepository @Inject constructor(
    private val connectorDao: ConnectorDao
) {
    suspend fun saveConnector(connector: ConnectorEntity) {
        connectorDao.insert(connector)
    }

    suspend fun updateConnector(connector: ConnectorEntity) {
        connectorDao.update(connector)
    }

    suspend fun deleteConnector(id: String) {
        connectorDao.deleteById(id)
    }

    suspend fun getConnector(id: String): ConnectorEntity? {
        return connectorDao.getById(id)
    }

    fun getAllConnectors(userId: String): Flow<List<ConnectorEntity>> {
        return connectorDao.getAllForUser(userId)
    }

    fun getConnectedConnectors(userId: String): Flow<List<ConnectorEntity>> {
        return connectorDao.getConnectedForUser(userId)
    }
}