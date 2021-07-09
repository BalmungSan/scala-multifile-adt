package example.simple

sealed trait MySimpleADT[A] extends Product with Serializable {
  protected final val magicNumber: Int = 1

  def combine: A
}

final case class Foo(x: Int, y: Int) extends MySimpleADT[Int] with FooImpl
final case class Bar(a: String, b: String) extends MySimpleADT[String] with BarImpl
