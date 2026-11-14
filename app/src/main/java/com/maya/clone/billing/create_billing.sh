#!/usr/bin/env bash
# Play Billing integration for Maya Clone
# This creates the necessary classes for Google Play Billing Library integration

echo "Creating Play Billing integration..."

cat > /root/maya_clone_project/app/src/main/java/com/maya/clone/billing/BillingManager.kt << 'EOF'
package com.maya.clone.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.maya.clone.data.entity.LicenseEntity
import com.maya.clone.data.repository.LicenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingManager @Inject constructor(
    private val context: Context,
    private val licenseRepository: LicenseRepository
) {

    private var billingClient: BillingClient? = null
    private var purchasesUpdatedListener: PurchasesUpdatedListener? = null
    private var isBillingSetupComplete = false

    interface BillingCallback {
        fun onBillingSetupFinished(billingResult: BillingResult)
        fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?)
    }

    fun initialize(callback: BillingCallback) {
        purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            callback.onPurchasesUpdated(billingResult, purchases)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                handlePurchases(purchases)
            }
        }

        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener!!)
            .enablePendingPurchases()
            .build()

        startConnection(callback)
    }

    private fun startConnection(callback: BillingCallback) {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                isBillingSetupComplete = billingResult.responseCode == BillingClient.BillingResponseCode.OK
                callback.onBillingSetupFinished(billingResult)
                if (isBillingSetupComplete) {
                    queryPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                isBillingSetupComplete = false
                // Retry connection after delay
                CoroutineScope(Dispatchers.IO).launch {
                    Thread.sleep(5000)
                    startConnection(callback)
                }
            }
        })
    }

    fun queryPurchases() {
        if (!isBillingSetupComplete) return

        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(purchases)
            }
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    val tier = when {
                        purchase.products.contains("promax") -> "promax"
                        purchase.products.contains("pro") -> "pro"
                        else -> "free"
                    }

                    val license = LicenseEntity(
                        id = java.util.UUID.randomUUID().toString(),
                        userId = "default_user", // TODO: get from auth
                        tier = tier,
                        purchaseToken = purchase.purchaseToken,
                        expiresAt = if (tier == "free") null else System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000,
                        featuresJson = getFeaturesForTier(tier),
                        verifiedAt = System.currentTimeMillis()
                    )

                    licenseRepository.saveLicense(license)
                }
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productId: String) {
        if (!isBillingSetupComplete) return

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient?.launchBillingFlow(activity, params)
    }

    fun acknowledgePurchase(purchaseToken: String) {
        if (!isBillingSetupComplete) return

        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(params) { billingResult ->
            // Handle acknowledgment result
        }
    }

    private fun getFeaturesForTier(tier: String): String {
        return when (tier) {
            "free" -> """["basic_chat", "basic_voice"]"""
            "pro" -> """["basic_chat", "basic_voice", "advanced_voice", "sub_agents", "skills", "backup", "face_lock"]"""
            "promax" -> """["basic_chat", "basic_voice", "advanced_voice", "sub_agents", "skills", "backup", "face_lock", "connectors", "auto_reply", "awareness", "triggers", "priority_support"]"""
            else -> """["basic_chat", "basic_voice"]"""
        }
    }

    fun isReady(): Boolean = isBillingSetupComplete

    fun disconnect() {
        billingClient?.endConnection()
        isBillingSetupComplete = false
    }
}
EOF

echo "Play Billing integration created"
EOF
chmod +x /root/maya_clone_project/app/src/main/java/com/maya/clone/billing/create_billing.sh
/root/maya_clone_project/app/src/main/java/com/maya/clone/billing/create_billing.sh