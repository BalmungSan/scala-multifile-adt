package example.simple

import MySimpleADT.Foo

private[simple] trait FooImpl:
  self: Foo =>

  def plus(n: Int): Foo =
    self.copy(x = self.x + n, y = self.y + n)

  override final def combine: Int =
    self.x + self.y + self.magicNumber
end FooImpl
