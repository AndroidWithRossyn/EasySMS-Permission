package com.android.securesms.service.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.securesms.service.domain.repository.SmsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SmsViewModel @Inject constructor(
    private val smsRepository: SmsRepository
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
}