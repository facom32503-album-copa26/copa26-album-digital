package com.example.copa26_album_digital.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.copa26_album_digital.ui.theme.AlbumSurface
import com.example.copa26_album_digital.ui.theme.AlbumYellow

/** Avatar circular de jogador ou técnico; sem foto, exibe as iniciais do nome. */
@Composable
fun PersonAvatar(
    photoUrl: String,
    name: String,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 56.dp
) {
    SubcomposeAsyncImage(
        model = photoUrl,
        contentDescription = name,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(avatarSize)
            .clip(CircleShape)
            .background(AlbumSurface)
            .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape),
        loading = { PersonInitials(name = name, avatarSize = avatarSize) },
        error = { PersonInitials(name = name, avatarSize = avatarSize) },
    )
}

@Composable
private fun PersonInitials(
    name: String,
    avatarSize: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(avatarSize),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initialsOf(name),
            color = AlbumYellow.copy(alpha = 0.7f),
            fontSize = (avatarSize.value * 0.34f).sp,
            fontWeight = FontWeight.Black
        )
    }
}

private fun initialsOf(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts.first().take(1).uppercase()
        else -> "${parts.first().take(1)}${parts.last().take(1)}".uppercase()
    }
}
