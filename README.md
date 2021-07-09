# Scala multi-file ADTs

This repo contains two small demos of how to be able to split an **ADT** into multiple file,
without loosing important properties like exhaustive pattern matching checks.

## Disclaimer

First of all, note that I am sharing those two patterns mostly as an academic exercise,
this means that I do not recommend using those two approaches:

+ First, because I think one should _(almost)_ never need to have
an **ADT** so big for one to care about how to split it into files-
Since this usually means your **ADT** is not just data, but behaviour;
something I consider a design error.

+ Additionally, and most importantly, because I don't think this is _(yet?)_
a best practice on how to tackle this problem;
mainly because I haven't used this on a real project.

Nevertheless, I am posting this because there are always exceptions and
valid use cases for having a big **ADT** and
I hope this would help you if you are in such a situation.
Also, because there can't be best practices without doing something bad first.
Thus, if you follow this approach and found some problems; and
even better managed to overcome them,
please do not hesitate in opening an issue / pull request.

Secondly, the _complex_ demo was ~~stolen~~ inspired by
[the work of @jimka](https://users.scala-lang.org/t/refactoring-class-hierarchy-into-adt/6997)!

## Guide

### Introduction

This repo is just a guide of how to implement a pattern I called multi-line **ADT**.<br>
The rest of this README will be guide itself,
whereas the source code serves as an example as well to validate that the pattern works.

The pattern is divided into two sections: _simple_ and _complex_.
Those are not the best names _(I am very bad a naming)_,
but they convey two different intentions.<br>
The idea is that _simple_ is what I expect most people would use,
it ensures that most basic / common functionality of a traditional single file **ADT** is retained.<br>
Whereas _complex_ tries to take that even further by adding more **OOP** capabilities,
like constructor arguments, linearization, overriding concrete methods and calling super methods
_(if you need this you probably should rethink your design, but whatever floats your boat)_.

### _Simple_

**Goals:**

- [X] 1. The **ADT** logic can be split across multiple files.
- [X] 2. Exhaustive pattern matching checks are preserved.
- [X] 3. Implementations can access properties / methods of their corresponding product type.
- [X] 4. Implementations can access shared properties / methods of the root `trait`.
- [X] 5. Implementations can implement abstract methods of the root `trait`.
- [X] 6. Minimize the possibility of mixing implementations by mistake.
- [X] 7. The compiler errors if a member of the **ADT** doesn't implement an abstract method of the root `trait`.
- [X] 8. Implementation details are not exposed. i.e.
         Users should not notice any difference with a regular **ADT**.

This pattern is actually quite simple to implement.<br>
The idea is simple instead of having something like this:

```scala
sealed trait MyADT {
  // MyADT declaration.
}

final case class Foo(x: Int, y: Int) extends MyADT {
  // Foo implementation.
}

final case class Foo(a: String, b: String) extends MyADT {
  // Bar implementation.
}
```

We well do something like this instead:

```scala
sealed trait MyADT {
  // MyADT declaration.
}

final case class Foo(x: Int, y: Int) extends MyADT with FooImpl
final case class Bar(a: String, b: String) extends MySimpleADT[String] with BarImpl

trait FooImp {
  // Foo implementation.
}

trait BarImp {
  // Bar implementation.
}
```

That way we can move the `Impl` traits into their own files,
which would guarantee objetive `1`.<br>
Moreover, we can test that it also guarantees objective `2`.

```scala
// If we omit a case in the pattern match:
val data: MyADT = Foo(x = 3, y = 5)
data match {
  case Foo(_, _) => ???
}
// Then, we will get the following compile error:
```

> match may not be exhaustive:<br>
> It would fail on the following input: Bar(_, _)

Cool!<br>
Now, let's see if we can also satisfy goals: `3`, `4` & `5`.

First let's spicy the definition of `MyADT` a little:

```scala
sealed trait MyADT[A] {
  protected final val magicNumber: Int = 1

  def combine: A
}

final case class Foo(x: Int, y: Int) extends MyADT[Int] with FooImpl
final case class Bar(a: String, b: String) extends MyADT[String] with BarImpl
```

Then let's see if we can:

3. Access properties like `x` and `y` and the `copy` method inside `FooImpl`
4. Access the shared property `magicNumber` inside `FooImpl`
5. Implement the `combine` abstract method inside `FooImpl`

Let's get into it!<br>
It turns out that we can do all that by just using a
somewhat standard feature of the language:
[**self-types**](https://docs.scala-lang.org/tour/self-types.html).

```scala
trait FooImpl { self: Foo =>
  def plus(n: Int): Foo =
    self.copy(x = self.x + n, y = self.y + n)

  override final def combine: Int =
    self.x + self.y + self.magicNumber
}
```

And that would be it!<br>
We can test it works as expected:

```scala
// Given:
val foo = Foo(x = 3, y = 5)
// Then:
foo.plus(n = 10).combine
// Returns:
29
```

Additionally, using self types give us also give us goal `6` out of the box.
Since now `FooImpl` has to be mixed in into something that also extends `Foo`,
and because `Foo` is `final` then it can only be mixed in into `Foo` itself
_(of course, one may still use the incorrect self-type like `Bar` in this case)_.

Great, we are almost there!<br>
Consequently, it happens that goal `7` is provided by the compiler out of the box.

```scala
// If we do not provide an implementation for combine in BarImpl:
trait BarImpl { self: Bar =>
}
// Then, we will get the following compile error:
```
> class Bar needs to be abstract. Missing implementation for:<br>
> def combine: String // inherited from trait MySimpleAD

Finally, for goal `8` we only need to move all the files to their own `package`
and mark all the implementation traits as `private[pckg]`
and then they will be invisible to users.
// We can also easily test that like this:

```scala
// If we try to access the FooImpl class:
import some.pckg._
new FooImpl
// Then, we will get the following compile error:
```

> not found: type FooImpl

#### Summary

Putting it all together the _simple_ multi-file **ADT** pattern is as follows:

```scala
// file: some/pckg/MyADT.scala ------------------------------------------------
package some.pckg

sealed trait MyADT {
  // MyADT declaration.
}

final case class Foo(x: Int, y: Int) extends MyADT with FooImpl
final case class Bar(a: String, b: String) extends MyADT with BarImpl
// ----------------------------------------------------------------------------


// file: some/pckg/FooImpl.scala ----------------------------------------------
package some.pckg

private[pckg] trait FooImpl { self: Foo =>
  // Foo implementation.
}
// ----------------------------------------------------------------------------


// file: some/pckg/BarImpl.scala ----------------------------------------------
package some.pckg

private[pckg] trait BarImpl { self: Bar =>
  // Foo implementation.
}
// ----------------------------------------------------------------------------
```

### _Complex_

**Goals:**

- [X] 0. All the sames goals as the _simple_ one.
- [X] 1. The root of the **ADR** can have constructor arguments.
- [X] 2. Implementations can override concrete methods of the root `abstract class`.
- [X] 3. Implementations can call super methods of the root `abstract class`.

This pattern requires a little bit of extra trickery.<br>
The main idea is the same as before,
being able to split the **ADT** into multiple file,
But this time, we also want to make use of traditional **OOP** tricks;
like overriding a method or calling `super`.

First, let's define our original **ADT** in a single file:

```scala
sealed abstract class MyADT (flag: Boolean = false) {
  def combine(other: MyADT): MyADT =
    other match {
      case bar @ Bar(_, _) => if (flag) bar else this
      case _ => this
    }
}

final case class Foo(x: Int, y: Int) extends MyADT(y > x) {
  override def combine(other: MyADT): MyADT =
    super.combine(other) match {
      case Foo(xx, yy) => Foo(self.x + xx, self.y + yy)
      case Bar(a, b) => Bar(a * self.x, b * self.y)
    }
}

final case class Bar(a: String, b: String) extends MyADT
```

If we try to apply the same pattern as before we will quickly notice that
calling the constructor of the `abstract class` and ensuring
the correct linearization order can be tricky.

To fix that we need to introduce an intermediate `trait`
plus a couple of **self-types**:

```scala
private[pckg] abstract class MyADTRoot (flag: Boolean = false) { self: MyADT =>
  def combine(other: MyADT): MyADT =
    other match {
      case bar @ Bar(_, _) => if (flag) bar else self
      case _ => self
    }
}

sealed trait MyADT extends Product with Serializable { self: MyADTRoot =>
}
```

And then the implementations `traits` need to be also `abstract classes`
as well as extending the root `abstract class`:

```scala
private[pckg] abstract class FooImpl(flag: Boolean) extends MyADTRoot(flag) { self: Foo =>
  def plus(n: Int): Foo =
    self.copy(x = self.x + n, y = self.y + n)

  override def combine(other: MyADT): MyADT =
    super.combine(other) match {
      case Foo(xx, yy) => Foo(self.x + xx, self.y + yy)
      case Bar(a, b) => Bar(a * self.x, b * self.y)
    }
}
```

Finally, the leaves of the **ADT** would look like:

```scala
final case class Foo(x: Int, y: Int) extends FooImpl(y > x) with MyADT
final case class Bar(a: String, b: String) extends BarImpl with MyADT
```

We can test the correct linearization order as follows:

```scala
// Given:
val foo = Foo(x = 0, y = 1)
val bar = Bar(a = "A", b = "B")
// Then:
foo.combine(bar)
bar.combine(foo)
foo.combine(foo)
bar.combine(bar)
// Returns (respectively):
Bar("", "B")
Bar("A", "B")
Foo(0, 2)
Bar("A", "B")
```

We can also do the same experiments as before
to ensure exhaustive pattern matching and
that the implementation `classes` are not visible to users.<br>
Thus we can say that we are done!

#### Summary

Putting it all together the _complex_ multi-file **ADT** pattern is as follows:

```scala
// file: some/pckg/MyADT.scala ------------------------------------------------
package some.pckg

private[pckg] abstract class MyADTRoot (...) { self: MyADT =>
  // MyADT declaration.
}

sealed trait MyADT { self => MyADTRoot =>
}

final case class Foo(x: Int, y: Int) extends FooImpl(...) with MyADT
final case class Bar(a: String, b: String) extends BarImpl(...) with MyADT
// ----------------------------------------------------------------------------


// file: some/pckg/FooImpl.scala
package some.pckg

private[pckg] abstract class FooImpl (...) extends MyADTRoot(...) { self: Foo =>
  // Foo implementation.
}
// ----------------------------------------------------------------------------

// file: some/pckg/BarImpl.scala
package some.pckg

private[pckg] abstract class BarImpl (...) extends MyADTRoot(...) { self: Bar =>
  // Foo implementation.
}
// ----------------------------------------------------------------------------
```

## Final words

Ok that is all folks, hope you found it either helpful or interesting.<br>
I am sorry for the names and code examples for being too artificial.

If you end up using this in a real project, please let me know how it went.

-- Luis Miguel Mejía Suárez _(@BalmungSan)_
