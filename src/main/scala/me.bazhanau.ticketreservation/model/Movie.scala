package me.bazhanau.ticketreservation.model

case class Movie (
                   imdbId: String,
                   screenId: String,
                   availableSeats: Int,
                   reservedSeats: Int,
                   movieTitle: String
                 )
