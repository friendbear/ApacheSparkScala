package course

object LearningScala3 extends App {

  def squareIt(x: Int): Int = {
    x * x
  }

  def cubeIt(x: Int): Int = {x * x * x}

  println(squareIt(2))
  println(cubeIt(3))

  def transformInt(x: Int, f: Int => Int): Int = {
    f(x)
  }

  println(transformInt(2, squareIt))
  println(transformInt(3, cubeIt))

  println(transformInt(2, x => {val y = x * 2; y * y}))

}
