package com.ticonsys.desco.di

import android.content.Context
import com.ticonsys.desco.util.SharedPref
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
    fun provideSharedPref(
        @ApplicationContext context: Context
    ): SharedPref = SharedPref(
        context
    )
}