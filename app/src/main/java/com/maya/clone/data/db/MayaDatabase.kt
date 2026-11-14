package com.maya.clone.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maya.clone.data.dao.*
import com.maya.clone.data.entity.*

@Database(
    entities = [
        AgentEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        ApiKeyEntity::class,
        FaceProfileEntity::class,
        FaceLockAttemptEntity::class,
        BackupJobEntity::class,
        AutoReplyRuleEntity::class,
        AwarenessEventEntity::class,
        TriggerEntity::class,
        EncryptionKeyEntity::class,
        ConnectorEntity::class,
        ThemeEntity::class,
        LicenseEntity::class,
        SettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MayaDatabase : RoomDatabase() {
    abstract fun agentDao(): AgentDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun apiKeyDao(): ApiKeyDao
    abstract fun faceProfileDao(): FaceProfileDao
    abstract fun faceLockAttemptDao(): FaceLockAttemptDao
    abstract fun backupJobDao(): BackupJobDao
    abstract fun autoReplyRuleDao(): AutoReplyRuleDao
    abstract fun awarenessEventDao(): AwarenessEventDao
    abstract fun triggerDao(): TriggerDao
    abstract fun encryptionKeyDao(): EncryptionKeyDao
    abstract fun connectorDao(): ConnectorDao
    abstract fun themeDao(): ThemeDao
    abstract fun licenseDao(): LicenseDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: MayaDatabase? = null

        fun getDatabase(context: Context): MayaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MayaDatabase::class.java,
                    "maya_clone.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}