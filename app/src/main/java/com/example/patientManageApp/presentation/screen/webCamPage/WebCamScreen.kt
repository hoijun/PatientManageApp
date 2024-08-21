package com.example.patientManageApp.presentation.screen.webCamPage

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.patientManageApp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun WebCamScreen(rtspUrl: String, onBackPressed: () -> Unit) {
    BackHandler(true) {
        onBackPressed()
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var playerState by remember { mutableStateOf<PlayerState>(PlayerState.INITIALIZING) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            volume = 0f
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    playerState = PlayerState.ERROR
                    errorMessage = "플레이어 오류: ${error.message}"
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_READY -> playerState = PlayerState.READY
                        Player.STATE_BUFFERING -> playerState = PlayerState.BUFFERING
                        Player.STATE_ENDED -> playerState = PlayerState.ENDED
                        Player.STATE_IDLE -> {}
                    }
                }
            })
        }
    }

    val mediaSource = remember {
        RtspMediaSource.Factory()
            .setForceUseRtpTcp(true)
            .setDebugLoggingEnabled(true)
            .createMediaSource(MediaItem.fromUri(Uri.parse(rtspUrl)))
    }

    LaunchedEffect(mediaSource) {
        playerState = PlayerState.INITIALIZING
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        scope.launch {
            delay(10000)
            if (playerState == PlayerState.INITIALIZING) {
                playerState = PlayerState.ERROR
                errorMessage = "연결 시간 초과. 네트워크를 확인해 주세요."
            }
        }
    }

    CamScreen(playerState, exoPlayer, errorMessage) {
        onBackPressed()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun CamScreen(playerState: PlayerState, exoPlayer: ExoPlayer?, errorMessage: String?, onBackPressed: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black))
    {
        when (playerState) {
            PlayerState.INITIALIZING, PlayerState.BUFFERING -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            PlayerState.READY -> {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                            useController = false
                            rotation = 180f
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            PlayerState.ERROR -> {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                        .background(Color.White)
                        .fillMaxWidth(0.6f)
                        .padding(30.dp)
                        .rotate(180f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("오류 발생", color = Color.Red)
                    Spacer(modifier = Modifier
                        .height(8.dp))
                    Text(errorMessage ?: "알 수 없는 오류", color = Color.Red)
                }
            }

            PlayerState.ENDED -> {
                Text("재생 종료",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .rotate(180f))
            }
        }

        IconButton(
            onClick = { onBackPressed() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 10.dp, end = 10.dp)
                .rotate(180f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "뒤로 가기",
                tint = Color.White
            )
        }

        Text(
            text = "1번 카메라",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .rotate(180f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WebCamPreview() {
    CamScreen(playerState = PlayerState.ERROR, exoPlayer = null, errorMessage = "dkdkdk") {

    }
}
