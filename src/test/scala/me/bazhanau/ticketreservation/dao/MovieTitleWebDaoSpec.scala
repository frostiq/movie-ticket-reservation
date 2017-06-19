package me.bazhanau.ticketreservation.dao

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri
import me.bazhanau.ticketreservation.util.ActorSpec
import org.scalatest.AsyncFlatSpecLike

import scala.util.Random

class MovieTitleWebDaoSpec extends ActorSpec with AsyncFlatSpecLike {

  val requester = Http().singleRequest(_ : HttpRequest)
  val baseUri = Uri(config.getString("omdbapi.baseUrl"))
  val dao: MovieTitleDao = new MovieTitleWebDao(requester, baseUri, config.getString("omdbapi.apiKey"))

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
