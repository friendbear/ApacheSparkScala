package local.spark.core

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

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

    val flipped = totalByCustomer.map(x => (x._2, x._1))
    val totalByCustomerSorted = flipped.sortByKey()

    val results = totalByCustomerSorted.collect()

    results.foreach {println _}

    sc.stop()
  }
}
