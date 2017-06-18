package me.bazhanau.ticketreservation.conversion

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import me.bazhanau.ticketreservation.models.MovieRegistration
import me.bazhanau.ticketreservation.models.MovieReservation
import me.bazhanau.ticketreservation.models.db.Movie
import me.bazhanau.ticketreservation.models.db.MovieId
import spray.json._

class JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val movieIdFormat = jsonFormat2(MovieId)
  implicit val movieFormat = jsonFormat4(Movie)
  implicit val movieRegistrationFormat = jsonFormat3(MovieRegistration)
  implicit val movieReservationFormat = jsonFormat2(MovieReservation)
}
