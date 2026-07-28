package com.example.copa26_album_digital.ui.person

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.copa26_album_digital.R
import com.example.copa26_album_digital.domain.model.Coach
import com.example.copa26_album_digital.domain.model.Player
import com.example.copa26_album_digital.ui.components.ErrorState
import com.example.copa26_album_digital.ui.components.LoadingState
import com.example.copa26_album_digital.ui.components.PersonAvatar
import com.example.copa26_album_digital.ui.components.StatBar
import com.example.copa26_album_digital.ui.preview.PreviewData
import com.example.copa26_album_digital.ui.theme.AlbumBackground
import com.example.copa26_album_digital.ui.theme.AlbumSurface
import com.example.copa26_album_digital.ui.theme.AlbumYellow
import com.example.copa26_album_digital.ui.theme.Copa26albumdigitalTheme
import com.example.copa26_album_digital.ui.theme.StatBlue
import com.example.copa26_album_digital.ui.theme.StatGreen
import com.example.copa26_album_digital.ui.theme.StatRed

/**
 * Detalhe de jogador OU técnico. View "burra": exibe o estado recebido do
 * ViewModel (carregando / erro / jogador / técnico).
 */
@Composable
fun PersonDetailScreen(
    isLoading: Boolean,
    player: Player?,
    coach: Coach?,
    errorMessage: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> LoadingState(modifier = modifier)
        errorMessage != null -> ErrorState(message = errorMessage, modifier = modifier)
        player != null -> PlayerDetail(player = player, onBack = onBack, modifier = modifier)
        coach != null -> CoachDetail(coach = coach, onBack = onBack, modifier = modifier)
        else -> ErrorState(message = stringResource(R.string.person_not_found), modifier = modifier)
    }
}

@Composable
private fun PlayerDetail(
    player: Player,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AlbumBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        BackButton(onBack)
        Spacer(Modifier.size(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.BottomCenter) {
                PersonAvatar(
                    photoUrl = player.photoUrl,
                    name = player.name,
                    avatarSize = 120.dp
                )
                if (player.shirtNumber > 0) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AlbumYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.shirtNumber.toString(),
                            color = AlbumBackground,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
        Spacer(Modifier.size(16.dp))
        Text(
            text = player.name,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(6.dp))
        Text(
            text = listOfNotNull(
                player.position.takeIf { it.isNotBlank() },
                player.nationality.takeIf { it.isNotBlank() }
            ).joinToString(" · "),
            color = AlbumYellow.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.size(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCell(stringResource(R.string.person_stat_games), player.stats.games.toString(), Modifier.weight(1f))
            StatCell(stringResource(R.string.person_stat_goals), player.stats.goals.toString(), Modifier.weight(1f))
            StatCell(stringResource(R.string.person_stat_assists), player.stats.assists.toString(), Modifier.weight(1f))
        }

        Spacer(Modifier.size(24.dp))
        Text(
            text = stringResource(R.string.person_section_stats).uppercase(),
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 10.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        val maxStat = maxOf(player.stats.games, player.stats.goals, player.stats.assists, 1)
        StatBar(
            label = stringResource(R.string.person_stat_games),
            valueText = player.stats.games.toString(),
            fraction = player.stats.games.toFloat() / maxStat,
            barColor = StatBlue
        )
        StatBar(
            label = stringResource(R.string.person_stat_goals),
            valueText = player.stats.goals.toString(),
            fraction = player.stats.goals.toFloat() / maxStat,
            barColor = StatGreen
        )
        StatBar(
            label = stringResource(R.string.person_stat_assists),
            valueText = player.stats.assists.toString(),
            fraction = player.stats.assists.toFloat() / maxStat,
            barColor = StatRed
        )
    }
}

@Composable
private fun CoachDetail(
    coach: Coach,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AlbumBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        BackButton(onBack)
        Spacer(Modifier.size(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PersonAvatar(
                photoUrl = coach.photoUrl,
                name = coach.name,
                avatarSize = 120.dp
            )
        }
        Spacer(Modifier.size(16.dp))
        Text(
            text = coach.name,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(6.dp))
        Text(
            text = "${stringResource(R.string.role_coach)} · ${coach.nationality}",
            color = AlbumYellow.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.size(24.dp))
        Text(
            text = stringResource(R.string.person_section_profile).uppercase(),
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 10.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = stringResource(R.string.coach_profile),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
    }
}

@Composable
private fun BackButton(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        contentDescription = stringResource(R.string.cd_back),
        tint = Color.White.copy(alpha = 0.6f),
        modifier = modifier
            .size(28.dp)
            .clickable(onClick = onBack)
    )
}

@Composable
private fun StatCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AlbumSurface)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = AlbumYellow,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 9.sp,
            letterSpacing = 1.sp
        )
    }
}

@Preview(showBackground = true, name = "Detalhe do jogador")
@Composable
private fun PlayerDetailPreview() {
    Copa26albumdigitalTheme {
        PersonDetailScreen(
            isLoading = false,
            player = PreviewData.player,
            coach = null,
            errorMessage = null,
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Detalhe do técnico")
@Composable
private fun CoachDetailPreview() {
    Copa26albumdigitalTheme {
        PersonDetailScreen(
            isLoading = false,
            player = null,
            coach = PreviewData.coach,
            errorMessage = null,
            onBack = {}
        )
    }
}
