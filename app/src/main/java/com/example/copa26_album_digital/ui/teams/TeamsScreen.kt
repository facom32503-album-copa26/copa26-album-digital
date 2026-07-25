package com.example.copa26_album_digital.ui.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import com.example.copa26_album_digital.domain.model.Team
import com.example.copa26_album_digital.ui.components.CrestImage
import com.example.copa26_album_digital.ui.components.ErrorState
import com.example.copa26_album_digital.ui.components.LoadingState
import com.example.copa26_album_digital.ui.theme.AlbumBackground
import com.example.copa26_album_digital.ui.theme.AlbumSurface
import com.example.copa26_album_digital.ui.theme.AlbumYellow

/**
 * Grade de equipes participantes da competição. View "burra": renderiza o estado
 * (carregando / erro / lista) recebido do ViewModel e emite eventos de navegação.
 */
@Composable
fun TeamsScreen(
    isLoading: Boolean,
    teams: List<Team>,
    errorMessage: String?,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onSelectTeam: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> LoadingState(modifier = modifier)
        errorMessage != null -> ErrorState(
            message = errorMessage,
            onRetry = onRetry,
            modifier = modifier,
        )
        else -> TeamsContent(
            teams = teams,
            onBack = onBack,
            onSelectTeam = onSelectTeam,
            modifier = modifier,
        )
    }
}

@Composable
private fun TeamsContent(
    teams: List<Team>,
    onBack: () -> Unit,
    onSelectTeam: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AlbumBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.cd_back),
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(28.dp)
                    .clickable(onClick = onBack)
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${stringResource(R.string.fifa_label)} ${stringResource(R.string.competition_year)}",
                    color = AlbumYellow.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    letterSpacing = 3.sp
                )
                Text(
                    text = stringResource(R.string.teams_title),
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AlbumYellow.copy(alpha = 0.1f))
                    .border(1.dp, AlbumYellow.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.format_teams_count, teams.size),
                    color = AlbumYellow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = stringResource(R.string.teams_section_all).uppercase(),
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 10.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(teams, key = { it.id }) { team ->
                TeamCard(
                    team = team,
                    onClick = { onSelectTeam(team.id) }
                )
            }
        }
    }
}

@Composable
private fun TeamCard(
    team: Team,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AlbumSurface)
            .border(1.dp, AlbumYellow.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            CrestImage(
                url = team.crestUrl,
                contentDescription = stringResource(R.string.cd_team_crest, team.name),
                boxSize = 56.dp,
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = team.name.uppercase(),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
            if (team.victories > 0) {
                Spacer(Modifier.size(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(minOf(team.victories, 5)) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = AlbumYellow,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = stringResource(R.string.format_titles, team.victories),
                        color = AlbumYellow.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
