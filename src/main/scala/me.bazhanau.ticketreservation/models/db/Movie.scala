package me.bazhanau.ticketreservation.models.db

case class Movie(movieId: MovieId, availableSeats: Int, reservedSeats: Int, movieTitle: String)

object Movie{
  def apply(_id: MovieId, availableSeats: Int, reservedSeats: Int, movieTitle: String): Movie =
    Movie(_id, availableSeats, reservedSeats, movieTitle)
}