import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

/*
 * Learning from
 * https://github.com/47deg/annotate-your-case-classes/blob/master/modules/macros/src/main/scala/com/fortysevendeg/macros/ToStringObfuscateImpl.scala
 */
class CCFields extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro CCMacros.impl
}

//excercise 1
// CCFields should generate trait with object $NameMeta containing just val $fieldName} = "String"

object CCMacros {

  /* for given case class Entity produces EntityMeta
   */

  def impl(c:whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def extractCaseClassesParts(classDecl: ClassDef) = classDecl match {
      case q"case class $className(..$fields) extends ..$parents { ..$body }" =>
        (className, fields, parents, body)
    }

    def modifiedDeclaration(classDecl: ClassDef) = {
      val (className, fields, parents, body) = extractCaseClassesParts(classDecl)

      val newFields = fields.asInstanceOf[List[ValDef]] map { p =>
        ValDef(Modifiers(),p.name,tq"String",q"${p.name.toString}")
      }

      val newName = TypeName(className.toString()+"Meta")

      val ex = c.Expr[Any](
        q"""
        $classDecl

        trait $newName {
          ..$newFields
        }
      """
      )
      println(ex.toString())
      ex
    }

    annottees.map (_.tree) match {
      case (classDecl: ClassDef) :: Nil => modifiedDeclaration(classDecl)
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }

  }

}
