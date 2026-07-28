package com.example.copa26_album_digital.data.mapper

import com.example.copa26_album_digital.data.remote.dto.PersonDto
import com.example.copa26_album_digital.data.remote.dto.TeamDto
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testes funcionais da camada de mapeamento (DTO → entidade → domínio).
 *
 * Focam em duas regras de negócio próprias do app:
 *  - títulos reais de Copa do Mundo vindos de um mapa fixo (não da API);
 *  - enriquecimento de estatísticas do jogador (jogos/gols/assistências).
 */
class TeamMapperTest {

    private fun teamDto(id: Int, name: String, clubColors: String? = null) = TeamDto(
        id = id,
        name = name,
        shortName = name,
        crest = null,
        clubColors = clubColors,
        founded = 1900,
        venue = null,
        coach = null,
        squad = null,
    )

    private fun personDto(id: Int, name: String) = PersonDto(
        id = id,
        name = name,
        position = "Offence",
        dateOfBirth = null,
        nationality = "Argentina",
        shirtNumber = 10,
    )

    @Test
    fun `titulos vem do mapa fixo por id da selecao`() {
        // Brasil (764) = 5, França (773) = 2 — dados históricos reais.
        assertEquals(5, teamDto(764, "Brazil").toEntity(competitionId = 2000).victories)
        assertEquals(2, teamDto(773, "France").toEntity(competitionId = 2000).victories)
        assertEquals(3, teamDto(762, "Argentina").toEntity(competitionId = 2000).victories)
    }

    @Test
    fun `selecao sem titulo mapeado conta zero`() {
        // Croácia (799) participa da Copa, mas não tem títulos mundiais.
        assertEquals(0, teamDto(799, "Croatia").toEntity(competitionId = 2000).victories)
    }

    @Test
    fun `cores oficiais sao quebradas a partir do texto livre`() {
        val entity = teamDto(762, "Argentina", clubColors = "Sky Blue / White").toEntity(2000)
        val domain = entity.toDomain(players = emptyList(), coach = null)
        assertEquals(listOf("Sky Blue", "White"), domain.colors)
    }

    @Test
    fun `jogador recebe estatisticas quando informadas`() {
        val entity = personDto(3218, "Lionel Messi").toPlayerEntity(
            teamId = 762,
            games = 8,
            goals = 8,
            assists = 4,
        )
        assertEquals(8, entity.games)
        assertEquals(8, entity.goals)
        assertEquals(4, entity.assists)
    }

    @Test
    fun `jogador sem estatisticas fica zerado por padrao`() {
        val entity = personDto(6, "Géronimo Rulli").toPlayerEntity(teamId = 762)
        assertEquals(0, entity.games)
        assertEquals(0, entity.goals)
        assertEquals(0, entity.assists)
    }
}
