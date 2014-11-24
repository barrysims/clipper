clipper
=======

Intellij plugin that scans a scala source file and copies all identifiers to the clipboard history.

For example, running the plugin on Example.scala:

```
package com.example

/**
 * Example doc
 */
trait ExTrait {
  // Example comment
  def exDef(): String = "hello"
}

class ExClass extends ExTrait {
  var exVar  = "hello"
  val exVal = (a: Int, b: Int) => a * b
}

object ExObject {
  def apply() = new ExClass()
}
```
will copy ExTrait, exDef, ExClass, exVar, exVal and ExObject to the clipboard.

Usage
-----

shift ctrl H, or Code->Clip Identifiers

Build
-----

A bit of a mish-mash, I'm afraid.  I imagine all of this could be replaced by a simple sbt assembly step, but for now:

1. Do not import sbt project into idea.
2. Run sbt assembly to package the scala source into lib/clipper-scala.jar
3. Make sure that intellij is set up to look in action-src for java source files
4. Run build->Prepare Plugin Module 'clipper' for Deployment
5. Deploy using settings->plugins->load plugin from disk

TODO
----

Examine cursor position when action is invoked, and only clip identifiers that are in scope, and prioritise using scope.




