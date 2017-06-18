package me.bazhanau.ticketreservation.dao

import me.bazhanau.ticketreservation.conversion.MongoCodecs
import me.bazhanau.ticketreservation.models.db.Movie
import me.bazhanau.ticketreservation.models.db.MovieId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.FindOneAndUpdateOptions
import org.mongodb.scala.model.ReturnDocument
import org.mongodb.scala.model.Updates._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait MoviesDao{
  def findOne(id: MovieId): Future[Option[Movie]]
  def insert(movie: Movie): Future[Movie]
  def reserveSeat(id: MovieId): Future[Option[Movie]]
}

class MongoMoviesDao(db: MongoDatabase)(implicit executionContext: ExecutionContext)
  extends MoviesDao with MongoCodecs{

  val collection: MongoCollection[Movie] = db.withCodecRegistry(codecRegistry).getCollection("movies")

  override def findOne(id: MovieId): Future[Option[Movie]] = {
    collection.find(equal("_id", id))
      .toFuture()
      .map(_.headOption)
  }

  override def insert(movie: Movie): Future[Movie] = {
    collection.insertOne(movie)
      .toFuture()
      .flatMap(x => findOne(movie._id))
      .map(_.get)
  }

  override def reserveSeat(id: MovieId): Future[Option[Movie]] = {
    collection.findOneAndUpdate(
      and(equal("_id", id), gt("availableSeats", 0)),
      combine(inc("availableSeats", -1), inc("reservedSeats", 1)),
      FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
    ).toFuture().map(Option(_))
  }
}
