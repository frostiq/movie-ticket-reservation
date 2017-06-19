package me.bazhanau.ticketreservation.service

import akka.actor.Status.Failure
import akka.testkit.ImplicitSender
import me.bazhanau.ticketreservation.dao.MovieDao
import me.bazhanau.ticketreservation.dao.MovieTitleDao
import me.bazhanau.ticketreservation.model._
import me.bazhanau.ticketreservation.service.MovieServiceActor.FindOne
import me.bazhanau.ticketreservation.service.MovieServiceActor.Register
import me.bazhanau.ticketreservation.service.MovieServiceActor.Reserve
import me.bazhanau.ticketreservation.util.ActorSpec
import org.scalamock.function.FunctionAdapter1
import org.scalatest.FlatSpecLike

import scala.concurrent.Future
import scala.util.Random

class MovieServiceActorSpec extends ActorSpec with FlatSpecLike with ImplicitSender  {

  "MovieServiceActor.findOne" should "return Movie, if it exists" in new Test{

    movieServiceActor ! FindOne(id.imdbId, id.screenId)

    val expectedMovie = Movie(
      id.imdbId,
      id.screenId,
      movie.availableSeats,
      movie.reservedSeats,
      movie.movieTitle
    )
    expectMsg(Some(expectedMovie))
  }

  it should "return None, if Movie not exists" in new Test{

    movieServiceActor ! FindOne(id.imdbId, Random.nextString(5))

    expectMsg(None)
  }

  "MovieServiceActor.register" should "register new Movie" in new Test {

    val registration = MovieRegistration(id2.imdbId, id2.screenId, 1)

    movieServiceActor ! Register(registration)

    expectMsg(Movie(
      registration.imdbId,
      registration.screenId,
      registration.availableSeats,
      0,
      "new title"
    ))
  }

  it should "throw return Failure if movie already exists" in new Test {
    movieServiceActor ! Register(MovieRegistration(id.imdbId, id.screenId, 1))

    expectMsgClass(classOf[Failure])
  }

  "MovieServiceActor.reserve" should "reserve seat for the movie" in new Test {
    movieServiceActor ! Reserve(MovieReservation(id.imdbId, id.screenId))

    expectMsg(Some(movie.copy(availableSeats = 0, reservedSeats = 1).toViewModel))
  }

  "MovieServiceActor.reserve" should "return None if movie not exists" in new Test {
    movieServiceActor ! Reserve(MovieReservation(id2.imdbId, id2.screenId))

    expectMsg(None)
  }

  trait Test {
    val id = db.MovieId("id1", "s1")
    val id2 = db.MovieId("id2", "s2")
    val movie = db.Movie(id, 1, 1, "title")
    val movieDao = stub[MovieDao]
    val movieTitleDao = stub[MovieTitleDao]
    (movieDao.findOne _).when(id).returns(Future.successful(Some(movie)))
    (movieDao.findOne _).when(*).returns(Future.successful(None))
    (movieDao.insert(_: db.Movie))
      .when(new FunctionAdapter1((m: db.Movie) => m._id == id))
      .throwing(DuplicateMovieException(new RuntimeException))
    (movieDao.insert(_: db.Movie)).when(*).onCall((x: db.Movie) => Future.successful(x))
    (movieDao.reserveSeat _).when(id)
      .returns(Future.successful(Some(movie.copy(availableSeats = 0, reservedSeats = 1))))
    (movieDao.reserveSeat _).when(*).returns(Future.successful(None))
    (movieTitleDao.find _).when(*).returns(Future.successful(Some("new title")))
    val movieServiceActor = system.actorOf(MovieServiceActor.props(movieDao, movieTitleDao))
  }

}
