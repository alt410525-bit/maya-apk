package com.maya.clone.data.util

import android.content.Context
import com.maya.clone.data.repository.AgentRepository
import com.maya.clone.data.repository.ConversationRepository
import com.maya.clone.data.repository.MessageRepository
import com.maya.clone.data.entity.AgentEntity
import com.maya.clone.data.entity.ConversationEntity
import com.maya.clone.data.entity.MessageEntity
import com.maya.clone.data.entity.SettingEntity
import com.maya.clone.data.repository.SettingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Utility class for seeding demo data on first app launch.
 * Creates a demo user, 3 agents (Assistant, Coder, Creative), and 2 conversations with messages.
 */
class SeedData private constructor(
    private val agentRepository: AgentRepository,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val settingRepository: SettingRepository
) {

    companion object {
        private const val SEED_COMPLETED_KEY = "seed_data_completed"
        private const val DEMO_USER_ID = "demo_user_001"

        @Volatile
        private var INSTANCE: SeedData? = null

        fun getInstance(
            agentRepository: AgentRepository,
            conversationRepository: ConversationRepository,
            messageRepository: MessageRepository,
            settingRepository: SettingRepository
        ): SeedData {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SeedData(
                    agentRepository,
                    conversationRepository,
                    messageRepository,
                    settingRepository
                ).also { INSTANCE = it }
            }
        }

        /**
         * Checks if seed data has already been initialized.
         */
        suspend fun isSeedCompleted(settingRepository: SettingRepository): Boolean {
            val setting = settingRepository.getSettingByKey(DEMO_USER_ID, SEED_COMPLETED_KEY)
            return setting?.valueJson == "true"
        }

        /**
         * Marks seed data as completed.
         */
        suspend fun markSeedCompleted(settingRepository: SettingRepository) {
            val setting = SettingEntity(
                id = UUID.randomUUID().toString(),
                userId = DEMO_USER_ID,
                key = SEED_COMPLETED_KEY,
                valueJson = "true"
            )
            settingRepository.saveSetting(setting)
        }
    }

    /**
     * Seeds the database with demo data if not already seeded.
     * Should be called on first app launch (e.g., from Application.onCreate or WorkManager).
     */
    fun seedIfNeeded(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val completed = isSeedCompleted(settingRepository)
            if (!completed) {
                seedDemoData()
                markSeedCompleted(settingRepository)
            }
        }
    }

    /**
     * Seeds the database with demo data.
     */
    private suspend fun seedDemoData() {
        // Create demo agents
        val assistantAgent = createAgent(
            id = "agent_assistant",
            name = "Assistant",
            systemPrompt = "You are a helpful, friendly AI assistant. Provide clear, concise, and accurate responses.",
            model = "gpt-4o-mini",
            temperature = 0.7f,
            toolsJson = """["web_search", "code_execution", "file_operations"]""",
            permissionsJson = """{"web_search": true, "code_execution": true, "file_operations": true}"""
        )

        val coderAgent = createAgent(
            id = "agent_coder",
            name = "Coder",
            systemPrompt = """You are an expert software engineer. Help users write, debug, and optimize code.
Follow best practices, write clean code, and explain your reasoning.
You can write in any programming language and framework.""",
            model = "gpt-4o",
            temperature = 0.3f,
            toolsJson = """["code_execution", "file_operations", "git_operations", "terminal"]""",
            permissionsJson = """{"code_execution": true, "file_operations": true, "git_operations": true, "terminal": true}"""
        )

        val creativeAgent = createAgent(
            id = "agent_creative",
            name = "Creative",
            systemPrompt = """You are a creative AI assistant specialized in writing, brainstorming, and creative tasks.
Help with stories, articles, marketing copy, ideas, and artistic concepts.
Be imaginative, engaging, and inspiring.""",
            model = "gpt-4o-mini",
            temperature = 0.9f,
            toolsJson = """["web_search", "image_generation"]""",
            permissionsJson = """{"web_search": true, "image_generation": true}"""
        )

        agentRepository.createAgent(assistantAgent)
        agentRepository.createAgent(coderAgent)
        agentRepository.createAgent(creativeAgent)

        // Create demo conversations
        val conversation1 = ConversationEntity(
            id = "conv_001",
            userId = DEMO_USER_ID,
            agentId = "agent_assistant",
            title = "Welcome to Maya",
            messageCount = 4,
            lastMessageAt = System.currentTimeMillis() - 1000 * 60 * 30, // 30 min ago
            createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2, // 2 hours ago
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 30
        )

        val conversation2 = ConversationEntity(
            id = "conv_002",
            userId = DEMO_USER_ID,
            agentId = "agent_coder",
            title = "Kotlin Coroutines Help",
            messageCount = 6,
            lastMessageAt = System.currentTimeMillis() - 1000 * 60 * 10, // 10 min ago
            createdAt = System.currentTimeMillis() - 1000 * 60 * 60, // 1 hour ago
            updatedAt = System.currentTimeMillis() - 1000 * 60 * 10
        )

        conversationRepository.createConversation(conversation1)
        conversationRepository.createConversation(conversation2)

        // Create messages for conversation 1 (Welcome to Maya)
        val messages1 = listOf(
            MessageEntity(
                id = "msg_001",
                conversationId = "conv_001",
                role = "system",
                content = "Welcome to Maya! I'm your AI assistant. How can I help you today?",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2
            ),
            MessageEntity(
                id = "msg_002",
                conversationId = "conv_001",
                role = "user",
                content = "Hi! What can you do?",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2 + 5000
            ),
            MessageEntity(
                id = "msg_003",
                conversationId = "conv_001",
                role = "assistant",
                content = "I can help you with a wide range of tasks:\n\n• Answer questions and explain concepts\n• Write, debug, and review code\n• Creative writing and brainstorming\n• Research and web searches\n• File operations and data analysis\n• Task planning and organization\n\nJust let me know what you need!",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2 + 10000
            ),
            MessageEntity(
                id = "msg_004",
                conversationId = "conv_001",
                role = "user",
                content = "That's great! Tell me about the different agents.",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 30
            )
        )

        // Create messages for conversation 2 (Kotlin Coroutines Help)
        val messages2 = listOf(
            MessageEntity(
                id = "msg_005",
                conversationId = "conv_002",
                role = "system",
                content = "You are Coder, an expert software engineer. Help with Kotlin coroutines.",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60
            ),
            MessageEntity(
                id = "msg_006",
                conversationId = "conv_002",
                role = "user",
                content = "How do I use structured concurrency in Kotlin?",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 + 5000
            ),
            MessageEntity(
                id = "msg_007",
                conversationId = "conv_002",
                role = "assistant",
                content = "Structured concurrency in Kotlin is achieved through **coroutine scopes**. Here's the key concept:\n\n```kotlin\ncoroutineScope {\n    launch { /* child 1 */ }\n    launch { /* child 2 */ }\n    // Waits for both children to complete\n}\n```\n\nKey points:\n• **coroutineScope** - suspends until all children complete\n• **supervisorScope** - children failures don't cancel siblings\n• **structured** - parent waits for children, automatic cancellation propagation\n\nWould you like a practical example?",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 + 15000
            ),
            MessageEntity(
                id = "msg_008",
                conversationId = "conv_002",
                role = "user",
                content = "Yes, show me a practical example with error handling.",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 30
            ),
            MessageEntity(
                id = "msg_009",
                conversationId = "conv_002",
                role = "assistant",
                content = "Here's a practical example with error handling:\n\n```kotlin\nsuspend fun fetchUserData(userIds: List<String>): List<User> = coroutineScope {\n    userIds.map { id ->\n        async {\n            try {\n                api.getUser(id)\n            } catch (e: Exception) {\n                User.Error(id, e.message)\n            }\n        }\n    }.awaitAll()\n}\n```\n\n**Key patterns**:\n1. **async/awaitAll** - parallel execution with results\n2. **try/catch in children** - isolates failures\n3. **Result wrapper** - handles success/error uniformly\n\nFor supervisor behavior (one failure doesn't cancel others):\n```kotlin\nsupervisorScope {\n    userIds.map { id ->\n        async { api.getUser(id) }\n    }.awaitAll()\n}\n```",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 20
            ),
            MessageEntity(
                id = "msg_010",
                conversationId = "conv_002",
                role = "user",
                content = "Perfect! What about cancellation?",
                status = "sent",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 10
            )
        )

        messageRepository.createMessages(messages1)
        messageRepository.createMessages(messages2)
    }

    private fun createAgent(
        id: String,
        name: String,
        systemPrompt: String,
        model: String,
        temperature: Float,
        toolsJson: String,
        permissionsJson: String
    ): AgentEntity {
        val now = System.currentTimeMillis()
        return AgentEntity(
            id = id,
            userId = DEMO_USER_ID,
            name = name,
            avatarUrl = null,
            systemPrompt = systemPrompt,
            model = model,
            temperature = temperature,
            toolsJson = toolsJson,
            permissionsJson = permissionsJson,
            isActive = true,
            createdAt = now,
            updatedAt = now
        )
    }
}