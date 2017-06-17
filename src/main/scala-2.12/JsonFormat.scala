import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

class JsonFormat extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val movieFormat = jsonFormat5(Movie)
  implicit val movieRegistrationFormat = jsonFormat3(MovieRegistration)
  implicit val movieReservationFormat = jsonFormat2(MovieReservation)

}
