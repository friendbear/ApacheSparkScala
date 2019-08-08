package local.spark.core

import java.nio.charset.CodingErrorAction

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

import scala.io.{Codec, Source}
import scala.math.sqrt

/**
  * Map input rating to (userId, (movieId, rating))
  * Find every movie pare rated by the same user
  * - Theis can be done with a "self-join" operation
  * - At this point we have (userId, ((movieId1, rating1), (movieId2, rating2)))
  * Filter out duplicate pares
  * Make the movie pairs the key
  * - map to (movieId1, movieId2), (rating1, rating2))
  * groupByKey() to get every rating pair found for each movie pair
  * Compute similarity between ratings for each movie in the pair
  * Sort, save, and display results.
  *
  * In this example, we'll query the final RDD of movie similarities a couple of time
  * Any time you will perform more than one action on an RDD,
  * you must cache it!
  * - Otherwise, Spark might re-evaluate the entire RDD all over again!
  *
  * Use .cache() or .persist() to do this.
  * - Persist() optionally lets you cache it to disk instead of justmemory,
  *   just in case a node fails or something.
  *
  */
object MovieSimilarities {

  type MovieRating = (Int, Double)
  type UserRatingPair = (Int, (MovieRating, MovieRating))
  /** Load up a Map of movie IDs to movie names. */
  def loadMovieNames() : Map[Int, String] = {

    // Handle character encoding issues:
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    // Create a Map of Ints to Strings, and populate it from u.item.
    var movieNames:Map[Int, String] = Map()

    val lines = Source.fromFile("../ml-100k/u.item").getLines()
    for (line <- lines) {
      line.split('|') match {
        case Array(a, b,  _*) => movieNames += (a.toInt -> b)
        case _ =>
      }
    }
    movieNames
  }

  def makePairs(userRatings: UserRatingPair) = {
    val movieRating1 = userRatings._2._1
    val movieRating2 = userRatings._2._2

    val movie1 = movieRating1._1
    val rating1 = movieRating1._2
    val movie2 = movieRating2._1
    val rating2 = movieRating2._2
    ((movie1, movie2), (rating1, rating2))
  }

  type RatingPair = (Double, Double)
  type RatingPairs = Iterable[RatingPair]

  def filterDuplicates(userRatings: UserRatingPair) = {
    val movieRating1 = userRatings._2._1
    val movieRating2 = userRatings._2._2

    val movie1 = movieRating1._1
    val movie2 = movieRating2._1
    movie1 < movie2
  }

  def computeCosineSimilarity(ratingPairs: RatingPairs) ={
    var numPairs = 0
    var sum_xx = 0.0
    var sum_yy = 0.0
    var sum_xy = 0.0

    for (pair <- ratingPairs) {
      val ratingX = pair._1
      val ratingY = pair._2
      sum_xx += ratingX * ratingX
      sum_yy += ratingY * ratingY
      sum_xy += ratingX * ratingY
      numPairs += 1
    }
    val numerator = sum_xy
    val denominator = sqrt(sum_xx) * sqrt(sum_yy)
    val score = if (denominator != 0) numerator / denominator else 0

    (score, numPairs)
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local[*]", "MovieSimilarities")

    println("\nLoading movie names...")
    val nameDict = loadMovieNames

    val data = sc.textFile("../ml-100k/u.data")

    // Map ratings to key /value pairs: userId => movieId, rating
    val ratings = data.map(_.split("\t")).map(v =>(v(0).toInt, (v(1).toInt, v(2).toDouble)))

    // Self-join to find every combination.
    val joindRatings = ratings.join(ratings)

    // RDD consists of userId => ((movieId, rating), (movieId, rating))

    // Filter out duplicate pairs
    val uniqueJoinedRatings = joindRatings.filter(filterDuplicates)


    // RDD (movie1, movie2) pairs.
    // (movie1, movie2) => (rating1, rating2)
    val moviePairs = uniqueJoinedRatings.map(makePairs)
    val moviePairRatings = moviePairs.groupByKey()

    //
    // => (movie1, movie2) => (rating1, rating2), (rating1, rating2) ...
    // Cashed
    val moviePairSimilarities = moviePairRatings.mapValues(computeCosineSimilarity).cache

    /**
      * Save the results if desired
      * val sorted = moviePairSimilarities.sortByKey()
      * sorted.saveAsTextFile("movie-sims")
      */

    val scoreThreshold = 0.97
    val coOccurenceThreshold = 50.0
    val topTake = 50

    val movieId = {
      if (!args.isEmpty) args(0).toInt
      else 50
    }

    val filteredResults = moviePairSimilarities.filter(x => {
      val pair = x._1
      val sim = x._2
      (pair._1 == movieId || pair._2 == movieId) && sim._1 > scoreThreshold && sim._2 > coOccurenceThreshold
    })

    val results = filteredResults.map(v =>(v._2, v._1)).sortByKey(false).take(topTake)

    println(s"\nTop $topTake similar movies for ${nameDict(movieId)}")
    results.foreach(v => {
      val sim = v._1
      val pair = v._2

      var similarMovieId = pair._1
      if (similarMovieId == movieId) {
        similarMovieId = pair._2
      }
      println(s"${nameDict(similarMovieId)} \tscore: ${sim._1} \tstrength: ${sim._2}")

    })

    sc.stop
  }
}
