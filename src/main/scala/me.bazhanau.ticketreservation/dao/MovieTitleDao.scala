package me.bazhanau.ticketreservation.dao

import scala.concurrent.Future

trait MovieTitleDao {
  def find(imdbId: String): Future[Option[String]]
}
