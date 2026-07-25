package com.example.copa26_album_digital.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Avatar circular de uma pessoa (jogador ou técnico). A foto vem de uma URL
 * remota (carregada com Coil). Imagem informativa, então recebe
 * `contentDescription` com o nome.
 */
@Composable
fun PersonAvatar(
    photoUrl: String,
    name: String,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 56.dp
) {
    AsyncImage(
        model = photoUrl,
        contentDescription = name,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(avatarSize)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.06f))
            .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape)
    )
}
