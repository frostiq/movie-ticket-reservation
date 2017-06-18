package me.bazhanau.ticketreservation

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import akka.pattern.ask
import akka.util.Timeout
import me.bazhanau.ticketreservation.conversion.JsonProtocol
import me.bazhanau.ticketreservation.model.Movie
import me.bazhanau.ticketreservation.model.MovieRegistration
import me.bazhanau.ticketreservation.model.MovieReservation
import me.bazhanau.ticketreservation.service.MovieServiceActor.FindOne
import me.bazhanau.ticketreservation.service.MovieServiceActor.Register

import scala.concurrent.duration._

class Routes(movieService: ActorRef) extends JsonProtocol {

  implicit val timeout: Timeout = 5.seconds

  val routes =
    pathPrefix("movies") {
      get {
        path(Segment / Segment) { (imdbId: String, screenId: String) =>
          val f = (movieService ? FindOne(imdbId, screenId)).mapTo[Option[Movie]]
          onSuccess(f)(complete(_))
        }
      } ~
      post {
        entity(as[MovieRegistration]) { registration =>
          val f = (movieService ? Register(registration)).mapTo[Movie]
          onSuccess(f)(complete(_))
        }
      }
    } ~
    path("reservations") {
      post {
        entity(as[MovieReservation]) { reservation =>
          complete(reservation)
        }
      }
    }
}
