package example.complex

private[complex] abstract class FooImpl(flag: Boolean) extends MyComplexADTRoot(flag) { self: Foo =>
  def plus(n: Int): Foo =
    self.copy(x = self.x + n, y = self.y + n)

  override def combine(other: MyComplexADT): MyComplexADT =
    super.combine(other) match {
      case Foo(xx, yy) => Foo(self.x + xx, self.y + yy)
      case Bar(a, b) => Bar(a * self.x, b * self.y)
    }
}
