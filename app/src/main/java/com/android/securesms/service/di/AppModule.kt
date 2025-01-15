package com.android.securesms.service.di

import android.content.Context
import android.content.res.Resources
import com.android.securesms.service.domain.repository.SmsRepository
import com.android.securesms.service.utils.PermissionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    fun providePermission(@ApplicationContext context: Context): PermissionManager {
        return PermissionManager(context)
    }

    @Provides
    fun provideSmsRepo(@ApplicationContext context: Context): SmsRepository {
        return SmsRepository(context)
    }


}