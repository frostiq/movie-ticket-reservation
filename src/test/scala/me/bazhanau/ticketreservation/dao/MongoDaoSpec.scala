package me.bazhanau.ticketreservation.dao

import me.bazhanau.ticketreservation.model.db.Movie
import me.bazhanau.ticketreservation.model.db.MovieId
import me.bazhanau.ticketreservation.service.DuplicateMovieException
import org.mongodb.scala._
import org.scalatest.Tag
import org.scalatest._

import scala.concurrent.Future
import scala.util.Random

object MongoDbTest extends Tag("MongoDB test")

class MongoDaoSpec extends AsyncFlatSpec with Matchers with BeforeAndAfterAll{

  val client = MongoClient()
  val dao: MovieDao = new MongoMovieDao(client.getDatabase("test"))

  "MongoDao" should "find movie after creation" taggedAs MongoDbTest in newMovie { movie =>
    dao.findOne(movie._id).map(_ shouldBe Some(movie))
  }

  it should "return None for non-existing movies" taggedAs MongoDbTest in {
    dao.findOne(MovieId("x", "x")).map(_ shouldBe None)
  }

  it should "not allow to insert duplicate movie" taggedAs MongoDbTest in newMovie { movie =>
    recoverToSucceededIf[DuplicateMovieException]{
      dao.insert(movie)
    }
  }

  it should "decrease available seats after reservation" taggedAs MongoDbTest in newMovie { movie =>
    dao.reserveSeat(movie._id)
      .map(res => {
        res shouldBe defined
        res.get.availableSeats shouldBe movie.availableSeats - 1
        res.get.reservedSeats shouldBe movie.reservedSeats + 1
      })
  }

  it should "not allow reserve fully-reserved movie" taggedAs MongoDbTest in newMovie { movie =>
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
