package com.maya.clone.data.di

import android.content.Context
import com.maya.clone.data.dao.AgentDao
import com.maya.clone.data.dao.ApiKeyDao
import com.maya.clone.data.dao.AutoReplyRuleDao
import com.maya.clone.data.dao.AwarenessEventDao
import com.maya.clone.data.dao.BackupJobDao
import com.maya.clone.data.dao.ConnectorDao
import com.maya.clone.data.dao.ConversationDao
import com.maya.clone.data.dao.EncryptionKeyDao
import com.maya.clone.data.dao.FaceLockAttemptDao
import com.maya.clone.data.dao.FaceProfileDao
import com.maya.clone.data.dao.LicenseDao
import com.maya.clone.data.dao.MessageDao
import com.maya.clone.data.dao.SettingDao
import com.maya.clone.data.dao.ThemeDao
import com.maya.clone.data.dao.TriggerDao
import com.maya.clone.data.db.MayaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides the Room database and its DAOs.
 * Repositories are constructed by Hilt through their @Inject constructors,
 * so they must NOT be provided here (that would create duplicate bindings).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MayaDatabase =
        MayaDatabase.getDatabase(context)

    @Provides
    fun provideAgentDao(database: MayaDatabase): AgentDao = database.agentDao()

    @Provides
    fun provideConversationDao(database: MayaDatabase): ConversationDao = database.conversationDao()

    @Provides
    fun provideMessageDao(database: MayaDatabase): MessageDao = database.messageDao()

    @Provides
    fun provideApiKeyDao(database: MayaDatabase): ApiKeyDao = database.apiKeyDao()

    @Provides
    fun provideFaceProfileDao(database: MayaDatabase): FaceProfileDao = database.faceProfileDao()

    @Provides
    fun provideFaceLockAttemptDao(database: MayaDatabase): FaceLockAttemptDao = database.faceLockAttemptDao()

    @Provides
    fun provideBackupJobDao(database: MayaDatabase): BackupJobDao = database.backupJobDao()

    @Provides
    fun provideAutoReplyRuleDao(database: MayaDatabase): AutoReplyRuleDao = database.autoReplyRuleDao()

    @Provides
    fun provideAwarenessEventDao(database: MayaDatabase): AwarenessEventDao = database.awarenessEventDao()

    @Provides
    fun provideTriggerDao(database: MayaDatabase): TriggerDao = database.triggerDao()

    @Provides
    fun provideEncryptionKeyDao(database: MayaDatabase): EncryptionKeyDao = database.encryptionKeyDao()

    @Provides
    fun provideConnectorDao(database: MayaDatabase): ConnectorDao = database.connectorDao()

    @Provides
    fun provideThemeDao(database: MayaDatabase): ThemeDao = database.themeDao()

    @Provides
    fun provideLicenseDao(database: MayaDatabase): LicenseDao = database.licenseDao()

    @Provides
    fun provideSettingDao(database: MayaDatabase): SettingDao = database.settingDao()
}
