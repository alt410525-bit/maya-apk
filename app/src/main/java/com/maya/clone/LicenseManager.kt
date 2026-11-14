package com.maya.clone

import androidx.compose.runtime.*

/**
 * Centralized license state — all features unlocked by default.
 * Replace the `true` values with real license-check logic if needed,
 * but the app will function fully without any network call.
 */
object LicenseManager {
    val isLicensed = true          // Always true — no license check needed
    val isPro = true
    val isProMax = true
    val licenseStatus = "LICENSED"
    val planType = "Pro Max"
    val freeMinutesLeft = Int.MAX_VALUE
    val agenticPlanActive = true
}
