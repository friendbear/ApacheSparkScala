package local.spark

import java.util.concurrent.atomic.AtomicLong

import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}


object AverageTweetLength2 extends App {

  override def main(args: Array[String]): Unit = {

    Utilities.setupTwitter()

    val ssc = new StreamingContext("local[*]", "MaxTweetLike", Seconds(1))
    Utilities.setupLogging()

    val tweets = TwitterUtils.createStream(ssc, None)
    val totalTweets = new AtomicLong(0)
    val totalChars = new AtomicLong(0)
    tweets.foreachRDD((rdd, time) => {
      val count = rdd.count()
      if (count > 0) {
        totalTweets.getAndAdd(count)
        totalChars.getAndAdd(rdd.map(_.getText.length).reduce(_ + _))

        println(s"Total tweets: ${totalTweets.get()} Total Chars: ${totalChars.get()} " +
          s"Average ${totalChars.get()/ totalTweets.get()}")
      }
    })

    ssc.checkpoint("/tmp/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
  }

}
