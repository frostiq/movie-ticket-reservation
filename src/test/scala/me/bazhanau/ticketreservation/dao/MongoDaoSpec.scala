package me.bazhanau.ticketreservation.dao

import me.bazhanau.ticketreservation.model.mongo.Movie
import me.bazhanau.ticketreservation.model.mongo.MovieId
import org.mongodb.scala._
import org.scalatest._

import scala.concurrent.Future
import scala.util.Random

class MongoDaoSpec extends AsyncFlatSpec with Matchers with BeforeAndAfterAll{

  val client = MongoClient()
  val dao: MovieDao = new MongoMovieDao(client.getDatabase("test"))

  "MongoDao" should "find movie after creation" in newMovie { movie =>
    dao.findOne(movie._id).map(_ shouldBe Some(movie))
  }

  it should "return None for non-existing movies" in {
    dao.findOne(MovieId("x", "x")).map(_ shouldBe None)
  }

  it should "not allow to insert duplicate movie" in newMovie { movie =>
    recoverToSucceededIf[DuplicateMovieException]{
      dao.insert(movie)
    }
  }

  it should "decrease available seats after reservation" in newMovie { movie =>
    dao.reserveSeat(movie._id)
      .map(res => {
        res shouldBe defined
        res.get.availableSeats shouldBe movie.availableSeats - 1
        res.get.reservedSeats shouldBe movie.reservedSeats + 1
      })
  }

  it should "not allow reserve fully-reserved movie" in newMovie { movie =>
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

  override protected def afterAll(): Unit = {
    try super.afterAll()
    finally client.close()
  }
}
