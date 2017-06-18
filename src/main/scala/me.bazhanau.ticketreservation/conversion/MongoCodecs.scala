package me.bazhanau.ticketreservation.conversion

import me.bazhanau.ticketreservation.model.mongo.Movie
import me.bazhanau.ticketreservation.model.mongo.MovieId
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

trait MongoCodecs {
  val codecRegistry = fromRegistries(
    fromProviders(classOf[MovieId], classOf[Movie]),
    DEFAULT_CODEC_REGISTRY
  )
}
