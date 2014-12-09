package com.example

import barrysims.clipper.IdentifierGrabber

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
  if (true) {
    val ifVar = 1
  } else {
    val elseVar = 1
  }
}

object ExObject {
  def apply() = new ExClass()
}

