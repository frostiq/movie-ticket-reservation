package me.bazhanau.ticketreservation.service

import akka.actor.Actor
import akka.actor.Props
import akka.pattern.pipe
import me.bazhanau.ticketreservation.dao.MovieDao
import me.bazhanau.ticketreservation.model.MovieRegistration
import me.bazhanau.ticketreservation.model.MovieReservation
import me.bazhanau.ticketreservation.model.db.Movie
import me.bazhanau.ticketreservation.model.db.MovieId
import me.bazhanau.ticketreservation.service.MovieServiceActor.FindOne
import me.bazhanau.ticketreservation.service.MovieServiceActor.Register
import me.bazhanau.ticketreservation.service.MovieServiceActor.Reserve

import scala.concurrent.ExecutionContext

class MovieServiceActor(moviesDao: MovieDao) extends Actor{

  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case FindOne(imdbId, screenId) =>
      moviesDao.findOne(MovieId(imdbId, screenId))
        .map(_.map(_.toViewModel)) pipeTo sender()

    case Register(registration) =>
      val movie: Movie = Movie(
        MovieId(registration.imdbId, registration.screenId),
        registration.availableSeats,
        0,
        "temp"
      )
      moviesDao.insert(movie).map(_.toViewModel) pipeTo sender()

    case Reserve(reservation) =>
      moviesDao.reserveSeat(MovieId(
        reservation.imdbId, reservation.screenId
      )) map(_.map(_.toViewModel)) pipeTo sender()
  }
}

object MovieServiceActor{
  case class FindOne(imdbId: String, screenId: String)
  case class Register(movie: MovieRegistration)
  case class Reserve(reservation: MovieReservation)

  def props(moviesDao: MovieDao): Props = {
    Props.create(classOf[MovieServiceActor], moviesDao)
  }
}
