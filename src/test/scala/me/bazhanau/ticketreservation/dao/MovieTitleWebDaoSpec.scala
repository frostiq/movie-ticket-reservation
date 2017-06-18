package me.bazhanau.ticketreservation.dao

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import org.scalatest._

import scala.util.Random

class MovieTitleWebDaoSpec extends TestKit(ActorSystem("MovieTitleWebDaoSpec"))
  with Matchers
  with AsyncFlatSpecLike {

  implicit val materializer = ActorMaterializer()
  val f = Http().singleRequest(_ : HttpRequest)
  val baseUri = Uri("http://www.omdbapi.com/")
  val dao: MovieTitleDao = new MovieTitleWebDao(f, baseUri, "289ba303")

  "MovieTitleWebDao" should "return title, if it exists" in {
    dao.find("tt0111161").map(s => {
      s shouldBe defined
      s.get shouldBe "The Shawshank Redemption"
    })
  }

  it should "return None, if it not exists" in {
    dao.find(Random.nextString(10)).map(s => s shouldBe None)
  }
}
