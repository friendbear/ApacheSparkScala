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

    val wordCounts = lowercaseWords.map(x => (x, 1)).reduceByKey((x, y) => x + y)

    val wordCountsSorted = wordCounts.map(x => (x._2, x._1)).sortByKey().collect()

    wordCountsSorted.foreach{ v =>
      println(s"${v._2}: ${v._1}")
    }

    sc.stop()
  }

}

