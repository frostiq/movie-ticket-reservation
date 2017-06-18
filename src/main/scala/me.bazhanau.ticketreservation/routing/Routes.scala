package me.bazhanau.ticketreservation.routing

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import me.bazhanau.ticketreservation.conversion.JsonProtocol
import me.bazhanau.ticketreservation.model.Movie
import me.bazhanau.ticketreservation.model.MovieRegistration
import me.bazhanau.ticketreservation.model.MovieReservation
import me.bazhanau.ticketreservation.service.DuplicateMovieException
import me.bazhanau.ticketreservation.service.MovieServiceActor.FindOne
import me.bazhanau.ticketreservation.service.MovieServiceActor.Register
import me.bazhanau.ticketreservation.service.MovieServiceActor.Reserve

import scala.concurrent.Future
import scala.concurrent.duration._

trait Routes extends Directives with JsonProtocol {

  implicit val timeout: Timeout = 5.seconds
  protected val movieService: ActorRef

  val routes =
    pathPrefix("movies") {
      get {
        path(Segment / Segment) { (imdbId: String, screenId: String) =>
          val res = (movieService ? FindOne(imdbId, screenId)).mapTo[Option[Movie]]
          complete(res)
        }
      } ~
        post {
          entity(as[MovieRegistration]) { registration =>
            validate(
              1 to 1000 contains registration.availableSeats,
              "availableSeats must be from 1 to 1000"
            ){
              val res = (movieService ? Register(registration)).mapTo[Movie]
              onSuccess(res)(complete(_))
            }
          }
        }
    } ~
      path("reservations") {
        post {
          entity(as[MovieReservation]) { reservation =>
            val res = (movieService ? Reserve(reservation)).mapTo[Option[Movie]]
            complete(res)
          }
        }
      }

  def complete[T: ToResponseMarshaller](result: Future[Option[T]]): Route =
    onSuccess(result) {
      case Some(res) => complete(ToResponseMarshallable(res))
      case None => complete(HttpResponse(StatusCodes.NotFound))
    }

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case ex@DuplicateMovieException(_) =>
        complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(ex.getMessage)))
    }

}
