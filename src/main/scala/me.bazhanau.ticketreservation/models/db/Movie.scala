package me.bazhanau.ticketreservation.models.db

case class Movie(_id: MovieId, availableSeats: Int, reservedSeats: Int, movieTitle: String)