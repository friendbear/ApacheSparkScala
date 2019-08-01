package local.spark.core

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object MostPopularSuperhero {

  def countCoOccurences(line: String) = {
    val elements = line.split("\\s+")
    (elements(0).toInt, elements.length -1)
  }

  def parseNames(line: String): Option[(Int, String)] = {
    val fields = line.split('\"')
    if (fields.length > 1) {
      Some(fields(0).trim.toInt, fields(1))
    } else None
  }

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local[*]", "MostPopularSuperhero")

    val names = sc.textFile("resources/data/marvel-names.txt")
    val namesRdd = names.flatMap(parseNames)

    val lines = sc.textFile("resources/data/marvel-graph.txt")
    val pairing = lines.map(countCoOccurences)

    val totalFriendsByCharacter = pairing.reduceByKey((x, y) => x + y)
    val flipped = totalFriendsByCharacter.map(x => (x._2, x._1))

    val mostPopular = flipped.max()

    val mostPopularName = namesRdd.lookup(mostPopular._2).head

    println(s"$mostPopularName is the most popular superhero with ${mostPopular._1} co-appearances.")

    sc.stop
  }
}
