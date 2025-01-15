package com.android.securesms.service.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.securesms.service.data.FirestoreHelper
import com.android.securesms.service.domain.model.SmsMessage
import com.android.securesms.service.domain.repository.SmsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SmsViewModel @Inject constructor(
    private val smsRepository: SmsRepository,
    private val firestoreHelper: FirestoreHelper
) : ViewModel() {

    val smsMessages = smsRepository.smsMessages.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun register() {
        smsRepository.startSetup()
    }

    override fun onCleared() {
        super.onCleared()
        smsRepository.cleanup()
    }

    fun saveBulkSms(smsList: List<SmsMessage>) {
        firestoreHelper.saveBulkSmsToFirestore(smsList) { success, message ->
            if (success) {
                Log.d("SmsViewModel", "Bulk SMS save successful: $message")
            } else {
                Log.e("SmsViewModel", "Failed to save bulk SMS: $message")
            }
        }
    }
}