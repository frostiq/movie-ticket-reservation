package me.bazhanau.ticketreservation

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import me.bazhanau.ticketreservation.dao.MongoMovieDao
import me.bazhanau.ticketreservation.dao.MovieTitleDao
import me.bazhanau.ticketreservation.dao.MovieTitleWebDao
import me.bazhanau.ticketreservation.routing.Routes
import me.bazhanau.ticketreservation.service.MovieServiceActor
import org.mongodb.scala.MongoClient

import scala.io.StdIn

object Main extends App with Routes with StrictLogging {
    implicit val system = ActorSystem("movie-ticket-reservation")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val config = ConfigFactory.load()

    val mongoClient = MongoClient(config.getString("mongodb.connectionString"))
    val movieDao = new MongoMovieDao(mongoClient.getDatabase(config.getString("mongodb.dbName")))

    val requester = Http().singleRequest(_: HttpRequest)
    val baseUri = Uri(config.getString("omdbapi.baseUrl"))
    val movieTitleDao: MovieTitleDao = new MovieTitleWebDao(requester, baseUri, config.getString("omdbapi.apiKey"))

    override val movieService = system.actorOf(MovieServiceActor.props(movieDao, movieTitleDao))

    val bindingFuture = Http().bindAndHandle(routes, "localhost", config.getInt("server.port"))

    logger.info(s"Server online at http://localhost:${config.getInt("server.port")}/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => {
        system.terminate()
        mongoClient.close()
        logger.info("Server stopped")
      })
}
