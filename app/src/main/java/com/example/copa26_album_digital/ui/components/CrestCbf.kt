package com.example.copa26_album_digital.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.copa26_album_digital.R
import com.example.copa26_album_digital.ui.theme.AlbumYellow
import com.example.copa26_album_digital.ui.theme.BrazilBlue
import com.example.copa26_album_digital.ui.theme.BrazilGreen
import androidx.compose.material3.Text

/**
 * Escudo estilizado da CBF desenhado com [Canvas]: círculo verde, losango
 * amarelo, disco azul central e a sigla "CBF". Imagem informativa, portanto
 * expõe `contentDescription`.
 */
@Composable
fun CrestCbf(modifier: Modifier = Modifier, crestSize: Dp = 80.dp) {
    val description = stringResource(R.string.cd_crest_cbf)
    Box(
        modifier = modifier
            .size(crestSize)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val side = minOf(size.width, size.height)
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = side / 2f

            drawCircle(color = BrazilGreen, radius = radius, center = center)

            val diamond = radius * 0.84f
            val path = Path().apply {
                moveTo(center.x, center.y - diamond)
                lineTo(center.x + diamond, center.y)
                lineTo(center.x, center.y + diamond)
                lineTo(center.x - diamond, center.y)
                close()
            }
            drawPath(path, color = AlbumYellow)

            drawCircle(color = BrazilBlue, radius = radius * 0.46f, center = center)
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = radius * 0.02f,
                center = Offset(center.x, center.y - radius * 0.18f)
            )
        }
        Text(
            text = "CBF",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = (crestSize.value * 0.15f).sp
        )
    }
}
