package com.android.securesms.service.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class SmsMessage(
    val sender: String?,
    val messageBody: String?,
    val timestamp: Date,
    val serviceCenterAddress: String?,
    val protocolIdentifier: Int,
    val status: Int,
    val isStatusReport: Boolean
): Parcelable