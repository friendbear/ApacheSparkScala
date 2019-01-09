package local.m2

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._

object WordCount {

  def main(args: Array[String]) {

    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Create a SparkContext using every core of the local machine
    val sc = new SparkContext("local[*]", "WordCount")

    // Read each line of my book into an RDD
    val input = sc.textFile("resources/data/book.txt")

    // Split into words separated by a space character
    val words = input.flatMap(x => x.split("\\W+"))

    val lowercaseWords = words.map(_.toLowerCase)
    // Count up the occurrences of each word
    val wordCounts = lowercaseWords.countByValue()

    // Print the results.
    wordCounts.foreach(println)

    sc.stop()
  }

}

