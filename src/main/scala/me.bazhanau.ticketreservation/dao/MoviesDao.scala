package me.bazhanau.ticketreservation.dao

import me.bazhanau.ticketreservation.conversion.MongoCodecs
import me.bazhanau.ticketreservation.models.db.Movie
import me.bazhanau.ticketreservation.models.db.MovieId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait MoviesDao{
  def findOne(id: MovieId): Future[Option[Movie]]
  def insert(movie: Movie): Future[Movie]
  def reserveSeat(id: MovieId): Future[Unit]
}

class MongoMoviesDao(db: MongoDatabase)(implicit executionContext: ExecutionContext) extends MoviesDao with MongoCodecs{
  val collection: MongoCollection[Movie] = db.withCodecRegistry(codecRegistry).getCollection("movies")

  override def findOne(id: MovieId): Future[Option[Movie]] = {
    collection.find(equal("_id", id))
      .toFuture()
      .map(_.headOption)
  }

  override def insert(movie: Movie): Future[Movie] = {
    collection.insertOne(movie)
      .toFuture()
      .flatMap(x => findOne(movie.movieId))
      .map(_.get)
  }

  //TODO: was not found
  override def reserveSeat(id: MovieId): Future[Unit] = {
    collection.findOneAndUpdate(
      equal("_id", id),
      combine(inc("availableSeats", -1), inc("reservedSeats", 1))
    ).toFuture().map(_ => ())
  }
}
