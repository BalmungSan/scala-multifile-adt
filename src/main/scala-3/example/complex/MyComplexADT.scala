package example.complex

private[complex] abstract trait MyComplexADTRoot (flag: Boolean = false):
  self: MyComplexADT =>

  def combine(other: MyComplexADT): MyComplexADT =
    other match {
      case bar @ MyComplexADT.Bar(_, _) => if (flag) bar else this
      case _ => this
    }
end MyComplexADTRoot

enum MyComplexADT:
  self: MyComplexADTRoot =>

  case Foo(x: Int, y: Int) extends MyComplexADT with MyComplexADTRoot(y > x) with FooImpl
  case Bar(a: String, b: String) extends MyComplexADT with MyComplexADTRoot() with BarImpl
end MyComplexADT
