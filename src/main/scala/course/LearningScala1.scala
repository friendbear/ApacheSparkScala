package course

import scala.util.matching.Regex

object LearningScala1 {

  def main(args: Array[String]): Unit = {

    val hello: String = "Hola!"
    println(hello)

    var helloThere: String = hello
    helloThere = hello + " There!"
    println(helloThere)

    val immutableHelloThere: String = hello + "There!"
    println(immutableHelloThere)

    val numberOne: Int = 1

    val truth: Boolean = true
    val letterA: Char = 'a'
    val pi: Double = scala.math.Pi
    val piSinglePrecision: Float = scala.math.Pi.toFloat
    val bigNumber: Long = 123L
    val smallNumber: Byte = 127

    println("Here is a mess: " + numberOne + truth + letterA + pi + bigNumber)

    println(f"Pi is about $piSinglePrecision%.3f")
    println(f"Zero padding on the left: $numberOne%05d")

    println(s"I can use the s prefix to use variables like $numberOne $truth $letterA")

    println(s"The s prefix isn't limited to variables; Ican include any expression. Like ${1+2}")

    val theUltimateAnswer: String = "To life, the universe, and everything is 42."

    val pattern: Regex = """.*([\d]+).*""".r
    val pattern(answerString) = theUltimateAnswer
    val answer = answerString.toInt
    println(s"answer: $answer")

    val isGreater = 1 > 2 //> false
    val isLesser = 1 < 2  //> true
    val impossible = isGreater & isLesser  //> false
    val anotherWay = isGreater && isLesser //> false

    val picard: String = "Picard"
    val bestCaptain: String = "Picard"
    val isBest: Boolean = picard == bestCaptain

    println(f"Three decimal ${scala.math.Pi}%.3f")
  }
}
