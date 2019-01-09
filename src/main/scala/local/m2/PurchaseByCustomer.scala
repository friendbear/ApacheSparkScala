package local.m2

import scala.math._
import org.apache.spark._
import org.apache.log4j._
import org.apache.spark.SparkContext._

object PurchaseByCustomer {

  def extractCustomerPricePairs(line: String) = {
    val fields = line.split(",")
    val customerId = fields(0).toInt
    (customerId, fields(2).toFloat)
  }

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc = new SparkContext("local[*]", "PurchaseByCustomer")

    val lines = sc.textFile("resources/data/customer-orders.csv")

    val parsedLines = lines.map(extractCustomerPricePairs)

    val totalByCustomer = parsedLines.reduceByKey((x,y) => x + y)

    val results = totalByCustomer.collect()

    results.sorted.foreach {println _}

    sc.stop()
  }
}

