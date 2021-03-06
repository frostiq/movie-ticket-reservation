package me.bazhanau.ticketreservation.dao

import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import me.bazhanau.ticketreservation.conversion.JsonProtocol
import me.bazhanau.ticketreservation.model.web.OmdbResponse
import spray.json.DeserializationException

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class MovieTitleWebDao(get: (HttpRequest) => Future[HttpResponse], baseUri: Uri, apikey: String)
                      (implicit val m: Materializer, implicit val ec: ExecutionContext)
  extends MovieTitleDao with JsonProtocol with StrictLogging{

  override def find(imdbId: String) = {
    val uri = baseUri.withQuery(Uri.Query("i" -> imdbId, "apikey" -> apikey))
    val request = HttpRequest(HttpMethods.GET, uri)
    get(request)
      .flatMap( response => {
        response.status match {
          case StatusCodes.OK =>
            Unmarshal(response.entity)
              .to[OmdbResponse]
              .map(_.Title)
              .map(Some(_))
              .recover({case e: DeserializationException =>
                logError(response, imdbId)
                None
              })

          case _ =>
            logError(response, imdbId)
            Future.successful[Option[String]](None)
        }
      })
  }

  private def logError(response: HttpResponse, imdbId: String): Unit =
  {
    logger.warn(s"Failed to retrieve movie title with imdbId = $imdbId\n$response")
  }
}
