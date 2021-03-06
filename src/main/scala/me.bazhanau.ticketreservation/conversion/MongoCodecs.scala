package me.bazhanau.ticketreservation.conversion

import me.bazhanau.ticketreservation.model.db._
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

trait MongoCodecs {
  val codecRegistry = fromRegistries(
    fromProviders(classOf[MovieId], classOf[Movie], classOf[Title]),
    DEFAULT_CODEC_REGISTRY
  )
}
