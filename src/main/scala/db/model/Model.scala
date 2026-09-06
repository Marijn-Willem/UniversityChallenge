package db.model

import java.time.LocalDateTime

object Model {
  trait IdEntity { val id: Int }
  case class City(id: Int, name: Option[String]) extends IdEntity
  case class Season(id: Int, name: Option[String]) extends IdEntity
  case class Round(id: Int, name: Option[String]) extends IdEntity
  case class Episode(id: Int, roundId: Int) extends IdEntity
  case class Team(id: Int, name: Option[String], cityId: Int) extends IdEntity
  case class SeasonTeam(seasonId: Int, teamId: Int)
  case class SeasonEpisode(seasonId: Int, episodeId: Int, team1Id: Option[Int], team2Id: Option[Int], score1: Option[Int], score2: Option[Int], date: Option[LocalDateTime])
}
