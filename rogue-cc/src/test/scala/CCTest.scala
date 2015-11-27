import org.bson.types.ObjectId
import org.junit.Test
import org.specs2.matcher.JUnitMustMatchers





class CCTest extends JUnitMustMatchers {

 @Test
 def testExpansion: Unit = {
  @CCFields
  case class CCEntry(
                      _id: ObjectId,
                      name :String,
                      age: Option[Int])


  //CCEntry(new ObjectId(), "ALA", Some(1))
  object OCC extends CCEntryMeta
  println(s"OCC Age ${OCC.age}")
  println("CCEntry Meta age")
  // I want to have:
  //CCEntryMeta.age

  println("done")
 }





}
