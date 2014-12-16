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
   * @param src The scala source, as a String
   * @param offset The current cursor position
   * @return A list of in-scope identifiers
   */
  def apply(src: String, offset: Int) = {
    kleisli(parse) >=>
    kleisli(nodeToTree ∘ findInScopeNodes(offset)) >=>
    kleisli(nodesToIdentifiers)
  }.apply(src) some {_.flatten.distinct} none Nil

  private lazy val parse = (src: String) => ScalaParser.parse(src)

  private lazy val findInScopeNodes = (offset: Int) => (tree: Tree[AstNode]) => {
    lazy val inRange = (r: Option[Range]) => r some (_.contains(Range(offset, 0))) none false

    def find(child: Tree[AstNode]): Boolean = child.rootLabel match {
      case _: TmplDef | _: IfExpr | _: CompilationUnit => inRange(child.rootLabel.rangeOpt)
      case _ => false
    }
    @tailrec
    def nodeAtOffset(z: TreeLoc[AstNode]): Option[TreeLoc[AstNode]] = {
      z.findChild (find) match {
        case Some(c) => nodeAtOffset(c)
        case None => Some(z)
      }}

    val node = nodeAtOffset(tree.loc)

    node ∘ { n => (children(n.getLabel) ++ (n.parents.toList ∘ (p => children(p._2))).flatten).reverse }
  }

  private def children(n: AstNode): List[AstNode] = {
    nodeToTree(n).subForest ∘ (_.rootLabel)
  }.toList

  private lazy val nodeToTree = (node: AstNode) => {
    def treeRec(n: AstNode): List[Tree[AstNode]] = {
      filterNode(n) match {
        case Some(x) => List(x.node(x.immediateChildren ∘ treeRec flatten: _*))
        case None => n.immediateChildren ∘ treeRec flatten
      }
    }
    treeRec(node) head
  }

  private def filterNode(n: AstNode): Option[AstNode] = {

    lazy val nodeSel = (pf: PartialFunction[AstNode, AstNode]) => (x: AstNode) => x.immediateChildren.collectFirst(pf)
    lazy val findNode = kleisli(nodeSel { case x: Expr => x }) >=> kleisli(nodeSel { case x: GeneralTokens => x })

    n match {
      case _: FunDefOrDcl | _: TmplDef | _: IfExpr | _: CompilationUnit => Some(n)
      case _: PatDefOrDcl => findNode(n)
      case _ => None
    }
  }

  private lazy val nodesToIdentifiers = (nodes: List[AstNode]) => Option(nodes ∘ nodeToId)


  private def nodeToId(node: AstNode) = node match {
    case _: PackageBlock | _: PackageStat | _: ImportExpr | _: ImportClause | _: ImportSelectors => None
    case _ => peer(node).collectFirst { case t: Token if "VARID" == t.tokenType.name => t.text }
  }

  private def peer(n: AstNode) = n.tokens.filter { t => !n.immediateChildren.flatMap(_.tokens).contains(t)}

  object Debug {

    def showAst(src: String): String = draw(treeRec(parse(src).get).head)

    private def treeRec(n: AstNode): List[Tree[AstNode]] = List(n.node(n.immediateChildren ∘ treeRec flatten: _*))

    private def draw(t: Tree[AstNode]) = {
      implicit val AstNodeShow = new Show[AstNode] {
        override def shows(n: AstNode) = showAstNode(n)
      }
      t.drawTree
    }

    private def showAstNode(n: AstNode) = {
      val showRange = (r: Range) => s"${r.offset} ${r.offset + r.length}"
      s"${n.getClass.getSimpleName}: ${nodeToId(n) | "_"} ${n.rangeOpt some showRange none "_"}"
    }

    private def debugTree(t: Tree[AstNode]) = {
      println(draw(t)); t
    }
  }
}
