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
    import c.universe.definitions._

    def extractCaseClassesParts(classDecl: ClassDef) = classDecl match {
      case q"case class $className(..$fields) extends ..$parents { ..$body }" =>
        (className, fields, parents, body)
    }

    def modifiedDeclaration(classDecl: ClassDef) = {
      val (className, fields, parents, body) = extractCaseClassesParts(classDecl)

      def fieldObject(p: ValDef): ValDef = {

        println(s"RHS ${p.rhs}")
        println(s"TPT ${p.tpt}")
        println(s"TPE ${p.tpe}")
        p.tpt.foreach( t =>
          println(s"  Inn ${t}")
        )
        println(s"tpt.tpe ${p.tpt.tpe}")
        val tpt = p.tpt
        println(s"isTerm ${tpt.isTerm}")
        println(s"isDef ${tpt.isDef}")
        println(s"isType ${tpt.isType}")
        println(s"nonEMpty ${tpt.nonEmpty}")
        //println(s"nonEMpty ${tpt.}")

        p.tpt match {
          case tq"Int" => println("Int field")
          case tq"String" => println("String field")
          case tq"Option[_]" => println("Optional field")
          case _ => println("other field")
        }
  //      println("MEEE")
        println(s"P: $p")
        println(s"tpt ${p.tpt}")

    //    println(s"Type ${p.tpe}")
      //  println(s"$p")
        val fName = p.name


        //val fImpl = p.tpe match {
          //case IntTpe => ModuleDef
          //case _ =>

//        }
        //q"object $fName extends $fImpl"
        ValDef(Modifiers(),p.name,tq"String",q"${p.name.toString}")
      }
      //check if it is enough
      val newFields = fields.asInstanceOf[List[ValDef]] map { p => fieldObject(p)

        //ModuleDef(Modifiers(),TermName(p.name),Template())

      }

      val newName = TypeName(className.toString()+"Meta")

      //maybe put meta inside ?
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
