package com.android.securesms.service.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.securesms.service.databinding.SmsItemLayoutBinding
import com.android.securesms.service.domain.model.SmsMessage
import java.text.SimpleDateFormat
import java.util.Locale

class SmsAdapter(
) : RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    private var smsList: List<SmsMessage> = emptyList()

    fun submitList(newList: List<SmsMessage>) {
        smsList = newList
        notifyDataSetChanged()
    }

    inner class SmsViewHolder(private val binding: SmsItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(smsData: SmsMessage) {
            binding.apply {
                binding.smsSenderTextView.text = smsData.sender ?: "Unknown Sender"
                binding.smsBodyTextView.text = smsData.messageBody ?: "No Message"

                // Format the timestamp to a readable date format
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                binding.smsDateTextView.text = dateFormat.format(smsData.timestamp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val binding =
            SmsItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SmsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        holder.bind(smsList[position])
    }

    override fun getItemCount(): Int {
        return smsList.size
    }
}