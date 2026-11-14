package com.maya.clone.billing

import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase

interface BillingCallback {
    fun onBillingSetupFinished(billingResult: BillingResult)
    fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?)
}