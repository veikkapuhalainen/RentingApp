package Classes

import scala.collection.mutable

class Notification(var name: String, var publisher: User, var pricePerDay: Double, var description: String, val categories: mutable.Buffer[Category]):

  val comments = mutable.Buffer[Comment]()
  val damages = mutable.Buffer[String]()
 /* var pic: Pic = Pic
  val reserved = Buffer[TimePeriod]()
  val calendar =  Calendar
  */

  def addComment(description: String): Unit =
    this.comments += Comment(description, this)

  override def toString: String = s"$name, $publisher, ${pricePerDay}e, $description, $categories"
end Notification
