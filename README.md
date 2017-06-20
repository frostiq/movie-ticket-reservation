# Movie Ticket Reservation System

[Task description on wiki](https://github.com/frostiq/movie-ticket-reservation/wiki/Task-description)

## Prerequisites:

You need to have Java and sbt installed on your machine (tested with `OpenJDK v1.8.0_131` and `sbt v0.13.8`)

Also, you need to have running MongoDB on `localhost:27017` (you can change the address in the config)

If you have docker installed, you can run local MongoDB inside a container:

```
sudo docker run --name some-mongo -p 27017:27017 -d mongo
```

## How to run:

From the project folder you can run the next command in the console:

```
sbt run
```

It will start HTTP server on localhost:8080

## How to use:

### Register a movie:

```
curl http://localhost:8080/movies -H "Content-Type: application/json" \
-d '{ "imdbId": "tt0111161", "screenId": "screen_123456", "availableSeats": 100 }'
```

Where:
* `imdbId` is IMDB movie identifier
* `screenId` is an externally managed identifier of information when and where the movie is screened.
* `availableSeats` the total seats available for this movie

The result will be a registered movie:
```
{
    "imdbId":"tt0111161",
    "screenId":"screen_123456",
    "movieTitle":"The Shawshank Redemption",
    "availableSeats":100,
    "reservedSeats":0    
}
```

### Retrieve information about the movie

```
curl http://localhost:8080/movies/{imdbId}/{screenId}
```

An example of the response:

```
{
   "imdbId": "tt0111161",
   "screenId": "screen_123456",
   "movieTitle": "The Shawshank Redemption",
   "availableSeats": 100,
   "reservedSeats": 0
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

The result will be updated movie with one newly reserved seat:

```
{
    "imdbId":"tt0111161",
    "screenId":"screen_123456",
    "movieTitle":"The Shawshank Redemption",
    "availableSeats":99,
    "reservedSeats":1
}

```
