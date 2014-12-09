package barrysims.clipper

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.collection.JavaConverters._

import scala.io.Source

class IdentifierGrabberTest extends FlatSpec with Matchers {

  val src: String = Source.fromURL(getClass.getResource("/Example.scala")).mkString

  "Identifier Grabber" should "find val identifiers" in {
    val offset = 130
    println(src.substring(offset - 10, offset + 10))
    IdentifierGrabber(src, offset) should equal (List("ExTrait", "ExClass", "ExObject"))
  }
}

