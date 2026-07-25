package com.example.copa26_album_digital.ui.team

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.copa26_album_digital.R
import com.example.copa26_album_digital.domain.model.Coach
import com.example.copa26_album_digital.domain.model.Player
import com.example.copa26_album_digital.domain.model.Team
import com.example.copa26_album_digital.ui.components.CrestImage
import com.example.copa26_album_digital.ui.components.ErrorState
import com.example.copa26_album_digital.ui.components.LoadingState
import com.example.copa26_album_digital.ui.components.PersonAvatar
import com.example.copa26_album_digital.ui.theme.AlbumBackground
import com.example.copa26_album_digital.ui.theme.AlbumSurface
import com.example.copa26_album_digital.ui.theme.AlbumYellow

/**
 * Detalhe da equipe: identidade visual, elenco (ordenado por número) e comissão
 * técnica. View "burra": renderiza o estado recebido do ViewModel e emite
 * [onSelectPerson] ao tocar em um jogador ou no técnico.
 */
@Composable
fun TeamDetailScreen(
    isLoading: Boolean,
    team: Team?,
    errorMessage: String?,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onSelectPerson: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> LoadingState(modifier = modifier)
        errorMessage != null -> ErrorState(message = errorMessage, onRetry = onRetry, modifier = modifier)
        team == null -> ErrorState(
            message = stringResource(R.string.team_not_found),
            onRetry = onRetry,
            modifier = modifier,
        )
        else -> TeamContent(
            team = team,
            onBack = onBack,
            onSelectPerson = onSelectPerson,
            modifier = modifier,
        )
    }
}

@Composable
private fun TeamContent(
    team: Team,
    onBack: () -> Unit,
    onSelectPerson: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AlbumBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 32.dp)
    ) {
        item {
            TeamHeader(team = team, onBack = onBack)
        }

        if (team.description.isNotBlank()) {
            item {
                Text(
                    text = team.description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        if (team.coach != null) {
            item {
                SectionTitle(stringResource(R.string.team_section_coach))
            }
            item {
                CoachRow(
                    coach = team.coach,
                    onClick = { onSelectPerson(team.coach.id) }
                )
            }
        }

        item {
            SectionTitle(stringResource(R.string.team_section_squad, team.players.size))
        }

        val rows = team.players.sortedBy { it.shirtNumber.takeIf { n -> n > 0 } ?: Int.MAX_VALUE }
            .chunked(3)
        items(rows.size) { rowIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rows[rowIndex].forEach { player ->
                    PlayerCard(
                        player = player,
                        onClick = { onSelectPerson(player.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - rows[rowIndex].size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TeamHeader(
    team: Team,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(R.string.cd_back),
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onBack)
        )
        Spacer(Modifier.size(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            CrestImage(
                url = team.crestUrl,
                contentDescription = stringResource(R.string.cd_team_crest, team.name),
                boxSize = 72.dp,
            )
            Spacer(Modifier.size(16.dp))
            Column {
                Text(
                    text = team.name.uppercase(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                if (team.victories > 0) {
                    Spacer(Modifier.size(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = AlbumYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = stringResource(R.string.format_titles, team.victories),
                            color = AlbumYellow.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
        if (team.colors.isNotEmpty()) {
            Spacer(Modifier.size(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                team.colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = color,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = Color.White.copy(alpha = 0.3f),
        fontSize = 10.sp,
        letterSpacing = 3.sp,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun CoachRow(
    coach: Coach,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AlbumSurface)
            .border(1.dp, AlbumYellow.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PersonAvatar(
            photoUrl = coach.photoUrl,
            name = coach.name,
            avatarSize = 48.dp
        )
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = coach.name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${stringResource(R.string.role_coach)} · ${coach.nationality}",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun PlayerCard(
    player: Player,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AlbumSurface)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            PersonAvatar(
                photoUrl = player.photoUrl,
                name = player.name,
                avatarSize = 56.dp
            )
            if (player.shirtNumber > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AlbumYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.shirtNumber.toString(),
                        color = AlbumBackground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
        Spacer(Modifier.size(8.dp))
        Text(
            text = player.name,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        if (player.position.isNotBlank()) {
            Text(
                text = player.position,
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
