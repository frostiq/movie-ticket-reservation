# me.bazhanau.ticketreservation.models.db.Movie Ticket Reservation System

[Task description on wiki](https://github.com/frostiq/movie-ticket-reservation/wiki/Task-description)

## Prerequisites:

You need to have Java and sbt installed on your machine (tested with `Java v1.8.0_131` and `sbt v0.13.8`)


## How to run:

Form the project folder execute next command in console:

```
sbt run
```

It will start HTTP server on localhost:8080

## How to use:

### Register a movie:

```
curl http://localhost:8080/movies -H "Content-Type: application/json" \
-d '{ "imdbId": "tt0111161", "availableSeats": 100, "screenId": "screen_123456" }'
```

Where:
* `imdbId` is IMDB movie identifier
* `screenId` is an externally managed identifier of information when and where the movie is screened.
* `availableSeats` the total seats available for this movie

### Retrieve information about the movie

```
curl http://localhost:8080/movies/{imdbId}/{screenId}
```

Example of the response:

```
     {
        "imdbId": "tt0111161",
        "screenId": "screen_123456",
        "movieTitle": "The Shawshank Redemption",
        "availableSeats": 100,
        "reservedSeats": 50
     }   
```

Where:
* `imdbId` is IMDB movie identifier
* `screenId` is an externally managed identifier of information when and where the movie is screened.
* `movieTitle` is the title of the movie
* `availableSeats` the total seats available for this movie
* `reservedSeats` the total number of reserved seats for a movie and screen.

### Reserve a seat at the movie

```
curl http://localhost:8080/reservations -H "Content-Type: application/json" \
-d '{ "imdbId": "tt0111161", "screenId": "screen_123456" }'
```

Where:
* `imdbId` is IMDB movie identifier
* `screenId` is an externally managed identifier of information when and where the movie is screened.