@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.maya.clone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maya.clone.Navigation
import com.maya.clone.ui.theme.*

@Composable
fun PrivacyScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = { Text("Privacy Policy", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextSecondary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Last updated
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text("Last updated: November 2026", color = TextTertiary, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy sections
        PrivacySection(
            title = "1. Information We Collect",
            content = """
                We collect information you provide directly to us, such as when you create an account, 
                use our AI chat features, or contact us for support. This may include:
                • Account information (name, email, preferences)
                • Chat messages and conversation history
                • Voice recordings (when using voice features)
                • Face data (when using Face Lock, stored locally on device)
                • Usage data and analytics (if enabled)
                • Device information (model, OS version, app version)
            """
        )

        PrivacySection(
            title = "2. How We Use Your Information",
            content = """
                We use the information we collect to:
                • Provide, maintain, and improve our AI assistant services
                • Process and respond to your requests and conversations
                • Enable voice and face recognition features (processed locally when possible)
                • Send notifications and alerts (if enabled)
                • Improve our services through analytics (if opted in)
                • Ensure security and prevent fraud
                • Comply with legal obligations
            """
        )

        PrivacySection(
            title = "3. Data Storage & Security",
            content = """
                • Face embeddings and voice data are stored locally on your device when possible
                • Cloud backups are encrypted with AES-256-GCM before upload
                • API keys are encrypted at rest using Android Keystore
                • We use industry-standard encryption for data in transit (TLS 1.3)
                • You can delete your data at any time from Settings > Backup & Sync > Delete All Data
            """
        )

        PrivacySection(
            title = "4. Data Sharing",
            content = """
                We do not sell your personal information. We may share data only:
                • With your explicit consent
                • With service providers who process data on our behalf (e.g., cloud hosting, AI APIs)
                • When required by law or to protect rights and safety
                • In aggregated, anonymized form for analytics
            """
        )

        PrivacySection(
            title = "5. Your Rights",
            content = """
                Depending on your location, you may have the right to:
                • Access your personal data
                • Correct inaccurate data
                • Request deletion of your data
                • Restrict or object to processing
                • Data portability
                • Withdraw consent at any time
                To exercise these rights, contact us at privacy@maya-ai.example.com or use the 
                "Delete Account" option in Settings > Personal Info.
            """
        )

        PrivacySection(
            title = "6. Children's Privacy",
            content = """
                Our services are not directed to children under 13 (or 16 in some jurisdictions). 
                We do not knowingly collect personal information from children. If you believe 
                we have collected data from a child, please contact us immediately.
            """
        )

        PrivacySection(
            title = "7. International Transfers",
            content = """
                Your information may be transferred to and processed in countries other than your 
                own. We ensure appropriate safeguards are in place, such as Standard Contractual 
                Clauses, for any international data transfers.
            """
        )

        PrivacySection(
            title = "8. Changes to This Policy",
            content = """
                We may update this Privacy Policy from time to time. We will notify you of any 
                material changes by posting the new policy in the app and updating the "Last 
                Updated" date. Your continued use of the app after changes constitutes acceptance.
            """
        )

        PrivacySection(
            title = "9. Contact Us",
            content = """
                If you have questions about this Privacy Policy or our data practices, please contact us at:
                • Email: privacy@maya-ai.example.com
                • In-app: Settings > Privacy > Contact Us
                • Mail: Maya AI, 123 Tech Street, San Francisco, CA 94102, USA
            """
        )
    }
}

@Composable
private fun PrivacySection(title: String, content: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = NeonPurple,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = content.trimIndent(),
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
        Divider(color = DividerColor, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
    }
}