package me.bazhanau.ticketreservation.routing

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.ExceptionHandler
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import me.bazhanau.ticketreservation.conversion.JsonProtocol
import me.bazhanau.ticketreservation.model.Movie
import me.bazhanau.ticketreservation.model.MovieRegistration
import me.bazhanau.ticketreservation.model.MovieReservation
import me.bazhanau.ticketreservation.service.DuplicateMovieException
import me.bazhanau.ticketreservation.service.MovieServiceActor.FindOne
import me.bazhanau.ticketreservation.service.MovieServiceActor.Register
import me.bazhanau.ticketreservation.service.MovieServiceActor.Reserve

import scala.concurrent.duration._

trait Routes extends Directives with JsonProtocol with StrictLogging {

  implicit val timeout: Timeout = 5.seconds
  protected val movieService: ActorRef

  val routes =
    pathPrefix("movies") {
      get {
        path(Segment / Segment) { (imdbId: String, screenId: String) =>
          val future = (movieService ? FindOne(imdbId, screenId)).mapTo[Option[Movie]]
          onSuccess(future){
            case Some(res) => complete(ToResponseMarshallable(res))
            case None => complete(HttpResponse(StatusCodes.NotFound))
          }
        }
      } ~
        post {
          entity(as[MovieRegistration]) { registration =>
            (validate(1 to 1000 contains registration.availableSeats,
              "availableSeats must be from 1 to 1000") &
              validate(registration.imdbId.length <= 30,
              "imdbId is too long") &
              validate(registration.screenId.length <= 30,
              "screenId is too long")) {

              val res = (movieService ? Register(registration)).mapTo[Movie]
              onSuccess(res)(complete(_))
            }
          }
        }
    } ~
      path("reservations") {
        post {
          entity(as[MovieReservation]) { reservation =>
            val future = (movieService ? Reserve(reservation)).mapTo[Option[Movie]]
            onSuccess(future){
              case Some(res) => complete(res)
              case None => complete(HttpResponse(StatusCodes.BadRequest))
            }
          }
        }
      }

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case ex@DuplicateMovieException(_) =>
        complete(HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(ex.getMessage)))
      case ex: Exception =>
        logger.error("Failed to process request", ex)
        complete(HttpResponse(StatusCodes.InternalServerError))
    }

}
