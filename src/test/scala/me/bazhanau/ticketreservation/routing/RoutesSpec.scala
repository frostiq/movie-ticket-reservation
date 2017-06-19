package me.bazhanau.ticketreservation.routing

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Status.Failure
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.ValidationRejection
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.testkit.TestActorRef
import me.bazhanau.ticketreservation.model.Movie
import me.bazhanau.ticketreservation.model.MovieRegistration
import me.bazhanau.ticketreservation.model.MovieReservation
import me.bazhanau.ticketreservation.service.DuplicateMovieException
import me.bazhanau.ticketreservation.service.MovieServiceActor.FindOne
import me.bazhanau.ticketreservation.service.MovieServiceActor.Register
import me.bazhanau.ticketreservation.service.MovieServiceActor.Reserve
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.util.Random

class RoutesSpec extends FlatSpec
  with ScalatestRouteTest
  with Matchers
  with MockFactory
  with Routes {

  "Routes" should "return Movie if it exists" in {
    Get("/movies/id1/s1") ~> routes ~> check {
      responseAs[Movie] shouldBe movie
    }
  }

  it should "return NotFound if movie not exists" in {
    Get("/movies/id99/s1") ~> routes ~> check {
      response.status shouldBe StatusCodes.NotFound
    }
  }

  it should "register movie successfully" in {
    Post("/movies", registration) ~> routes ~> check {
      responseAs[Movie] shouldBe movie
    }
  }

  it should "reject movie with availableSeats > 1000" in {
    Post("/movies", registration.copy(availableSeats = 1001)) ~> routes ~> check {
      rejection shouldBe ValidationRejection("availableSeats must be from 1 to 1000")
    }
  }

  it should "reject movie with long imdbId" in {
    Post("/movies", registration.copy(imdbId = Random.nextString(31))) ~> routes ~> check {
      rejection shouldBe ValidationRejection("imdbId is too long")
    }
  }

  it should "reject movie with long screenId" in {
    Post("/movies", registration.copy(screenId = Random.nextString(31))) ~> routes ~> check {
      rejection shouldBe ValidationRejection("screenId is too long")
    }
  }

  it should "respond with BadRequest in case of invalid registration" in {
    Post("/movies", invalidRegistration) ~> routes ~> check {
      response.status shouldBe StatusCodes.BadRequest
    }
  }

  it should "reserve seat for movie successfully" in {
    Post("/reservations", reservation) ~> routes ~> check {
      responseAs[Movie] shouldBe movie
    }
  }

  it should "respopnd with BadRequest in case of invalid reservation" in {
    Post("/reservations", invalidReservation) ~> routes ~> check {
      response.status shouldBe StatusCodes.BadRequest
    }
  }

  val probe = TestActorRef(new Actor {
    def receive: Receive = {
      case FindOne("id1", "s1") =>
        sender ! Some(movie)
      case FindOne(_, _) =>
        sender ! None
      case Register(`registration`) =>
        sender ! movie
      case Register(`invalidRegistration`) =>
        sender ! Failure(DuplicateMovieException(new RuntimeException))
      case Reserve(`reservation`) =>
        sender ! Some(movie)
      case Reserve(`invalidReservation`) =>
        sender ! None
    }
  })

  override protected val movieService: ActorRef = probe
  val movie = Movie("id1", "s1", 1, 1, "title")
  val registration = MovieRegistration("id1", "s1", 1)
  val invalidRegistration = MovieRegistration("id2", "s2", 1)
  val reservation = MovieReservation("id1", "s1")
  val invalidReservation = MovieReservation("id2", "s2")
}
