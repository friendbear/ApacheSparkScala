package local.spark.core

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object WordCountSorted {

  def main(args: Array[String]) {

    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Create a SparkContext using every core of the local machine
    val sc = new SparkContext("local[*]", appName = "WordCountSorted")

    // Read each line of my book into an RDD
    val input = sc.textFile("resources/data/book.txt")

    // Split into words separated by a space character
    val words = input.flatMap(x => x.split("\\W+"))

    val lowercaseWords = words.map(_.toLowerCase)

    val wordCounts = lowercaseWords.map(x => (x, 1)).reduceByKey((v1, v2) => v1 + v2)

    val wordCountsSorted = wordCounts.map(x => (x._2, x._1)).sortByKey(false).collect()

    val filterCount = wordCountsSorted.count(v1 => v1._1 > 30)

    println("-" * 10 + filterCount)
    wordCountsSorted.filter(x => x._1 > 30).foreach{ v =>
      println(s"${v._2}: ${v._1}")
    }


    sc.stop()
  }

}
