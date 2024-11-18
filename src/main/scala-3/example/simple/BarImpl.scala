package example.simple

import MySimpleADT.Bar

/** Error: class Bar needs to be abstract. Missing implementation for:
 *         def combine: String // inherited from trait MySimpleAD
 */
// private[simple] trait BarImpl { self: Bar =>
// }

private[simple] trait BarImpl:
  self: Bar =>

  override final def combine: String =
    s"Bar: ${self.a} | ${self.b}"
end BarImpl
