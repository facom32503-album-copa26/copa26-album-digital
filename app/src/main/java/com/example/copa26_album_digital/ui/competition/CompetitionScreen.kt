package com.example.copa26_album_digital.ui.competition

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.copa26_album_digital.ui.theme.AlbumBackground
import com.example.copa26_album_digital.ui.theme.AlbumYellow
import com.example.copa26_album_digital.ui.theme.Copa26albumdigitalTheme

/**
 * Tela inicial do álbum: capa da Copa do Mundo 2026 e ação para ver as equipes.
 * View "burra": apenas exibe textos e emite o evento [onSeeTeams].
 */
@Composable
fun CompetitionScreen(
    onSeeTeams: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AlbumBackground)
    ) {
        // Etiqueta superior "Álbum Oficial"
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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.fifa_label),
                color = AlbumYellow.copy(alpha = 0.6f),
                fontSize = 11.sp,
                letterSpacing = 6.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "${stringResource(R.string.competition_title_line1)}\n${stringResource(R.string.competition_title_line2)}",
                color = Color.White,
                fontSize = 60.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 58.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(AlbumYellow.copy(alpha = 0.3f))
                )
                Text(
                    text = stringResource(R.string.competition_year),
                    color = AlbumYellow,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(AlbumYellow.copy(alpha = 0.3f))
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.competition_hosts),
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                letterSpacing = 3.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.competition_summary),
                color = Color.White.copy(alpha = 0.25f),
                fontSize = 10.sp,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AlbumYellow)
                    .clickable(onClick = onSeeTeams)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.action_see_teams),
                    color = AlbumBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Capa do álbum")
@Composable
private fun CompetitionPreview() {
    Copa26albumdigitalTheme {
        CompetitionScreen(onSeeTeams = {})
    }
}
