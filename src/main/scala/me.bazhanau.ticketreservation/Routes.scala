package me.bazhanau.ticketreservation

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import me.bazhanau.ticketreservation.conversion.JsonProtocol
import me.bazhanau.ticketreservation.models.MovieRegistration
import me.bazhanau.ticketreservation.models.MovieReservation

trait Routes extends JsonProtocol{
  val route =
    pathPrefix("movies") {
      path(Segment / Segment) { (imdbId: String, screenId: String) =>
        complete(s"imdbId=$imdbId, screenId=$screenId")
      } ~
      post{
        entity(as[MovieRegistration]){ registration =>
          complete(registration)
        }
      }
    } ~
    path("reservations"){
      post{
        entity(as[MovieReservation]){ reservation =>
          complete(reservation)
        }
      }
    }
}
