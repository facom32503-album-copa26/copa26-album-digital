package com.example.copa26_album_digital.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.copa26_album_digital.R
import com.example.copa26_album_digital.ui.theme.AlbumBackground
import com.example.copa26_album_digital.ui.theme.AlbumYellow

/**
 * Escudo/emblema remoto exibido num quadro arredondado. Carregado com Coil a
 * partir de uma URL (suporta PNG e SVG). Imagem informativa: recebe
 * `contentDescription`.
 */
@Composable
fun CrestImage(
    url: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    boxSize: Dp = 56.dp,
) {
    Box(
        modifier = modifier
            .size(boxSize)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/** Estado de carregamento em tela cheia (fundo do álbum + indicador amarelo). */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AlbumBackground),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = AlbumYellow)
    }
}

/**
 * Estado de erro em tela cheia com mensagem e ação opcional de "tentar novamente".
 */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AlbumBackground)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = message,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )
            if (onRetry != null) {
                Spacer(Modifier.size(20.dp))
                Text(
                    text = stringResource(R.string.action_retry),
                    color = AlbumBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AlbumYellow)
                        .clickable(onClick = onRetry)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                )
            }
        }
    }
}
