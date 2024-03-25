package com.example.sportsbooking.Data.DI

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.models.Config
import io.getstream.chat.android.models.UploadAttachmentsNetworkType
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideOfflinePluginFactory(@ApplicationContext context: Context) =
        StreamOfflinePluginFactory(
            appContext = context,

        )

    fun providesStatePluginFactory(@ApplicationContext context: Context)=
        StreamStatePluginFactory(
            config = StatePluginConfig(),
            appContext = context
        )

    @Singleton
    @Provides
    fun provideChatClient(@ApplicationContext context: Context, offlinePluginFactory: StreamOfflinePluginFactory,statePluginFactory: StreamStatePluginFactory) =
        ChatClient.Builder("qz9brkfjvd5u", context)
            .withPlugins(offlinePluginFactory,statePluginFactory)
            .logLevel(ChatLogLevel.ALL)
            .build()
}

