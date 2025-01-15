/*
 * Copyright (c) 2025 RohitrajKhorwal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.android.securesms.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.multidex.MultiDexApplication
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import com.gu.toolargetool.TooLargeTool
/**
 * The SMS application.
 *
 * Project Created on: Jan 15, 2025
 * @author Rohit Raj Khorwal
 * @since v1.0.0
 * @see <a href="mailto:banrossyn@gmail.com">banrossyn@gmail.com</a>
 */
@HiltAndroidApp
class MyApplication : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    companion object {
        val isDebug: Boolean by lazy { BuildConfig.DEBUG }
    }

    override fun onCreate() {
        super<MultiDexApplication>.onCreate()

        // Registers activity lifecycle callbacks
        this.registerActivityLifecycleCallbacks(this)

        DynamicColors.applyToActivitiesIfAvailable(this)     // Apply dynamic color theming to all activities

        if (isDebug) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                StrictMode.setVmPolicy(
                    StrictMode.VmPolicy.Builder().detectUnsafeIntentLaunch().build()
                )
            }
            // Start logging for tooLarge Exception checks
            TooLargeTool.startLogging(this)

            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "(${element.fileName}:${element.lineNumber})"
                }

                override fun log(
                    priority: Int, tag: String?, message: String, t: Throwable?
                ) {
                    val enhancedMessage = "[SMS]--->  $message"
                    super.log(priority, tag, enhancedMessage, t)
                }
            })

        }
        Timber.d("onCreate: ")

        // Initialize Firebase services and enable Crashlytics
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        applicationScope.launch {
            FirebaseApp.initializeApp(this@MyApplication)
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !isDebug
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Timber.d("onConfigurationChanged: ")
        super.onConfigurationChanged(newConfig)
    }

    /**
     * Stops logging for tooLarge Exceptions, unregisters activity lifecycle callbacks,
     * and terminates the application.
     */
    override fun onTerminate() {
        Timber.d("onTerminate: ")
        if (isDebug) TooLargeTool.stopLogging(this)
        this.unregisterActivityLifecycleCallbacks(this)
        super.onTerminate()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

}