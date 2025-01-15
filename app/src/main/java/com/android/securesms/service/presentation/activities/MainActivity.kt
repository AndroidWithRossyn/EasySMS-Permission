package com.android.securesms.service.presentation.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.securesms.service.R
import com.android.securesms.service.databinding.ActivityMainBinding
import com.android.securesms.service.presentation.adapters.SmsAdapter
import com.android.securesms.service.presentation.viewmodels.SmsViewModel
import com.android.securesms.service.utils.PermissionManager
import com.android.securesms.service.utils.ToastUtils.showToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DefaultLifecycleObserver, View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var permissionManager: PermissionManager
    private val viewModel: SmsViewModel by viewModels()
    private val smsAdapter = SmsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        this.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lifecycle.addObserver(this)


    }

    override fun onCreate(owner: LifecycleOwner) {
        super<DefaultLifecycleObserver>.onCreate(owner)

        binding.recyclerView.apply {
            adapter = smsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        if (permissionManager.checkAllPermissions()) {
            observeSmsMessages()
        } else {
            allPermissionResult.launch(permissionManager.requestAllPermissions())
        }
    }

    private fun observeSmsMessages() {
        lifecycleScope.launch {
            viewModel.register()
            delay(2000)
            viewModel.smsMessages.collect { messages ->
                smsAdapter.submitList(messages)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {

        }
    }

    override fun onStart() {
        super<AppCompatActivity>.onStart()

        /*
          * for new Api Level
          * Callback for handling the [OnBackPressedDispatcher.onBackPressed] event.
          */
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    isEnabled = false
                    moveTaskToBack(false)
                }
            }
        })
    }

    private val allPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (!allGranted) {
            MaterialAlertDialogBuilder(this)
                .setIcon(R.mipmap.ic_launcher_round)
                .setTitle("Permission Required")
                .setMessage("This App requires for Particular features to work as expected as Show Sms")
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.open_settings)) { _, _ ->
                    openAppPermission()
                }
                .setNegativeButton(resources.getString(R.string.exit)) { _, _ ->
                    finishAffinity()
                }.create().show()
        } else {
            observeSmsMessages()
        }
    }

    fun openAppPermission() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Timber.e(e, "Error opening Application Details Settings. Attempting fallback...")

            try {
                // Fallback: Open general application settings
                val fallbackIntent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
                startActivity(fallbackIntent)
                finish()
            } catch (fallbackException: Exception) {
                Timber.e(fallbackException, "Error opening Application Settings.")
                showToast("Could not open app settings")
            }
        }
    }

    /**
     * On destroy
     * Remove Lifecycle observer
     */
    override fun onDestroy() {
        lifecycle.removeObserver(this)
        super<AppCompatActivity>.onDestroy()
    }
}