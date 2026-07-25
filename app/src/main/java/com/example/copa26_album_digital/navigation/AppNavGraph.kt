package com.example.copa26_album_digital.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.copa26_album_digital.data.ServiceLocator
import com.example.copa26_album_digital.ui.competition.CompetitionScreen
import com.example.copa26_album_digital.ui.loading.LoadingScreen
import com.example.copa26_album_digital.ui.person.PersonDetailScreen
import com.example.copa26_album_digital.ui.person.PersonViewModel
import com.example.copa26_album_digital.ui.team.TeamDetailScreen
import com.example.copa26_album_digital.ui.team.TeamViewModel
import com.example.copa26_album_digital.ui.teams.TeamsScreen
import com.example.copa26_album_digital.ui.teams.TeamsViewModel

private const val LOADING_ROUTE = "loading"

/**
 * Grafo de navegação do app. O `NavController` centraliza as transições e o
 * tratamento dos botões "Up/Back"; cada destino coleta seu estado do ViewModel
 * e repassa para a tela (View "burra").
 */
@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = remember { ServiceLocator.provideRepository(context) }

    NavHost(
        navController = navController,
        startDestination = LOADING_ROUTE,
        modifier = modifier
    ) {
        composable(LOADING_ROUTE) {
            LoadingScreen(
                onDone = {
                    navController.navigate(AppDestinations.COMPETITION_ROUTE) {
                        popUpTo(LOADING_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.COMPETITION_ROUTE) {
            CompetitionScreen(
                onSeeTeams = { navController.navigate(AppDestinations.TEAMS_ROUTE) }
            )
        }

        composable(AppDestinations.TEAMS_ROUTE) {
            val viewModel: TeamsViewModel = viewModel(
                factory = TeamsViewModel.provideFactory(repository)
            )
            val uiState by viewModel.uiState.collectAsState()
            TeamsScreen(
                isLoading = uiState.isLoading,
                teams = uiState.teams,
                errorMessage = uiState.errorMessage,
                onRetry = viewModel::load,
                onBack = { navController.popBackStack() },
                onSelectTeam = { teamId ->
                    navController.navigate(AppDestinations.team(teamId))
                }
            )
        }

        composable(
            route = AppDestinations.TEAM_ROUTE,
            arguments = listOf(
                navArgument(AppDestinations.ARG_TEAM_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt(AppDestinations.ARG_TEAM_ID) ?: 0
            val viewModel: TeamViewModel = viewModel(
                factory = TeamViewModel.provideFactory(repository, teamId)
            )
            val uiState by viewModel.uiState.collectAsState()
            TeamDetailScreen(
                isLoading = uiState.isLoading,
                team = uiState.team,
                errorMessage = uiState.errorMessage,
                onRetry = viewModel::load,
                onBack = { navController.popBackStack() },
                onSelectPerson = { personId ->
                    navController.navigate(AppDestinations.person(teamId, personId))
                }
            )
        }

        composable(
            route = AppDestinations.PERSON_ROUTE,
            arguments = listOf(
                navArgument(AppDestinations.ARG_TEAM_ID) { type = NavType.IntType },
                navArgument(AppDestinations.ARG_PERSON_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getInt(AppDestinations.ARG_TEAM_ID) ?: 0
            val personId = backStackEntry.arguments?.getInt(AppDestinations.ARG_PERSON_ID) ?: 0
            val viewModel: PersonViewModel = viewModel(
                factory = PersonViewModel.provideFactory(repository, teamId, personId)
            )
            val uiState by viewModel.uiState.collectAsState()
            PersonDetailScreen(
                isLoading = uiState.isLoading,
                player = uiState.player,
                coach = uiState.coach,
                errorMessage = uiState.errorMessage,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
