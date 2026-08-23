package database

import cats.effect
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IOApp, Resource}
import org.typelevel.otel4s.trace.Tracer.Implicits.noop
import org.typelevel.otel4s.trace.Tracer
import org.typelevel.otel4s.metrics.Meter.Implicits.noop
import skunk.{Query, Session, Void}
import skunk.codec.all.{int4, varchar}
import skunk.implicits.sql

object Test extends IOApp {
  private case class Round (id: Int, name: String)

  private val session: Resource[effect.IO, Session[effect.IO]] = Session.single(
    host = "127.0.0.1",
    port = 5432,
    user = "postgres",
    database = "universitychallenge",
    password = Some("postgres")
  )

  private val queryList: Query[Void, Round] = sql"SELECT id, name FROM round ORDER BY id".query(int4 *: varchar(15)).to[Round]

  private val queryById: Query[Int, Round] = sql"SELECT id, name FROM round WHERE id = $int4".query(int4 *: varchar(15)).to[Round]

  override def run(args: List[String]): effect.IO[ExitCode] =
    session.use { ses =>
      runQuery(ses).unsafeRunSync().foreach { r => println(s"${r.id} - ${r.name}") }

      Range(1, 7).foreach(getNameById(ses, _).unsafeRunSync().foreach(println))

      effect.IO(ExitCode.Success)
    }

  private def runQuery(ses: Session[effect.IO]): effect.IO[List[Round]] =
    ses.execute(queryList)

  private def getNameById(ses: Session[effect.IO], id: Int): effect.IO[Option[String]] =
    ses.prepare(queryById).flatMap(_.option(id).map(_.map(_.name)))
}
