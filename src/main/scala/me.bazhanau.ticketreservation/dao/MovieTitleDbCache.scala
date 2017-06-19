package me.bazhanau.ticketreservation.dao

import com.mongodb.client.model.FindOneAndUpdateOptions
import me.bazhanau.ticketreservation.conversion.MongoCodecs
import me.bazhanau.ticketreservation.model.db.Title
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.ReturnDocument
import org.mongodb.scala.model.Updates._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class MovieTitleDbCache(underlyingDao: MovieTitleDao, db: MongoDatabase)
                       (implicit executionContext: ExecutionContext)
  extends MovieTitleDao with MongoCodecs{

  val collection: MongoCollection[Title] =
    db.withCodecRegistry(codecRegistry).getCollection("titlesCache")

  override def find(imdbId: String) = {

    collection.find(equal("_id", imdbId))
      .toFuture()
      .map(_.headOption)
      .flatMap {
        case Some(title) => Future.successful(Some(title.title))
        case None => underlyingDao.find(imdbId)
          .flatMap(insertInCache(imdbId))
      }

  }

  private def insertInCache(imdbId: String)(titleOpt: Option[String]): Future[Option[String]] = {
    val findOptions = new FindOneAndUpdateOptions()
      .upsert(true)
      .returnDocument(ReturnDocument.AFTER)

    titleOpt.map { title =>
      collection.findOneAndUpdate(
        equal("_id", imdbId),
        setOnInsert("title", title),
        findOptions
      )
        .toFuture()
        .map(_ => Some(title))

    } getOrElse Future.successful(None)
  }
}
