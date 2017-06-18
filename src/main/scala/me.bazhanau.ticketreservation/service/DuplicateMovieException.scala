package me.bazhanau.ticketreservation.service

case class DuplicateMovieException(e: Exception)
  extends RuntimeException("Movie with specified key already exists", e)
