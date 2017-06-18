package me.bazhanau.ticketreservation.dao

import me.bazhanau.ticketreservation.model.db.Movie
import me.bazhanau.ticketreservation.model.db.MovieId

import scala.concurrent.Future

trait MovieDao{
  def findOne(id: MovieId): Future[Option[Movie]]
  def insert(movie: Movie): Future[Movie]
  def reserveSeat(id: MovieId): Future[Option[Movie]]
}


