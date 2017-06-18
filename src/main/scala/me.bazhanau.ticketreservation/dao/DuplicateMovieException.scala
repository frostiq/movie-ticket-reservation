package me.bazhanau.ticketreservation.dao

case class DuplicateMovieException(e: Exception)
  extends RuntimeException("Movie with specified key already exists", e)
