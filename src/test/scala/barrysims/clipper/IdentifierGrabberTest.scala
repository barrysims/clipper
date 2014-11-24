package barrysims.clipper

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import scala.collection.JavaConverters._

import scala.io.Source

class IdentifierGrabberTest extends FlatSpec with Matchers {

  val src: String = Source.fromURL(getClass.getResource("/Example.scala")).mkString

  "IdentfierGrabber" should "find val identifiers" in {
    IdentifierGrabber(src, List("VAL")) should equal (List("exVal").asJava)
  }

  it should "find var identifiers" in {
    IdentifierGrabber(src, List("VAR")) should equal (List("exVar").asJava)
  }

  it should "find object identifiers" in {
    IdentifierGrabber(src, List("OBJECT")) should equal (List("ExObject").asJava)
  }

  it should "find class identifiers" in {
    IdentifierGrabber(src, List("CLASS")) should equal (List("ExClass").asJava)
  }

  it should "find trait identifiers" in {
    IdentifierGrabber(src, List("TRAIT")) should equal (List("ExTrait").asJava)
  }

  it should "find def identifiers" in {
    IdentifierGrabber(src, List("DEF")) should equal (List("exDef").asJava)
  }
}

