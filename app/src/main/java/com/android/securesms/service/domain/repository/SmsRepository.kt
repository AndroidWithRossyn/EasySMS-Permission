package com.android.securesms.service.domain.repository

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import com.android.securesms.service.domain.model.SmsMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _smsMessages = MutableStateFlow<List<SmsMessage>>(emptyList())
    val smsMessages: StateFlow<List<SmsMessage>> = _smsMessages.asStateFlow()

    private var contentObserver: ContentObserver? = null

    fun startSetup() {
        registerSmsContentObserver()
        loadMessages()
    }

    private fun registerSmsContentObserver() {
        contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                loadMessages()
            }
        }

        context.contentResolver.registerContentObserver(
            Uri.parse("content://sms"), true, contentObserver!!
        )
    }

    private fun loadMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            val messages = readSmsMessages()
            _smsMessages.emit(messages)
        }
    }

    private fun readSmsMessages(): List<SmsMessage> {
        val smsList = mutableListOf<SmsMessage>()
        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI, arrayOf(
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.SERVICE_CENTER,
                Telephony.Sms.PROTOCOL,
                Telephony.Sms.STATUS,
                Telephony.Sms.TYPE
            ), "${Telephony.Sms.TYPE} = ?", arrayOf("1"), // 1 means inbox
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    val sms = SmsMessage(
                        sender = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)),
                        messageBody = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)),
                        timestamp = Date(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))),
                        serviceCenterAddress = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                Telephony.Sms.SERVICE_CENTER
                            )
                        ),
                        protocolIdentifier = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.PROTOCOL)),
                        status = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.STATUS)),
                        isStatusReport = false
                    )
                    smsList.add(sms)
                } while (cursor.moveToNext())
            }
        }

        return smsList
    }

    fun cleanup() {
        contentObserver?.let {
            context.contentResolver.unregisterContentObserver(it)
        }
    }
}
