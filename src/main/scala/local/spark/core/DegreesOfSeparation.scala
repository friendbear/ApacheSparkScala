package local.spark.core

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.util.LongAccumulator

import scala.collection.mutable.ArrayBuffer

object DegreesOfSeparation {

  // the characters we want to find the separation between.
  val startCharacterID = 5306 // SpiderMan
  val targetCharacterID = 14 //

  var hitCounter: Option[LongAccumulator] = None // accumulator a "global" Option

  type BFSData = (Array[Int], Int, String) // array hero ID connection, the distance, and color.
  type BFSNode = (Int, BFSData)

  def convertToBFS(line: String) : BFSNode = {
    val fields = line.split("\\s+").map(_.toInt)
    val heroID = fields(0)

    var connections = ArrayBuffer[Int]()
    for (c <- 1 to fields.length -1) {
      connections += fields(c)
    }
    var color = "WHITE"
    var distance = 9999

    if (heroID == startCharacterID) {
      color = "GRAY"
      distance = 0
    }
    println(s"$heroID, $connections, $distance, $color")
    (heroID, (connections.toArray, distance, color))
  }

  /**
    * Create "iteration 0 of our RDD of BFSNodes
    */
  def createStartingRdd(sc: SparkContext): RDD[BFSNode] = {
    val inputFile = sc.textFile("resources/data/marvel-graph.txt")
    inputFile.map(convertToBFS)
  }

  /**
    * Expands aBFSNode into this node and its children
    */
  def bfsMap(node: BFSNode): Array[BFSNode] = {

    val characterID = node._1
    val data: BFSData = node._2

    val connections: Array[Int] = data._1
    val distance = data._2
    var color: String = data._3

    // Gray nodes are flagged for expansion , and create new gray nodes for each connection
    var results = ArrayBuffer[BFSNode]()
    if (color == "GRAY") {
      for (c <- connections) {
        val newCharacterID = c
        val newDistance = distance + 1
        val newColor = "GRAY"
        if (targetCharacterID == c) {
          if (hitCounter.isDefined)
            hitCounter.get.add(1)
        }
        val newEntry: BFSNode = (newCharacterID, (Array(), newDistance, newColor))
        results += newEntry
      }
      color = "BLACK"
    }
    val thisEntry: BFSNode = (characterID, (connections, distance, color))
    results += thisEntry
    results.toArray
  }

  /**
    * Combine nodes for the same heroID, preserving he shortest length and darkest
    */
  def bfsReduce(data1: BFSData, data2: BFSData): BFSData = {

    // Extract
    val edges1: Array[Int] = data1._1
    val edges2: Array[Int] = data2._1

    val distance1 = data1._2
    val distance2 = data2._2

    val color1 = data1._3
    val color2 = data2._3

    // Default node values
    var distance = 9999
    var color = "WHITE"
    var edges = ArrayBuffer[Int]()

    if (edges1.length > 0) edges ++= edges1
    if (edges2.length > 0) edges ++= edges2

    distance = math.min(distance1, distance)
    distance = math.min(distance2, distance)

    if (color1 == "WHITE" && (color2 == "GRAY" || color2 == "BLACK"))
      color = color2
    if (color1 == "GRAY" && color2 == "BLACK")
      color = color2
    if (color2 == "WHITE" && (color1 == "GRAY" || color1 == "BLACK"))
      color = color1
    if (color2 == "GRAY" && color1 == "BLACK")
      color = color1

    (edges.toArray, distance, color)
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local[*]", "DegreesOfSeparation")

    // Our accumulator, used to signal when we find the target
    //character in our BFS traversal.
    hitCounter = Some(sc.longAccumulator("Hit Counter"))

    var iterationRdd = createStartingRdd(sc)

    for (iteration <- 1 to 20) {
      println(s"Running BFS Iteration# $iteration")

      val mapped = iterationRdd.flatMap(bfsMap)

      println(s"Processing ${mapped.count()} values.")

      if (hitCounter.isDefined) {
        val hitCount = hitCounter.get.value
        if (hitCount > 0) {
          println(s"Hit the target character! From $hitCount different direction(s).")
        }
      }

      // Reducer combines data for each character ID, preserving the darkest color and shortest path
      iterationRdd = mapped.reduceByKey(bfsReduce)

    }
    sc.stop()
  }
}
