import akka.http.scaladsl.server.Directives._

trait Routes extends JsonFormat{
  val route =
    pathPrefix("movies") {
      path(Segment / Segment) { (imdbId: String, screenId: String) =>
        complete(s"imdbId=$imdbId, screenId=$screenId")
      } ~
      post{
        entity(as[MovieRegistration]){ registration =>
          complete(registration)
        }
      }
    } ~
    path("reservations"){
      post{
        entity(as[MovieReservation]){ reservation =>
          complete(reservation)
        }
      }
    }
}
