package me.bazhanau.ticketreservation.conversion

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import me.bazhanau.ticketreservation.model.Movie
import me.bazhanau.ticketreservation.model.MovieRegistration
import me.bazhanau.ticketreservation.model.MovieReservation
import me.bazhanau.ticketreservation.model.web.OmdbResponse
import spray.json._

trait JsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val movieFormat = jsonFormat5(Movie)
  implicit val movieRegistrationFormat = jsonFormat3(MovieRegistration)
  implicit val movieReservationFormat = jsonFormat2(MovieReservation)
  implicit val omdbResponseFormat = jsonFormat1(OmdbResponse)
}
