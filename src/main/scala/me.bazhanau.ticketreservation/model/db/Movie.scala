package me.bazhanau.ticketreservation.model.db

case class Movie(_id: MovieId, availableSeats: Int, reservedSeats: Int, movieTitle: String)