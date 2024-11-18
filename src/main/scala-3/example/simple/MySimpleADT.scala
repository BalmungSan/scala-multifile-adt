package example.simple

enum MySimpleADT[A]:
  protected final val magicNumber: Int = 1

  def combine: A

  case Foo(x: Int, y: Int) extends MySimpleADT[Int] with FooImpl
  case Bar(a: String, b: String) extends MySimpleADT[String] with BarImpl
