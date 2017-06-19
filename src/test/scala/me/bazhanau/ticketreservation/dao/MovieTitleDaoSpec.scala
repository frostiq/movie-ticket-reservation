package me.bazhanau.ticketreservation.dao

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri
import me.bazhanau.ticketreservation.util.ActorSpec
import me.bazhanau.ticketreservation.util.ExternalApiTest
import me.bazhanau.ticketreservation.util.MongoSpec

import scala.util.Random

class MovieTitleDaoSpec extends ActorSpec with MongoSpec {

  val requester = Http().singleRequest(_ : HttpRequest)
  val baseUri = Uri(config.getString("omdbapi.baseUrl"))
  val webDao: MovieTitleDao = new MovieTitleWebDao(
    requester,
    baseUri,
    config.getString("omdbapi.apiKey")
  )

  "MovieTitleWebDao" should "return title, if it exists" taggedAs ExternalApiTest in {
    val dao = new MovieTitleDbCache(webDao, db)
    dao.find("tt0111161").map(s => {
      s shouldBe defined
      s.get shouldBe "The Shawshank Redemption"
    })
  }

  it should "return None, if it not exists" taggedAs ExternalApiTest in {
    val dao = new MovieTitleDbCache(webDao, db)
    dao.find(Random.nextString(10)).map(s => s shouldBe None)
  }
}
