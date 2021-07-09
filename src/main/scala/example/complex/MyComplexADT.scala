package example.complex

private[complex] abstract class MyComplexADTRoot (flag: Boolean = false) { self: MyComplexADT =>
  def combine(other: MyComplexADT): MyComplexADT =
    other match {
      case bar @ Bar(_, _) => if (flag) bar else self
      case _ => self
    }
}

sealed trait MyComplexADT extends Product with Serializable { self: MyComplexADTRoot =>
}

final case class Foo(x: Int, y: Int) extends FooImpl(y > x) with MyComplexADT
final case class Bar(a: String, b: String) extends BarImpl with MyComplexADT
