package barrysims.clipper

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.language.postfixOps

import scalariform.lexer.Token
import scalariform.parser._
import scalariform.utils.Range

import scalaz._
import Scalaz._
import scalaz.TreeLoc.{Parent, TreeForest}
import scalaz.Kleisli._

/**
 * Scans a scala source file and generates a list of the identifiers used in the file.
 *
 * The list is sorted by occurrence.
 */
object IdentifierGrabber {

  def asJava(src: String, offset: Int): java.util.List[String] = apply(src, offset)

  /**
   * Kleislis, for the hell of it
   *
   * @param src The scala source, as a String
   * @param offset The current cursor position
   * @return A list of in-scope identifiers
   */
  def apply(src: String, offset: Int) = {
    kleisli(parse) >=>
    kleisli(astToTree ∘ findInScopeNodes(offset)) >=>
    kleisli(nodesToIdentifiers)
  }.apply(src) some {_.flatten.distinct} none Nil

  private lazy val parse = (src: String) => ScalaParser.parse(src)

  private lazy val astToTree = (node: AstNode) => {
    val t = treeRec(node).head
    println(draw(t))
    t
  }

  private lazy val findInScopeNodes = (offset: Int) => (tree: Tree[AstNode]) => {
    lazy val inRange = (r: Range) => r.contains(Range(offset, 0))
    @tailrec
    def recChild(z: TreeLoc[AstNode]): Option[TreeLoc[AstNode]] = {
      z.findChild(_.rootLabel.rangeOpt some inRange none false) match {
        case Some(c) => recChild(c)
        case None => Some(z)
      }}
    recChild(tree.loc) ∘ (_.parents.toList ∘ siblings flatten)
  }

  private lazy val nodesToIdentifiers = (nodes: List[AstNode]) => Option(nodes ∘ nodeToId)

  private def treeRec(n: AstNode): List[Tree[AstNode]] = List(n.node(n.immediateChildren ∘ treeRec flatten: _*))

  private def siblings[A](p: Parent[A]) = {
    val tf2l = (tf: TreeForest[A]) => tf.toStream ∘ (_.rootLabel)
    tf2l(p._1) ++ Stream(p._2) ++ tf2l(p._3)
  }

  private def nodeToId(node: AstNode) = node match {
    case _: PackageBlock | _: PackageStat | _: ImportExpr | _: ImportClause | _: ImportSelectors => None
    case _ => peer(node).collectFirst { case t: Token if "VARID" == t.tokenType.name => t.text }
  }

  private def peer(n: AstNode): List[Token] = n.tokens.filter { t => !n.immediateChildren.flatMap(_.tokens).contains(t)}

  private def draw(t: Tree[AstNode]): String = {
    val showRange = (r: Range) => s"${r.offset} ${r.offset + r.length}"
    implicit val AstNodeShow = new Show[AstNode] {
      override def shows(n: AstNode) =
        s"${n.getClass.getSimpleName}: ${nodeToId(n) | "_"} ${n.rangeOpt some showRange none "_"}"
    }
    t.drawTree
  }
}
