package barrysims.clipper

import scala.collection.mutable
import scalariform.lexer.Token
import scalariform.parser.ScalaParser
import scala.collection.JavaConversions._

/**
 * Scans a scala source file and generates a list of the identifiers used in the file.
 *
 * The list is sorted by occurrence.
 */
object IdentifierGrabber {

  val useful = List("OBJECT", "CLASS", "TRAIT", "DEF", "VAL", "VAR")
  val filteredIdentifiers = List("apply")

  def apply(src: String): java.util.List[String] = apply(src, useful)

  def apply(src: String, usefulNodes: List[String]): java.util.List[String] = {

    val ast = ScalaParser.parse(src)
    val tokenMap = new TokenMap()

    ast map { a =>
      val iter = a.tokens.iterator
      while (iter.hasNext) {
        iter.next() match {
          case t: Token if usefulNodes.contains(t.tokenType.name) => tokenMap.add(iter.next().text)
          case _ =>
        }
      }
      tokenMap.toList.sortBy(_._2).map(_._1).
        filter(!filteredIdentifiers.contains(_))
    }
  }.getOrElse(Nil).toList
}

private class TokenMap extends mutable.HashMap[String, Int]() {
  def add(item: String): Unit = {
    val number = if (this.contains(item)) this(item) + 1 else 1
    this += (item -> number)
  }
}
