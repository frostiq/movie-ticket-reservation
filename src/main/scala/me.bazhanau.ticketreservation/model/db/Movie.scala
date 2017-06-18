package me.bazhanau.ticketreservation.model.db

case class Movie(_id: MovieId, availableSeats: Int, reservedSeats: Int, movieTitle: String){
  type ViewModel = me.bazhanau.ticketreservation.model.Movie
  def toViewModel = new ViewModel(_id.imdbId, _id.screenId, availableSeats, reservedSeats, movieTitle)
}
