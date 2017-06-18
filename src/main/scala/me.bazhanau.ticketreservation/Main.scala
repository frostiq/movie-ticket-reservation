package me.bazhanau.ticketreservation

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import me.bazhanau.ticketreservation.dao.MongoMovieDao
import me.bazhanau.ticketreservation.service.MovieServiceActor
import org.mongodb.scala.MongoClient

import scala.io.StdIn

object Main {

  def main(args: Array[String]) {

    implicit val system = ActorSystem("movie-ticket-reservation")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val mongoClient = MongoClient()
    val movieDao = new MongoMovieDao(mongoClient.getDatabase("test"))
    val movieServiceActor = system.actorOf(MovieServiceActor.props(movieDao))
    val routes = new Routes(movieServiceActor).routes

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

}
