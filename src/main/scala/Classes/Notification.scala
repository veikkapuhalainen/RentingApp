package Classes

import scala.collection.mutable

case class Notification(name: String, publisher: User, pricePerDay: Double, pricePerHour: Double, description: String, category: Category):


  val comments = mutable.Buffer[String]()
  val damages = mutable.Buffer[String]()

  def addComment(description: String): Unit =
    this.comments += description
    
  def deleteNotification(): Unit = ???

  override def toString: String = s"Title: $name, Publisher: $publisher, Price(day): ${pricePerDay}e, Price(hour): ${pricePerHour}e, " +
    s"Desc: $description, Category: $category"
end Notification
