package com.maya.clone.di

import android.app.Application
import android.content.Context
import com.maya.clone.data.repository.AgentRepository
import com.maya.clone.data.repository.ConversationRepository
import com.maya.clone.data.repository.MessageRepository
import com.maya.clone.data.repository.SettingRepository
import com.maya.clone.data.util.SeedData
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MayaApplication : Application() {

    @Inject lateinit var agentRepository: AgentRepository
    @Inject lateinit var conversationRepository: ConversationRepository
    @Inject lateinit var messageRepository: MessageRepository
    @Inject lateinit var settingRepository: SettingRepository

    override fun onCreate() {
        super.onCreate()
        instance = this
        seedDemoData()
    }

    private fun seedDemoData() {
        SeedData.getInstance(
            agentRepository,
            conversationRepository,
            messageRepository,
            settingRepository
        ).seedIfNeeded(this)
    }

    companion object {
        @Volatile
        var instance: MayaApplication? = null
            private set

        @JvmStatic
        fun getContext(): Context? = instance
    }
}
