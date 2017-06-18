package me.bazhanau.ticketreservation.service

import akka.actor.Actor
import akka.actor.Props
import akka.pattern.pipe
import me.bazhanau.ticketreservation.dao.MovieDao
import me.bazhanau.ticketreservation.dao.MovieTitleDao
import me.bazhanau.ticketreservation.model.MovieRegistration
import me.bazhanau.ticketreservation.model.MovieReservation
import me.bazhanau.ticketreservation.model.db.Movie
import me.bazhanau.ticketreservation.model.db.MovieId
import me.bazhanau.ticketreservation.service.MovieServiceActor.FindOne
import me.bazhanau.ticketreservation.service.MovieServiceActor.Register
import me.bazhanau.ticketreservation.service.MovieServiceActor.Reserve

import scala.concurrent.ExecutionContext

class MovieServiceActor(movieDao: MovieDao, movieTitleDao: MovieTitleDao) extends Actor {

  implicit val ec: ExecutionContext = context.dispatcher

  override def receive: Receive = {
    case FindOne(imdbId, screenId) =>
      movieDao.findOne(MovieId(imdbId, screenId))
        .map(_.map(_.toViewModel)) pipeTo sender()

    case Register(registration) =>
      movieTitleDao.find(registration.imdbId)
        .map(title => Movie(
          MovieId(registration.imdbId, registration.screenId),
          registration.availableSeats,
          0,
          title.getOrElse("?")
        ))
        .flatMap(movieDao.insert)
        .map(_.toViewModel) pipeTo sender()

    case Reserve(reservation) =>
      movieDao.reserveSeat(MovieId(
        reservation.imdbId, reservation.screenId
      )) map (_.map(_.toViewModel)) pipeTo sender()
  }
}

object MovieServiceActor {
  def props(moviesDao: MovieDao, movieTitleDao: MovieTitleDao): Props = {
    Props.create(classOf[MovieServiceActor], moviesDao, movieTitleDao)
  }

  case class FindOne(imdbId: String, screenId: String)

  case class Register(movie: MovieRegistration)

  case class Reserve(reservation: MovieReservation)

}
