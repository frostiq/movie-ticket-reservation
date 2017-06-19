package me.bazhanau.ticketreservation.dao

import me.bazhanau.ticketreservation.model.db.Movie
import me.bazhanau.ticketreservation.model.db.MovieId
import me.bazhanau.ticketreservation.service.DuplicateMovieException
import me.bazhanau.ticketreservation.util.DbTest
import me.bazhanau.ticketreservation.util.MongoSpec
import org.scalatest._

import scala.concurrent.Future
import scala.util.Random

class MongoMovieDaoSpec extends MongoSpec{

  "MongoDao" should "find movie after creation" taggedAs DbTest in newMovie { movie =>
    dao.findOne(movie._id).map(_ shouldBe Some(movie))
  }

  it should "return None for non-existing movies" taggedAs DbTest in {
    dao.findOne(MovieId(Random.nextString(5), "x")).map(_ shouldBe None)
  }

  it should "not allow to insert duplicate movie" taggedAs DbTest in newMovie { movie =>
    recoverToSucceededIf[DuplicateMovieException]{
      dao.insert(movie)
    }
  }

  it should "decrease available seats after reservation" taggedAs DbTest in newMovie { movie =>
    dao.reserveSeat(movie._id)
      .map(res => {
        res shouldBe defined
        res.get.availableSeats shouldBe movie.availableSeats - 1
        res.get.reservedSeats shouldBe movie.reservedSeats + 1
      })
  }

  it should "not allow reserve fully-reserved movie" taggedAs DbTest in newMovie { movie =>
    Future.sequence((1 to movie.availableSeats).map(_ => dao.reserveSeat(movie._id)))
      .flatMap(_ => dao.findOne(movie._id))
      .map(res => {
        res shouldBe defined
        res.get.availableSeats shouldBe 0
        res.get.reservedSeats shouldBe movie.availableSeats
      })
      .flatMap(_ => dao.reserveSeat(movie._id))
      .map(_ shouldBe None)
  }

  def newMovie(testCode: Movie => Future[Assertion]) = {
    val movie = Movie(
      MovieId(Random.nextString(5), "screen_123456"),
      100,
      0,
      "Movie Title"
    )
    dao.insert(movie).flatMap(testCode)
  }

  var dao: MovieDao = _

  override protected def beforeAll(): Unit = {
    try super.beforeAll()
    finally dao = new MongoMovieDao(db)
  }
}
