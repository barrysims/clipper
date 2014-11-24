package com.example

/**
 * Example doc
 *
 * TODO: Expand this src file
 */
trait ExTrait {
  // Example comment
  def exDef(): String = "hello"
}

class ExClass extends ExTrait {
  var exVar = "hello"
  val exVal = (a: Int, b: Int) => a * b
}

object ExObject {
  def apply() = new ExClass()
}

