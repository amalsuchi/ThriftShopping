package com.example.sportsbooking.Data.DI

import android.app.Application
import android.content.Context

import com.example.sportsbooking.Data.location.LocationHelper

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesApplicationContext(application: Application): Context = application

    @Provides
    @Singleton
    fun provideLocationHelper(context: Context): LocationHelper {
        return LocationHelper(context)
    }
}

 */