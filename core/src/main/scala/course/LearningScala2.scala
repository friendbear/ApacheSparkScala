package course

class LearningScala2 {

  if (1 > 3) println("Impossible!") else println("The world makes sense.")

  if (1 > 3) {
    println("Impossible!")
  } else {
    println("The world makes sense.")
  }

  val number = 3
  number match {
    case 1 => println("One")
    case _ => println("Something else")
  }

  for (x <- 1 to 4) {
    val squared = x * x
    println(squared)
  }

  var x = 10
  while (x >= 0) {
    println(x)
    x = x - 1
  }

  do {println(s"//>$x"); x += 1} while (x <= 10)
}
