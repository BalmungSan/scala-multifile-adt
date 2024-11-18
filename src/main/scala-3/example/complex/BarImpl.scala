package example.complex

import MyComplexADT.Bar

private[complex] abstract trait BarImpl extends MyComplexADTRoot:
  self: Bar =>
end BarImpl
