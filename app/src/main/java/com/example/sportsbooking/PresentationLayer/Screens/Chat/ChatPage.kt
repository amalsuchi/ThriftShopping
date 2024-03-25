package com.example.sportsbooking.PresentationLayer.Screens.Chat

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.sportsbooking.PresentationLayer.ViewModel.VM
import com.example.sportsbooking.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory

@SuppressLint("CheckResult")
@Composable
fun chatPage(navController: NavController){
    val context = LocalContext.current
    val vm:VM = hiltViewModel()

    LaunchedEffect(Unit ){
        vm.getJwtToken()
    }
    LaunchedEffect(Unit){
        vm.fetchUserData()
    }


    val token by vm.jwtToken.collectAsState()
    Log.d("listdata","$token")
    val response by vm.uiResponse.collectAsState()


    val offlinePluginFactory = StreamOfflinePluginFactory(appContext = context)
    val statePluginFactory = StreamStatePluginFactory(config = StatePluginConfig(), appContext = context)

    val client = ChatClient.Builder("qz9brkfjvd5u", context)
        .withPlugins(offlinePluginFactory, statePluginFactory)
        .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
        .build()

    if(token != null && response.userInfoData?.name != null){
        Log.d("listdata","workded")
        val user = io.getstream.chat.android.models.User(
            name = "appu",
        )

        client.connectUser(
            user = user,
            token = token!!
        )
        val clientInitialisationState by client.clientState.initializationState.collectAsState()
        when (clientInitialisationState) {
            InitializationState.COMPLETE -> {
                ChannelsScreen(
                    title = stringResource(id = R.string.app_name),
                    isShowingSearch = true,
                    onItemClick = { channel ->
                        TODO()
                    },
                   // onBackPressed = { finish() }
                )
            }

            InitializationState.INITIALIZING -> {
                Text(text = "Initializing...")
            }

            InitializationState.NOT_INITIALIZED -> {
                Text(text = "Not initialized...")
            }
        }
    }


  



}