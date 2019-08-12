package course

object LearningScala4 extends App {
  val captainStuff = ("Picard", "Enterprise-D", "NCC-1701-D")

  println(captainStuff)

  println(captainStuff._1)
  println(captainStuff._2)
  println(captainStuff._3)

  val picardsShip = "Picard" -> "Enterprise-D"
  println(picardsShip._2)

  val aBunchOfStuff = ("Kirk", 1964, true)


  val shopList = List("Enterprise", "Defiant", "Voyager", "Deep Space Nine")

  println(shopList(1))

  println(shopList.head)
  println(shopList.tail)

  for (shop <- shopList) {println(shop)}

  val numberList = 1 to 5 map(_.toInt)
  val sum = {
    numberList.reduce(_ + _)
  }
  println(sum)

  val iHateFives = numberList.filter(_ != 5)

  val moreNumbers = List(6, 7, 8)
  val lotsOfNumbers = numberList ++ moreNumbers
  val distinctValues = lotsOfNumbers.distinct
  val hasThree = iHateFives.contains(3)


  val shipMap = Map("Kirk" -> "Enterprise", "Picard" -> "Enterprise-D", "Sisko" -> "Deep Space Nine", "Janeway" -> "Voyager")

  val archersShip = util.Try(shipMap("Archer")) getOrElse "Unknown"

  println(archersShip)
}
