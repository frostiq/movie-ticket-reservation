case class MovieRegistration(
                              imdbId: String,
                              screenId: String,
                              availableSeats: Int
                            )

case class Movie(
                  imdbId: String,
                  screenId: String,
                  availableSeats: Int,
                  reservedSeats: Int,
                  movieTitle: String
                ) {
  def apply(registration: MovieRegistration, title: String): Movie = Movie(
    registration.imdbId,
    registration.screenId,
    registration.availableSeats,
    0,
    title)
}

case class MovieReservation(
                             imdbId: String,
                             screenId: String
                           )