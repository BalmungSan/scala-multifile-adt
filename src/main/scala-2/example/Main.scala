package example

object Main {
  def testSimple(): Unit = {
    import simple._
    println("Simple test begins!")

    val foo = Foo(x = 3, y = 5)
    println(foo.plus(n = 10).combine)

    /** Error: match may not be exhaustive:
     *         It would fail on the following input: Bar(_, _)
     */
    // (foo : MySimpleADT) match {
    //   case Foo(_, _) =>
    // }

    /** Error: not found: type FooImpl */
    // new FooImpl { }

    println("Simple test ends!")
  }

  def testComplex(): Unit = {
    import complex._
    println("Complex test begins!")

    val foo = Foo(x = 0, y = 1)
    val bar = Bar(a = "A", b = "B")

    println(foo.combine(bar))
    println(bar.combine(foo))
    println(foo.combine(foo))
    println(bar.combine(bar))

    /** Error: match may not be exhaustive:
     *         It would fail on the following input: (Bar(_, _), Foo(_, _))
     */
    // ((foo : MyComplexADT), (bar : MyComplexADT)) match {
    //   case (Foo(_, _), Foo(_, _)) =>
    //   case (Foo(_, _), Bar(_, _)) =>
    //   case (Bar(_, _), Bar(_, _)) =>
    // }

    /** Error: not found: type FooImpl */
    // new FooImpl

    /** Error: not found: type MyComplexADTRoot */
    // new MyComplexADTRoot

    println("Complex test ends!")
  }

  def main(args: Array[String]): Unit = {
    testSimple()
    println("-----")
    testComplex()
  }
}
