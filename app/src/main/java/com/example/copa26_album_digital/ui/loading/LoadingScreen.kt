package com.example.copa26_album_digital.ui.loading

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.copa26_album_digital.R
import com.example.copa26_album_digital.ui.components.CrestCbf
import com.example.copa26_album_digital.ui.theme.AlbumBackground
import com.example.copa26_album_digital.ui.theme.AlbumYellow
import kotlinx.coroutines.delay

/**
 * Tela de abertura com progresso animado. Ao concluir, dispara [onDone] para a
 * navegação seguir para a competição.
 */
@Composable
fun LoadingScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        stringResource(R.string.loading_step_init),
        stringResource(R.string.loading_step_stickers),
        stringResource(R.string.loading_step_team),
        stringResource(R.string.loading_step_almost)
    )
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 200),
        label = "loadingProgress"
    )

    LaunchedEffect(Unit) {
        val totalSteps = 40
        repeat(totalSteps) { index ->
            delay(65)
            progress = (index + 1) / totalSteps.toFloat()
        }
        delay(300)
        onDone()
    }

    val stepIndex = (progress * steps.size).toInt().coerceIn(0, steps.size - 1)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AlbumBackground),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(AlbumYellow)
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.album_label).uppercase(),
                color = AlbumBackground,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CrestCbf(crestSize = 108.dp)
            Spacer(Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.fifa_label),
                color = AlbumYellow.copy(alpha = 0.5f),
                fontSize = 11.sp,
                letterSpacing = 6.sp
            )
            Text(
                text = "${stringResource(R.string.competition_title_line1)} ${stringResource(R.string.competition_title_line2)}",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = stringResource(R.string.competition_year),
                color = AlbumYellow,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(AlbumYellow)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = steps[stepIndex],
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                color = AlbumYellow.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
        }
    }
}
