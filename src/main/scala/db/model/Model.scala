package db.model

import java.time.LocalDate

object Model {
  trait IdEntity { val id: Int }
  trait NamedIdEntity extends IdEntity { val name: Option[String] }
  case class City(id: Int, name: Option[String]) extends NamedIdEntity
  case class Season(id: Int, name: Option[String]) extends NamedIdEntity
  case class Episode(id: Int, name: Option[String], roundId: Int) extends NamedIdEntity
  case class Team(id: Int, name: Option[String], cityId: Int) extends NamedIdEntity
  case class SeasonTeam(seasonId: Int, teamId: Int)
  case class SeasonEpisode(seasonId: Int, episodeId: Int, team1Id: Option[Int], team2Id: Option[Int], 
                           score1: Option[Int], score2: Option[Int], date: Option[LocalDate],
                           roundId: Option[Int] = None, winningTeamId: Option[Int] = None,
                           losingTeamId: Option[Int] = None, losingScore: Option[Int] = None)
  
  object Round {
    val roundIdFirstRound = -1
    val roundIdRepechage = -2
    private val roundIdSecondRound = -3
    val roundIdQuarterFinal = -4
    private val roundIdSemiFinal = -5
    private val roundIdFinal = -6
    val roundIdNameMap: Map[Int, String] = Map(
      roundIdFirstRound -> "First round",
      roundIdRepechage -> "Repechage",
      roundIdSecondRound -> "Second round",
      roundIdQuarterFinal -> "Quarter final",
      roundIdSemiFinal -> "Semi final",
      roundIdFinal -> "Final"
    )
  }
}
