package me.bazhanau.ticketreservation

import java.util.logging.Level
import java.util.logging.Logger

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import me.bazhanau.ticketreservation.dao.MongoMovieDao
import me.bazhanau.ticketreservation.routing.Routes
import me.bazhanau.ticketreservation.service.MovieServiceActor
import org.mongodb.scala.MongoClient

import scala.io.StdIn

object Main extends App with Routes {
  implicit val system = ActorSystem("movie-ticket-reservation")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING)
  val mongoClient = MongoClient()
  val movieDao = new MongoMovieDao(mongoClient.getDatabase("test"))
  override val movieService = system.actorOf(MovieServiceActor.props(movieDao))

  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => {
      system.terminate()
      mongoClient.close()
    })
}
