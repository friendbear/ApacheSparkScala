package local.spark
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.StreamingContext._
import Utilities._

object SaveTweets extends App {
  val MaxTweetCollectionCount = 1000
  Utilities.setupTwitter()

  val ssc = new StreamingContext("local[4]", "SaveTweet", Seconds(1))

  Utilities.setupLogging()

  val tweets = TwitterUtils.createStream(ssc, None)
  val statuses = tweets.map(status => status.getText())

  //statuses.saveAsTextFiles("Tweets", "txt")

  var totalTweets = 0L

  statuses.foreachRDD((rdd, time) => {
    if (rdd.count() > 0 ) {
      val repartitionedRDD = rdd.repartition(1).cache()
      repartitionedRDD.saveAsTextFile("Tweets_" + time.milliseconds.toString)
      // Stop collected n tweets.
      totalTweets = totalTweets + repartitionedRDD.count()
      println(s"Tweet count: $totalTweets")
      if (totalTweets > MaxTweetCollectionCount) System.exit(0)
    }
  })

  ssc.checkpoint("/tmp/checkpoint")
  ssc.start()
  ssc.awaitTermination()
}
