package example

object Main:
  def testSimple(): Unit =
    import simple._
    println("Simple test begins!")

    val foo = MySimpleADT.Foo(x = 3, y = 5)
    println(foo.plus(n = 10).combine)

    /** Error: match may not be exhaustive:
     *         It would fail on the following input: Bar(_, _)
     */
    // (foo : MySimpleADT[?]) match
    //  case MySimpleADT.Foo(_, _) =>

    /** Error: not found: type FooImpl */
    // new FooImpl { }

    println("Simple test ends!")
  end testSimple

  def main(args: Array[String]): Unit =
    testSimple()
  end main
end Main
