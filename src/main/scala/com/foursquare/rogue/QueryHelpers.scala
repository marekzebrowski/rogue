// Copyright 2011 Foursquare Labs Inc. All Rights Reserved.

package com.foursquare.rogue

import com.mongodb.DBObject
import net.liftweb.json.{Extraction, Formats, Serializer, TypeInfo}
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.mongodb._
import net.liftweb.mongodb.record._

case class Degrees(value: Double)
case class LatLong(lat: Double, long: Double)

trait HasMongoForeignObjectId[RefType <: MongoRecord[RefType] with MongoId[RefType]]

object QueryHelpers {
  class DBObjectSerializer extends Serializer[DBObject] {
    val DBObjectClass = classOf[DBObject]

    def deserialize(implicit formats: Formats): PartialFunction[(TypeInfo, JValue), DBObject] = {
      case (TypeInfo(klass, _), json : JObject) if DBObjectClass.isAssignableFrom(klass) =>
        JObjectParser.parse(json)
    }

    def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
      case x: DBObject =>
        JObjectParser.serialize(x)
    }
  }

  private implicit val formats =
    (net.liftweb.json.DefaultFormats + new ObjectIdSerializer + new DBObjectSerializer)

  trait QueryLogger {
    def log(command: MongoCommand[_], timeMillis: Long) {
      // Default implementation, for backwards compatibility until we remove the deprecated log() method.
      log(command.toString, timeMillis)
    }

    @deprecated("Replaced by structured logging") def log(msg: => String, timeMillis: Long): Unit = {}
    @deprecated("Unused") def warn(msg: => String): Unit = {}
  }

  class DefaultQueryLogger extends QueryLogger {
    override def log(command: MongoCommand[_], timeMillis: Long) {}
    @deprecated("Replaced by structured logging") override def log(msg: => String, timeMillis: Long) {}
    @deprecated("Unused") override def warn(msg: => String) {}
  }

  object NoopQueryLogger extends DefaultQueryLogger

  var logger: QueryLogger = NoopQueryLogger

  trait QueryValidator {
    def validateList[T](xs: Traversable[T]): Unit
    def validateRadius(d: Degrees): Degrees
    def validateQuery[M <: MongoRecord[M]](query: BasicQuery[M, _, _, _, _, _, _]): Unit
    def validateModify[M <: MongoRecord[M]](modify: ModifyQuery[M]): Unit
    def validateFindAndModify[M <: MongoRecord[M], R](modify: FindAndModifyQuery[M, R]): Unit
  }

  class DefaultQueryValidator extends QueryValidator {
    override def validateList[T](xs: Traversable[T]) {}
    override def validateRadius(d: Degrees) = d
    override def validateQuery[M <: MongoRecord[M]](query: BasicQuery[M, _, _, _, _, _, _]) {}
    override def validateModify[M <: MongoRecord[M]](modify: ModifyQuery[M]) {}
    override def validateFindAndModify[M <: MongoRecord[M], R](modify: FindAndModifyQuery[M, R]) {}
  }

  object NoopQueryValidator extends DefaultQueryValidator

  var validator: QueryValidator = NoopQueryValidator

  def makeJavaList[T](sl: Traversable[T]): java.util.List[T] = {
    val list = new java.util.ArrayList[T]()
    for (id <- sl) list.add(id)
    list
  }

  def list[T](vs: Traversable[T]): java.util.List[T] = {
    validator.validateList(vs)
    makeJavaList(vs)
  }

  def list(vs: Double*): java.util.List[Double] = list(vs)

  def radius(d: Degrees) = {
    validator.validateRadius(d).value
  }

  def makeJavaMap[K, V](m: Map[K, V]): java.util.Map[K, V] = {
    val map = new java.util.HashMap[K, V]
    for ((k, v) <- m) map.put(k, v)
    map
  }

  def inListClause[V](fieldName: String, vs: Traversable[V]) = {
    if (vs.isEmpty)
      new EmptyQueryClause[java.util.List[V]](fieldName)
    else
      new QueryClause(fieldName, CondOps.In -> QueryHelpers.list(vs))
  }

  def allListClause[V](fieldName: String, vs: Traversable[V]) = {
    if (vs.isEmpty)
      new EmptyQueryClause[java.util.List[V]](fieldName)
    else
      new QueryClause(fieldName, CondOps.All -> QueryHelpers.list(vs))
  }

  def asDBObject[T](x: T): DBObject = {
    JObjectParser.parse(Extraction.decompose(x).asInstanceOf[JObject])
  }

  def orConditionFromQueries(subqueries: List[BasicQuery[_, _, _, _, _, _, _]]) = {
    MongoHelpers.OrCondition(subqueries.flatMap(subquery => {
      subquery match {
        case q: BasicQuery[_, _, _, _, _, _, _] => Some(q.condition)
        case _ => None
      }
    }))
  }

}
